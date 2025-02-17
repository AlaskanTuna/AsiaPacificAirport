package com.main;

import com.main.data.Statistics;

public class Planes implements Runnable {
    private final int planeId;
    private final ATC atc;
    private final Gates[] gates;
    private final RefuelingTruck refuelingTruck;
    private final Statistics statistics;

    public Planes(int planeId, ATC atc, Gates[] gates, RefuelingTruck refuelingTruck, Statistics statistics) {
        this.planeId = planeId;
        this.atc = atc;
        this.gates = gates;
        this.refuelingTruck = refuelingTruck;
        this.statistics = statistics;
    }

    @Override
    public void run() {
        // 1. Request landing
        atc.requestLanding(planeId);
        System.out.println("Plane " + planeId + ": Landing on runway.");
        sleep(Constants.LANDING_TIME_MS);  // Simulate landing time
        atc.landingComplete(planeId);

        // 2. Gate docking process
        Gates assignedGate = null;
        boolean docked = false;
        while (!docked) {
            for (Gates gate : gates) {
                synchronized (gate) {
                    if (!gate.isOccupied()) {
                        gate.setOccupied(true);
                        assignedGate = gate;
                        docked = true;
                        System.out.println("Plane " + planeId + ": Docked at Gate " + gate.getGateId());
                        break;
                    }
                }
            }
            if (!docked) {
                System.out.println("Plane " + planeId + ": Waiting for an available gate.");
                sleep(500);
            }
        }

        // 3. Perform gate operations
        if (assignedGate != null) {
            assignedGate.performGateOperations(planeId);
        }

        // 4. Request refuelling
        System.out.println("Plane " + planeId + ": Requesting refuelling.");
        refuelingTruck.requestRefuel(planeId);

        // 5. Request takeoff
        atc.requestTakeoff(planeId);
        System.out.println("Plane " + planeId + ": Taking off.");
        sleep(Constants.TAKEOFF_TIME_MS);  // Simulate takeoff time
        atc.takeoffComplete(planeId);
        System.out.println("Plane " + planeId + ": Has departed.");

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