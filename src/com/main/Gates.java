package com.main;

import com.main.ATC;
import com.main.Planes;
import com.main.RefuelingTruck;
import com.main.data.Statistics;

public class Gates implements Runnable {
    // Data fields
    private final int gateId;
    private boolean occupied = false;
    private final String threadName;

    // Constructors
    public Gates(int gateId) {
        this.gateId = gateId;
        this.threadName = "Gate-" + gateId;
    }

    // Getters
    public int getGateId() {
        return gateId;
    }

    public synchronized boolean isOccupied() {
        return occupied;
    }

    // Setters
    public synchronized void setOccupied(boolean status) {
        occupied = status;
    }

    // Methods
    public void performGateOperations(int planeId) {
        System.out.println(AirportMain.getTimecode() + " [" + threadName + "] Passengers disembarked from Plane " + planeId);
        sleep(Constants.GATE_OPERATION_TIME_MS);
        System.out.println(AirportMain.getTimecode() + " [" + threadName + "] Performing supply refill and cleaning for Plane " + planeId);
        sleep(Constants.GATE_OPERATION_TIME_MS);
        System.out.println(AirportMain.getTimecode() + " [" + threadName + "] Completed supply refill and cleaning for Plane " + planeId);
        sleep(Constants.GATE_OPERATION_TIME_MS);
        System.out.println(AirportMain.getTimecode() + " [" + threadName + "] Passengers are embarking onto Plane " + planeId);
        synchronized (this) {
            occupied = false;
            notifyAll();
        }
    }

    @Override
    public void run() {
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