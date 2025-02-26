// Planes.java

package com.main;

import com.main.ATC;
import com.main.Gates;
import com.main.RefuelingTruck;
import com.main.Statistics;
import com.main.Module;

public class Planes implements Runnable {
    // -------------------- Data Fields -------------------- //

    private final int planeId;
    private final ATC atc;
    private final Gates[] gates;
    private final RefuelingTruck refuelingTruck;
    private final Statistics statistics;

    // -------------------- Constructors -------------------- //

    public Planes(int planeId, ATC atc, Gates[] gates, RefuelingTruck refuelingTruck, Statistics statistics) {
        this.planeId = planeId;
        this.atc = atc;
        this.gates = gates;
        this.refuelingTruck = refuelingTruck;
        this.statistics = statistics;
    }

    // -------------------- Methods -------------------- //

    @Override
    public void run() {
        // 1. Request landing
        Module.printMessage(AirportMain.getTimecode() + " [" + Thread.currentThread().getName() + "] Requesting landing.");
        atc.requestLanding(planeId);
        synchronized (atc) {
            while (!atc.isRunwayClearedForLanding(planeId)) {
                try {
                    atc.wait(Constants.LANDING_REQUEST_TIME_MS); // Simulate waiting time between landing requests
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        Module.printMessage(AirportMain.getTimecode() + " [" + Thread.currentThread().getName() + "] Landing on runway.");
        sleep(Constants.LANDING_TIME_MS);
        Module.printMessage(AirportMain.getTimecode() + " [" + Thread.currentThread().getName() + "] Landed on runway.");
        atc.landingComplete(planeId);
        synchronized (atc) {
            atc.setRunwayFree();
            atc.notifyAll();
        }

        // 2. Gate docking process
        Gates assignedGate = null;
        boolean docked = false;
        while (!docked) {
            for (Gates g : gates) {
                synchronized (g) {
                    if (!g.isOccupied()) {
                        sleep(Constants.GATE_DOCKING_TIME_MS); // Simulate time to dock at the gate
                        g.setOccupied(true);
                        assignedGate = g;
                        docked = true;
                        Module.printMessage(AirportMain.getTimecode() + " [" + Thread.currentThread().getName() + "] Docked at Gate " + g.getGateId() + ".");
                        break;
                    }
                }
            }
            if (!docked) {
                Module.printMessage(AirportMain.getTimecode() + " [" + Thread.currentThread().getName() + "] Waiting for an available gate.");
                sleep(1000);
            }
        }

        // 3. Perform gate operations
        if (assignedGate != null) {
            Module.printMessage(AirportMain.getTimecode() + " [" + Thread.currentThread().getName() + "] Disembarking passengers from the plane.");
            assignedGate.performGateOperations(planeId);
            Module.printMessage(AirportMain.getTimecode() + " [" + Thread.currentThread().getName() + "] Embarked passengers onto the plane.");
        }

        // 4. Request refuelling
        Module.printMessage(AirportMain.getTimecode() + " [" + Thread.currentThread().getName() + "] Requesting refuelling.");
        refuelingTruck.requestRefuel(planeId);

        // 5. Request takeoff
        atc.requestTakeoff(planeId);
        synchronized (atc) {
            while (!atc.isRunwayClearedForTakeoff(planeId)) {
                try {
                    atc.wait(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        Module.printMessage(AirportMain.getTimecode() + " [" + Thread.currentThread().getName() + "] Taking off.");
        sleep(Constants.TAKEOFF_TIME_MS);
        Module.printMessage(AirportMain.getTimecode() + " [" + Thread.currentThread().getName() + "] Has departed.");
        synchronized (atc) {
            atc.setRunwayFree();
            atc.takeoffComplete(planeId);
            atc.notifyAll();
        }

        // 6. Record completion in statistics
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