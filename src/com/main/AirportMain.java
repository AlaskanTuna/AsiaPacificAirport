package com.main;

import com.main.data.Logging;
import com.main.data.Statistics;

import java.util.Random;

public class AirportMain {
    public static void main(String[] args) {
        // Create shared objects
        Logging logger = new Logging();
        Statistics statistics = new Statistics();

        // Create the ATC instance
        ATC atc = new ATC(logger, statistics);

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
        RefuelingTruck refuelingTruck = new RefuelingTruck(logger);
        Thread refuelTruckThread = new Thread(refuelingTruck, "RefuelingTruck");
        refuelTruckThread.start();

        // Create and start 3 Gate threads
        Gates[] gates = new Gates[3];
        Thread[] gateThreads = new Thread[3];
        for (int i = 0; i < 3; i++) {
            gates[i] = new Gates(i + 1, logger);
            gateThreads[i] = new Thread(gates[i], "Gate-" + (i + 1));
            gateThreads[i].start();
        }

        // Create and start 6 Plane threads with staggered arrivals
        Thread[] planeThreads = new Thread[6];
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            try {
                Thread.sleep(random.nextInt(2000));  // Staggered arrival
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            Planes plane = new Planes(i + 1, atc, gates, refuelingTruck, logger, statistics);
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
        logger.log("Simulation complete. Final statistics:");
        statistics.printStatistics();

        // Optionally, interrupt long-running threads (ATC, Gates, RefuelingTruck)
        atcThread.interrupt();
        for (Thread t : gateThreads) {
            t.interrupt();
        }
        refuelTruckThread.interrupt();
    }
}