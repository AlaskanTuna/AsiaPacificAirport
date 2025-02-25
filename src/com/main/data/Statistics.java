package com.main.data;

import com.main.AirportMain;
import com.main.ATC;
import com.main.Gates;
import com.main.RefuelingTruck;

public class Statistics {
    // Data fields
    private int planesCompleted = 0;

    // Constructors

    // Getters

    // Setters

    // Methods
    public void recordPlaneCompletion(int planeId) {
        synchronized (this) {
            planesCompleted++;
            System.out.println(
                "\n============================================================================\n" +
                AirportMain.getTimecode() + " [" + Thread.currentThread().getName() + "] Statistics: Recorded completion for Plane " + planeId + ". Total completed: " + planesCompleted +
                "\n============================================================================\n"
            );
        }
    }

    public void printStatistics() {
        synchronized (this) {
            System.out.println("============================================================================");
            System.out.println(AirportMain.getTimecode() + " [" + Thread.currentThread().getName() + "] Statistics: Total planes completed: " + planesCompleted);
            System.out.println("============================================================================");
        }
    }
}