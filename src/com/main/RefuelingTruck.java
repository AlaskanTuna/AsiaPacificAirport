// RefuelingTruck.java

package com.main;

import com.main.ATC;
import com.main.Gates;
import com.main.Planes;
import com.main.Statistics;
import com.main.Module;

public class RefuelingTruck implements Runnable {
    // -------------------- Data Fields -------------------- //

    private boolean isBusy = false;
    private final String threadName = "RefuelingTruck";

    // -------------------- Methods -------------------- //

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
            Module.printMessage(AirportMain.getTimecode() + " [" + threadName + "] Starting refuelling for Plane " + planeId + ".");
            sleep(Constants.REFUEL_TIME_MS);
            Module.printMessage(AirportMain.getTimecode() + " [" + threadName + "] Completed refuelling for Plane " + planeId + ".");
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

    // -------------------- Helper Methods -------------------- //

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}