// ATC.java

package com.main;

import com.main.Gates.*;
import com.main.Planes.*;
import com.main.RefuelingTruck.*;
import com.main.Statistics.*;
import com.main.Module.*;

import java.util.Queue;
import java.util.LinkedList;

public class ATC implements Runnable {
    // -------------------- Data Fields -------------------- //

    private final Statistics statistics;
    private boolean runwayFree = true;
    private int groundCount = 0;
    private final Queue<Integer> landingQueue = new LinkedList<>();
    private final Queue<Integer> emergencyQueue = new LinkedList<>();
    private final Queue<Integer> takeoffQueue = new LinkedList<>();

    // -------------------- Constructors -------------------- //

    public ATC(Statistics statistics) {
        this.statistics = statistics;
    }

    // -------------------- Getters -------------------- //

    public int getGroundCount() {
        return groundCount;
    }

    // -------------------- Setters -------------------- //

    public synchronized void setRunwayFree() {
        runwayFree = true;
    }

    // -------------------- Methods -------------------- //

    // Overload method for emergency landing
    public synchronized void requestLanding(int planeId, boolean isEmergency) {
        if (isEmergency) {
            emergencyQueue.add(planeId);
            Module.printMessage(AirportMain.getTimecode() + " [ATC] Received emergency landing request from Plane " + planeId + ".", Constants.ANSI_RED, false);
        } else {
            landingQueue.add(planeId);
            Module.printMessage(AirportMain.getTimecode() + " [ATC] Received landing request from Plane " + planeId + ".", Constants.ANSI_RESET, false);
        }
        notifyAll();
    }

    // Default method for non-emergency landing
    public synchronized void requestLanding(int planeId) {
        requestLanding(planeId, false);
    }

    public synchronized void landingComplete(int planeId) {
        groundCount++;
        Module.printMessage(AirportMain.getTimecode() + " [ATC] Plane " + planeId + " has successfully landed on runway. Ground count: " + groundCount + ".", Constants.ANSI_RESET, false);
        // Announce warning if ground count reaches maximum
        if (groundCount == Constants.MAX_GROUND) {
            Module.announceGroundCountWarning(groundCount, true);
        }
        notifyAll();
    }

    public synchronized void requestTakeoff(int planeId) {
        takeoffQueue.add(planeId);
        Module.printMessage(AirportMain.getTimecode() + " [ATC] Received takeoff request from Plane " + planeId + ".", Constants.ANSI_RESET, false);
        notifyAll();
    }

    public synchronized void takeoffComplete(int planeId) {
        groundCount--;
        Module.printMessage(AirportMain.getTimecode() + " [ATC] Plane " + planeId + " has taken off. Ground count: " + groundCount + ".", Constants.ANSI_RESET, false);
        notifyAll();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            synchronized (this) {
                if (!emergencyQueue.isEmpty() && runwayFree && groundCount < Constants.MAX_GROUND) {
                    int planeId = emergencyQueue.poll();
                    runwayFree = false;
                    Module.printMessage(AirportMain.getTimecode() + " [ATC] Runway cleared for emergency landing of Plane " + planeId + ". Ground count: " + groundCount + ".", Constants.ANSI_RED, false);
                } else if (!landingQueue.isEmpty() && runwayFree && groundCount < Constants.MAX_GROUND) {
                    int planeId = landingQueue.poll();
                    runwayFree = false;
                    Module.printMessage(AirportMain.getTimecode() + " [ATC] Runway cleared for Plane " + planeId + ". Ground count: " + groundCount + ".", Constants.ANSI_RESET, false);
                } else if (!takeoffQueue.isEmpty() && runwayFree) {
                    int planeId = takeoffQueue.poll();
                    runwayFree = false;
                    Module.printMessage(AirportMain.getTimecode() + " [ATC] Runway cleared for Plane " + planeId + " takeoff. Ground count: " + groundCount + ".", Constants.ANSI_RESET, false);
                } else if (!emergencyQueue.isEmpty() || !landingQueue.isEmpty()) {
                    int planeId = !emergencyQueue.isEmpty() ? emergencyQueue.peek() : landingQueue.peek();
                    Module.printMessage(AirportMain.getTimecode() + " [ATC] Waiting for runway or ground slot for Plane " + planeId + " landing.", Constants.ANSI_RESET, false);
                } else if (!takeoffQueue.isEmpty() && !runwayFree) {
                    int planeId = takeoffQueue.peek();
                    Module.printMessage(AirportMain.getTimecode() + " [ATC] Waiting runway to be free for Plane " + planeId + " takeoff.", Constants.ANSI_RESET, false);
                }

                try {
                    wait(Constants.RUNWAY_CHECK_TIME_MS); // Wait for runway availability
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    // -------------------- Helper Methods -------------------- //

    public synchronized boolean isRunwayClearedForLanding(int planeId) {
        return !runwayFree && !landingQueue.contains(planeId) && !emergencyQueue.contains(planeId) && groundCount <= Constants.MAX_GROUND;
    }

    public synchronized boolean isRunwayClearedForTakeoff(int planeId) {
        return !runwayFree && !takeoffQueue.contains(planeId);
    }
}