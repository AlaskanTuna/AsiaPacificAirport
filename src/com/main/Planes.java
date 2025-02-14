package com.main;

import com.main.data.Logging;
import com.main.data.Statistics;

public class Planes implements Runnable {
    private final int planeId;
    private final ATC atc;
    private final Gates[] gates;
    private final RefuelingTruck refuelingTruck;
    private final Logging logger;
    private final Statistics statistics;

    public Planes(int planeId, ATC atc, Gates[] gates, RefuelingTruck refuelingTruck, Logging logger, Statistics statistics) {
        this.planeId = planeId;
        this.atc = atc;
        this.gates = gates;
        this.refuelingTruck = refuelingTruck;
        this.logger = logger;
        this.statistics = statistics;
    }

    @Override
    public void run() {
        // 1. Request landing
        atc.requestLanding(planeId);
        logger.log("Plane " + planeId + ": Landing on runway.");
        sleep(1000);  // Simulate landing time
        atc.landingComplete(planeId);

        // 2. Gate docking process
        Gates assignedGate = null;
        boolean docked = false;
        while (!docked) {
            for (Gates gate : gates) {
                // Use an explicit synchronized block on the gate object
                synchronized (gate) {
                    if (!gate.isOccupied()) {
                        gate.setOccupied(true);
                        assignedGate = gate;
                        docked = true;
                        logger.log("Plane " + planeId + ": Docked at Gate " + gate.getGateId());
                        break;
                    }
                }
            }
            if (!docked) {
                logger.log("Plane " + planeId + ": Waiting for an available gate.");
                sleep(500);
            }
        }

        // 3. Perform gate operations
        if (assignedGate != null) {
            assignedGate.performGateOperations(planeId);
        }

        // 4. Request refuelling
        logger.log("Plane " + planeId + ": Requesting refuelling.");
        refuelingTruck.requestRefuel(planeId);

        // 5. Request takeoff
        atc.requestTakeoff(planeId);
        logger.log("Plane " + planeId + ": Taking off.");
        sleep(1000);  // Simulate takeoff time
        atc.takeoffComplete(planeId);
        logger.log("Plane " + planeId + ": Has departed.");

        // 6. Record completion in statistics
        statistics.recordPlaneCompletion(planeId);
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
