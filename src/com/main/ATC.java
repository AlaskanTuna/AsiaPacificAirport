package com.main;

import com.main.data.Statistics;

public class ATC {
    private final Statistics statistics;
    private boolean runwayFree = true;
    private int groundCount = 0;
    private final int maxCount = Constants.MAX_GROUND;

    public ATC(Statistics statistics) {
        this.statistics = statistics;
    }

    public void requestLanding(int planeId) {
        System.out.println("ATC: Plane " + planeId + " requests landing.");
        synchronized (this) {
            while (!runwayFree || groundCount >= maxCount) {
                try {
                    System.out.println("ATC: Plane " + planeId + " waiting for runway or ground slot.");
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            // Critical section: update runway and ground count atomically
            runwayFree = false;
            groundCount++;
            System.out.println("ATC: Plane " + planeId + " cleared to land. Ground count: " + groundCount);
        }
    }

    public void landingComplete(int planeId) {
        synchronized (this) {
            runwayFree = true;
            System.out.println("ATC: Plane " + planeId + " has landed. Runway is now free.");
            notifyAll();
        }
    }

    public void requestTakeoff(int planeId) {
        System.out.println("ATC: Plane " + planeId + " requests takeoff.");
        synchronized (this) {
            while (!runwayFree) {
                try {
                    System.out.println("ATC: Plane " + planeId + " waiting for runway to be free for takeoff.");
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            runwayFree = false;
            System.out.println("ATC: Plane " + planeId + " cleared for takeoff.");
        }
    }

    public void takeoffComplete(int planeId) {
        synchronized (this) {
            runwayFree = true;
            groundCount--;
            System.out.println("ATC: Plane " + planeId + " has taken off. Ground count: " + groundCount);
            notifyAll();
        }
    }
}