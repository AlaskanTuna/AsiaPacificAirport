package com.main;

public class RefuelingTruck implements Runnable {
    private boolean isBusy = false;

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
            System.out.println("RefuelingTruck: Starting refuelling for Plane " + planeId);
            sleep(Constants.REFUEL_TIME_MS);  // Simulate refuelling time
            System.out.println("RefuelingTruck: Completed refuelling for Plane " + planeId);
            isBusy = false;
            notifyAll();
        }
    }

    @Override
    public void run() {
        // The truck's thread loop remains active (optional) for any periodic tasks.
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