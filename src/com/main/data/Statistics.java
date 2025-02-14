package com.main.data;

public class Statistics {
    private int planesCompleted = 0;

    public void recordPlaneCompletion(int planeId) {
        synchronized (this) {
            planesCompleted++;
            System.out.println("Statistics: Recorded completion for Plane " + planeId + ". Total completed: " + planesCompleted);
        }
    }

    public void printStatistics() {
        synchronized (this) {
            System.out.println("Statistics: Total planes completed: " + planesCompleted);
        }
    }
}