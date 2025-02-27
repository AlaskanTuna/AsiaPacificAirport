// Statistics.java

package com.main;

import com.main.AirportMain;
import com.main.ATC;
import com.main.Gates;
import com.main.RefuelingTruck;
import com.main.Module;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Statistics {
    // -------------------- Data Fields -------------------- //

    private int planesCompleted = 0;
    private final List<Long> gateWaitTimes = new ArrayList<>();
    private int totalPassengersBoarded = 0;
    private final Gates[] gates;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    // -------------------- Constructors -------------------- //

    public Statistics(Gates[] gates) {
        this.gates = gates;
    }

    // -------------------- Getters -------------------- //

    public int getPlanesCompleted() {
        lock.readLock().lock();
        try {
            return planesCompleted;
        } finally {
            lock.readLock().unlock();
        }
    }

    // -------------------- Methods -------------------- //

    public void recordPlaneCompletion(int planeId) {
        lock.writeLock().lock();
        try {
            planesCompleted++;
            totalPassengersBoarded += Constants.PASSENGERS_PER_PLANE;
            Module.announcePlaneCompletion(planeId, planesCompleted, true);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void recordGateWaitTime(long waitTime) {
        lock.writeLock().lock();
        try {
            gateWaitTimes.add(waitTime);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void printStatistics() {
        lock.readLock().lock();
        try {
            // Sanity Check
            boolean allGatesEmpty = true;
            for (Gates gate : gates) {
                if (gate.isOccupied()) {
                    allGatesEmpty = false;
                    Module.printStatsMessage("Sanity Check Failed: Gate " + gate.getGateId() + " is still occupied.");
                }
            }
            if (allGatesEmpty) {
                Module.printStatsMessage("Sanity Check Passed: All gates are empty.");
            }

            // Waiting Time Statistics
            if (gateWaitTimes.isEmpty()) {
                Module.printStatsMessage("No gate waiting times recorded.");
            } else {
                long maxWait = gateWaitTimes.stream().mapToLong(Long::longValue).max().getAsLong();
                long minWait = gateWaitTimes.stream().mapToLong(Long::longValue).min().getAsLong();
                double avgWait = gateWaitTimes.stream().mapToLong(Long::longValue).average().getAsDouble();
                Module.printStatsMessage("Max Gate Wait Time: " + maxWait + " ms.");
                Module.printStatsMessage("Min Gate Wait Time: " + minWait + " ms.");
                Module.printStatsMessage("Avg Gate Wait Time: " + String.format("%.2f", avgWait) + " ms.");
            }

            // Planes Served and Passengers Boarded
            Module.printStatsMessage("Number of Planes Served: " + planesCompleted + " / " + Constants.NUM_PLANES + ".");
            Module.printStatsMessage("Total Passengers Boarded: " + totalPassengersBoarded + " / " + (Constants.NUM_PLANES * Constants.PASSENGERS_PER_PLANE) + ".");

            // Final completion message
            Module.announceFinalStatistics(planesCompleted, true);
        } finally {
            lock.readLock().unlock();
        }
    }
}