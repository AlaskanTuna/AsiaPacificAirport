// Statistics.java

package com.main;

import com.main.AirportMain;
import com.main.ATC;
import com.main.Gates;
import com.main.RefuelingTruck;
import com.main.Module;

public class Statistics {
    // -------------------- Data Fields -------------------- //

    private int planesCompleted = 0;

    // -------------------- Getters -------------------- //

    public int getPlanesCompleted() {
        return planesCompleted;
    }

    // -------------------- Methods -------------------- //

    public void recordPlaneCompletion(int planeId) {
        synchronized (this) {
            planesCompleted++;
            Module.announcePlaneCompletion(planeId, planesCompleted, true); // Delegate to Module
        }
    }

    public void printStatistics() {
        synchronized (this) {
            Module.announceFinalStatistics(planesCompleted, true); // Delegate to Module
        }
    }
}