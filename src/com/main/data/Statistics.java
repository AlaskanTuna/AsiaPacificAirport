package com.main.data;

public class Statistics {
    private int completedPlanes;

    // Method to record completed planes
    public synchronized void recordCompletedPlanes(int planeId) {
        completedPlanes++;
        System.out.println("Plane " + planeId + " has landed. Total planes landed: " + completedPlanes);
    }

    // Print the final statistics
    public synchronized void finalStats() {
        System.out.println("Total planes landed: " + completedPlanes);
    }
}