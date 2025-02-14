package com.main;

import com.main.data.Logging;
import com.main.data.Statistics;

public class ATC {
    private final Logging logger;
    private final Statistics statistics;
    private boolean runwayFree = true;
    private int groundCount = 0;
    private final int MAXGROUND = 3;

    public ATC(Logging logger, Statistics statistics) {
        this.logger = logger;
        this.statistics = statistics;
    }

    public void requestLanding(int planeId) {
        logger.log("ATC: Plane " + planeId + " requests landing.");
        synchronized (this) {
            while (!runwayFree || groundCount >= MAXGROUND) {
                try {
                    logger.log("ATC: Plane " + planeId + " waiting for runway or ground slot.");
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            // Critical section: update runway and ground count atomically
            runwayFree = false;
            groundCount++;
            logger.log("ATC: Plane " + planeId + " cleared to land. Ground count: " + groundCount);
        }
    }

    public void landingComplete(int planeId) {
        synchronized (this) {
            runwayFree = true;
            logger.log("ATC: Plane " + planeId + " has landed. Runway is now free.");
            notifyAll();
        }
    }

    public void requestTakeoff(int planeId) {
        logger.log("ATC: Plane " + planeId + " requests takeoff.");
        synchronized (this) {
            while (!runwayFree) {
                try {
                    logger.log("ATC: Plane " + planeId + " waiting for runway to be free for takeoff.");
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            runwayFree = false;
            logger.log("ATC: Plane " + planeId + " cleared for takeoff.");
        }
    }

    public void takeoffComplete(int planeId) {
        synchronized (this) {
            runwayFree = true;
            groundCount--;
            logger.log("ATC: Plane " + planeId + " has taken off. Ground count: " + groundCount);
            notifyAll();
        }
    }
}