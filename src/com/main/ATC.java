package com.main;

import com.main.Gates;
import com.main.Planes;
import com.main.RefuelingTruck;
import com.main.data.Statistics;

import java.util.Queue;
import java.util.LinkedList;

public class ATC implements Runnable {
    // Data fields
    private final Statistics statistics;
    private boolean runwayFree = true;
    private int groundCount = 0;
    private final Queue<Integer> landingQueue = new LinkedList<>();
    private final Queue<Integer> takeoffQueue = new LinkedList<>();

    // Constructors
    public ATC(Statistics statistics) {
        this.statistics = statistics;
    }

    // Getters


    // Setters

    // Methods
    public synchronized void requestLanding(int planeId) { // Plane calls this to request landing
        landingQueue.add(planeId);
        System.out.println(AirportMain.getTimecode() + " [ATC] Received landing request from Plane " + planeId);
        notifyAll();
    }

    public synchronized void landingComplete(int planeId) { // Plane calls this to report landing complete
        notifyAll();
    }

    public synchronized void requestTakeoff(int planeId) { // Plane calls this to request takeoff
        takeoffQueue.add(planeId);
        System.out.println(AirportMain.getTimecode() + " [ATC] Received takeoff request from Plane " + planeId);
        notifyAll();
    }

    public synchronized void takeoffComplete(int planeId) { // Plane calls this to report takeoff complete
        groundCount--;
        System.out.println(AirportMain.getTimecode() + " [ATC] Plane " + planeId + " has taken off. Ground count: " + groundCount);
        notifyAll();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            synchronized (this) {
                // If there are planes in the landing queue and runway is free, clear the runway
                if (!landingQueue.isEmpty() && runwayFree && groundCount < Constants.MAX_GROUND) {
                    int planeId = landingQueue.poll();
                    runwayFree = false;
                    groundCount++;
                    System.out.println(AirportMain.getTimecode() + " [ATC] Runway cleared for Plane " + planeId + ". Ground count: " + groundCount + ".");
                } else if (!takeoffQueue.isEmpty() && runwayFree) { // Else if there are planes in the takeoff queue and runway is free, clear the runway
                    int planeId = takeoffQueue.poll();
                    runwayFree = false;
                    System.out.println(AirportMain.getTimecode() + " [ATC] Runway cleared for Plane " + planeId + " takeoff. Ground count: " + groundCount + ".");
                } else if (!landingQueue.isEmpty() && (!runwayFree || groundCount >= Constants.MAX_GROUND)) { // Else if there are planes in the landing queue but no runway or ground slot available
                    int planeId = landingQueue.peek();
                    System.out.println(AirportMain.getTimecode() + " [ATC] Waiting for runway or ground slot for Plane " + planeId);
                } else if (!takeoffQueue.isEmpty() && !runwayFree) { // Else if there are planes in the takeoff queue but no runway available
                    int planeId = takeoffQueue.peek();
                    System.out.println(AirportMain.getTimecode() + " [ATC] Waiting runway to be free for Plane " + planeId + " takeoff.");
                }

                // Free runway after landing/takeoff completion
                if (runwayFree && groundCount > 0 && landingQueue.isEmpty() && takeoffQueue.isEmpty()) {}

                try {
                    wait(500); // Check queues periodically
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    // Helper methods
    public synchronized boolean isRunwayClearedForLanding(int planeId) {
        return !runwayFree && !landingQueue.contains(planeId) && groundCount <= Constants.MAX_GROUND;
    }

    public synchronized boolean isRunwayClearedForTakeoff(int planeId) {
        return !runwayFree && !takeoffQueue.contains(planeId);
    }

    public synchronized void setRunwayFree() {
        runwayFree = true;
    }
}