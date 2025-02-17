package com.main;

import com.main.data.Statistics;
import java.util.Random;

public class AirportMain {
    public static void main(String[] args) {
        // Create shared objects
        Statistics statistics = new Statistics();

        // Create the ATC instance
        ATC atc = new ATC(statistics);

        // Optionally start an ATC thread if periodic tasks are needed (here, it’s optional)
        Thread atcThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }, "ATC-Thread");
        atcThread.start();

        // Create and start the RefuelingTruck thread
        RefuelingTruck refuelingTruck = new RefuelingTruck();
        Thread refuelTruckThread = new Thread(refuelingTruck, "RefuelingTruck");
        refuelTruckThread.start();

        // Create and start 3 Gate threads
        Gates[] gates = new Gates[Constants.NUM_GATES];
        Thread[] gateThreads = new Thread[Constants.NUM_GATES];
        for (int i = 0; i < Constants.NUM_GATES; i++) {
            gates[i] = new Gates(i + 1);
            gateThreads[i] = new Thread(gates[i], "Gate-" + (i + 1));
            gateThreads[i].start();
        }

        // Create and start 6 Plane threads with staggered arrivals
        Thread[] planeThreads = new Thread[Constants.NUM_PLANES];
        Random random = new Random();
        for (int i = 0; i < Constants.NUM_PLANES; i++) {
            try {
                Thread.sleep(random.nextInt(Constants.PLANE_ARRIVAL_MAX_DELAY_MS));  // Staggered arrival
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            Planes plane = new Planes(i + 1, atc, gates, refuelingTruck, statistics);
            planeThreads[i] = new Thread(plane, "Plane-" + (i + 1));
            planeThreads[i].start();
        }

        // Wait for all plane threads to finish their lifecycle
        for (Thread planeThread : planeThreads) {
            try {
                planeThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Print final statistics
        System.out.println("Simulation complete. Final statistics:");
        statistics.printStatistics();

        // Optionally, interrupt long-running threads (ATC, Gates, RefuelingTruck)
        atcThread.interrupt();
        for (Thread t : gateThreads) {
            t.interrupt();
        }
        refuelTruckThread.interrupt();
    }
}