package com.main;

import com.main.data.Logging;
import com.main.data.Statistics;

public class Gates implements Runnable {
    private final int gateId;
    private final Logging logger;
    private boolean occupied = false;

    public Gates(int gateId, Logging logger) {
        this.gateId = gateId;
        this.logger = logger;
    }

    // Synchronized accessor methods for occupancy
    public synchronized boolean isOccupied() {
        return occupied;
    }

    public synchronized void setOccupied(boolean status) {
        occupied = status;
    }

    public void performGateOperations(int planeId) {
        logger.log("Gate " + gateId + ": Performing operations for Plane " + planeId);
        sleep(1500);  // Simulate operations such as disembarking/boarding
        logger.log("Gate " + gateId + ": Completed operations for Plane " + planeId);
        synchronized (this) {
            occupied = false;
            notifyAll();
        }
    }

    public int getGateId() {
        return gateId;
    }

    @Override
    public void run() {
        // The gate thread can simply sleep or perform periodic tasks if needed.
        while (!Thread.currentThread().isInterrupted()) {
            sleep(1000);
        }
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
