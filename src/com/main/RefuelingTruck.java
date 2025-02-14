package com.main;

import com.main.data.Logging;
import com.main.data.Statistics;

public class RefuelingTruck implements Runnable {
    private final Logging logger;
    private boolean isBusy = false;

    public RefuelingTruck(Logging logger) {
        this.logger = logger;
    }

    public void requestRefuel(int planeId) {
        synchronized (this) {
            while (isBusy) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            isBusy = true;
            logger.log("RefuelingTruck: Starting refuelling for Plane " + planeId);
            sleep(1000);  // Simulate refuelling time
            logger.log("RefuelingTruck: Completed refuelling for Plane " + planeId);
            isBusy = false;
            notifyAll();
        }
    }

    @Override
    public void run() {
        // The truck's thread loop remains active (optional) for any periodic tasks
        while (!Thread.currentThread().isInterrupted()) {
            sleep(500);
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
