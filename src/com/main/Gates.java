package com.main;

public class Gates implements Runnable {
    private final int gateId;
    private boolean occupied = false;

    public Gates(int gateId) {
        this.gateId = gateId;
    }

    // Synchronized accessor methods for occupancy
    public synchronized boolean isOccupied() {
        return occupied;
    }

    public synchronized void setOccupied(boolean status) {
        occupied = status;
    }

    public void performGateOperations(int planeId) {
        System.out.println("Gate " + gateId + ": Performing supply refill and cleaning for Plane " + planeId);
        sleep(Constants.GATE_OPERATION_TIME_MS);
        System.out.println("Gate " + gateId + ": Completed supply refill and cleaning for Plane " + planeId);
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