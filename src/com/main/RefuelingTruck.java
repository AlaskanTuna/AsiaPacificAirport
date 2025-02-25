package com.main;

import com.main.ATC;
import com.main.Gates;
import com.main.Planes;
import com.main.data.Statistics;

public class RefuelingTruck implements Runnable {
    // Data fields
    private boolean isBusy = false;
    private final String threadName = "RefuelingTruck";

    // Constructors

    // Getters

    // Setters

    // Methods
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
            System.out.println(AirportMain.getTimecode() + " [" + threadName + "] Starting refuelling for Plane " + planeId);
            sleep(Constants.REFUEL_TIME_MS);
            System.out.println(AirportMain.getTimecode() + " [" + threadName + "] Completed refuelling for Plane " + planeId);
            isBusy = false;
            notifyAll();
        }
    }

    @Override
    public void run() {
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