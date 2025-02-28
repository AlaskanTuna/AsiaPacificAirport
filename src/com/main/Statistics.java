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

    public void printStatistics(ATC atc) {
        lock.readLock().lock();
        try {
            // 1. Sanity Check
            boolean allGatesEmpty = true;
            for (Gates gate : gates) {
                if (gate.isOccupied()) {
                    allGatesEmpty = false;
                    break; // No need to check further if one gate is occupied
                }
            }

            // 2. Waiting Time Statistics
            long maxWait = gateWaitTimes.isEmpty() ? 0 : gateWaitTimes.stream().mapToLong(Long::longValue).max().getAsLong();
            long minWait = gateWaitTimes.isEmpty() ? 0 : gateWaitTimes.stream().mapToLong(Long::longValue).min().getAsLong();
            double avgWait = gateWaitTimes.isEmpty() ? 0.0 : gateWaitTimes.stream().mapToLong(Long::longValue).average().getAsDouble();

            // 3. Final completion and stats summary
            Module.announceFinalCompletion(planesCompleted, atc.getGroundCount(), true);
            Module.announceStatsSummary(allGatesEmpty, maxWait, minWait, avgWait, planesCompleted, totalPassengersBoarded);
        } finally {
            lock.readLock().unlock();
        }
    }
}