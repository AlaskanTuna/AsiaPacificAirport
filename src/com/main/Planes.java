// Planes.java

package com.main;

import com.main.ATC.*;
import com.main.Gates.*;
import com.main.RefuelingTruck.*;
import com.main.Statistics.*;
import com.main.Module.*;

import java.util.concurrent.Semaphore;

public class Planes implements Runnable {
    // -------------------- Data Fields -------------------- //

    private final int planeId;
    private final ATC atc;
    private final Gates[] gates;
    private final RefuelingTruck refuelingTruck;
    private final Statistics statistics;
    private final boolean isEmergency;
    private final Semaphore gateSemaphore;
    private long landingTime;

    // -------------------- Constructors -------------------- //

    public Planes(int planeId, ATC atc, Gates[] gates, RefuelingTruck refuelingTruck, Statistics statistics, boolean isEmergency, Semaphore gateSemaphore) {
        this.planeId = planeId;
        this.atc = atc;
        this.gates = gates;
        this.refuelingTruck = refuelingTruck;
        this.statistics = statistics;
        this.isEmergency = isEmergency;
        this.gateSemaphore = gateSemaphore;
    }

    public Planes(int planeId, ATC atc, Gates[] gates, RefuelingTruck refuelingTruck, Statistics statistics) {
        this(planeId, atc, gates, refuelingTruck, statistics, false, null); // For legacy calls without semaphore
    }

    // -------------------- Methods -------------------- //

    @Override
    public void run() {
        // 1. Request landing
        if (isEmergency) {
            Module.printMessage(AirportMain.getTimecode() + " [" + Thread.currentThread().getName() + "] Requesting emergency landing.", Constants.ANSI_RED, false);
        } else {
            Module.printMessage(AirportMain.getTimecode() + " [" + Thread.currentThread().getName() + "] Requesting landing.", Constants.ANSI_YELLOW, false);
        }
        atc.requestLanding(planeId, isEmergency);
        synchronized (atc) {
            while (!atc.isRunwayClearedForLanding(planeId)) {
                try {
                    atc.wait(Constants.LANDING_REQUEST_TIME_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        Module.printMessage(AirportMain.getTimecode() + " [" + Thread.currentThread().getName() + "] Landing on runway.", Constants.ANSI_YELLOW, false);
        sleep(Constants.LANDING_TIME_MS);
        Module.printMessage(AirportMain.getTimecode() + " [" + Thread.currentThread().getName() + "] Landed on runway.", Constants.ANSI_GREEN, false);
        atc.landingComplete(planeId);
        landingTime = System.currentTimeMillis();
        synchronized (atc) {
            atc.setRunwayFree();
            atc.notifyAll();
        }

        // 2. Gate docking process
        Gates assignedGate = null;
        try {
            if (gateSemaphore != null) {
                gateSemaphore.acquire(); // Acquire gate permit before plane docks
            }
            for (Gates g : gates) {
                synchronized (g) {
                    if (!g.isOccupied()) {
                        sleep(Constants.GATE_DOCKING_TIME_MS);  // Simulate docking time
                        g.setOccupied(true);
                        assignedGate = g;
                        long dockingTime = System.currentTimeMillis();
                        long waitTime = dockingTime - landingTime;
                        statistics.recordGateWaitTime(waitTime);
                        Module.printMessage(AirportMain.getTimecode() + " [" + Thread.currentThread().getName() + "] Docked at Gate " + g.getGateId() + ".",
                                Constants.ANSI_GREEN, false);
                        break;
                    }
                }
            }

            if (assignedGate == null) {
                Module.printMessage(AirportMain.getTimecode() + " [" + Thread.currentThread().getName() + "] Waiting for an available gate.", Constants.ANSI_YELLOW, false);
                sleep(1000);
            } else {
                Module.printMessage(AirportMain.getTimecode() + " [" + Thread.currentThread().getName() + "] Disembarking passengers from the plane.", Constants.ANSI_YELLOW, false);
                assignedGate.performGateOperations(planeId);
                Module.printMessage(AirportMain.getTimecode() + " [" + Thread.currentThread().getName() + "] Embarked passengers onto the plane.", Constants.ANSI_GREEN, false);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (assignedGate != null) {
                synchronized (assignedGate) {
                    assignedGate.setOccupied(false);
                }
                if (gateSemaphore != null) {
                    gateSemaphore.release(); // Release gate permit after plane undocks
                }
            }
        }

        // 3. Request refuelling
        Module.printMessage(AirportMain.getTimecode() + " [" + Thread.currentThread().getName() + "] Requesting refuelling.", Constants.ANSI_YELLOW, false);
        refuelingTruck.requestRefuel(planeId);
        Module.printMessage(AirportMain.getTimecode() + " [" + Thread.currentThread().getName() + "] Refuelling complete.", Constants.ANSI_GREEN, false);

        // 4. Request takeoff
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY); // 1
        Module.printMessage(AirportMain.getTimecode() + " [" + Thread.currentThread().getName() + "] Requesting takeoff.", Constants.ANSI_YELLOW, false);
        atc.requestTakeoff(planeId);
        synchronized (atc) {
            while (!atc.isRunwayClearedForTakeoff(planeId)) {
                try {
                    atc.wait(Constants.TAKEOFF_REQUEST_TIME_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        Module.printMessage(AirportMain.getTimecode() + " [" + Thread.currentThread().getName() + "] Taking off.", Constants.ANSI_YELLOW, false);
        sleep(Constants.TAKEOFF_TIME_MS);
        Module.printMessage(AirportMain.getTimecode() + " [" + Thread.currentThread().getName() + "] Has departed.", Constants.ANSI_GREEN, false);
        synchronized (atc) {
            atc.setRunwayFree();
            atc.takeoffComplete(planeId);
            atc.notifyAll();
        }

        // 5. Record completion in statistics
        statistics.recordPlaneCompletion(planeId);
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