// AirportMain.java

package com.main;

import com.main.ATC;
import com.main.Gates;
import com.main.Planes;
import com.main.RefuelingTruck;
import com.main.Statistics;

import java.util.Random;

public class AirportMain {
    // -------------------- Data Fields -------------------- //

    private static final long startTime = System.currentTimeMillis();

    // -------------------- Getters -------------------- //

    public static String getTimecode() {
        long elapsedMs = System.currentTimeMillis() - startTime; // Get elapsed time by - start time
        long seconds = (elapsedMs / 1000) % 60;
        long minutes = (elapsedMs / (1000 * 60)) % 60;
        return String.format("[%02d:%02d]", minutes, seconds);
    }

    // -------------------- Main Method -------------------- //

    public static void main(String[] args) {
        // Create shared objects
        Statistics statistics = new Statistics();

        // Create and start the ATC thread
        ATC atc = new ATC(statistics);
        Thread atcThread = new Thread(atc, "ATC");
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
                // Random delay before creating the next plane
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

        // Print final statistics (no need for extra println since Module handles it)
        statistics.printStatistics();

        // Interrupt all threads to stop the simulation
        for (Thread t : gateThreads) {
            t.interrupt();
        }
        atcThread.interrupt();
        refuelTruckThread.interrupt();
    }
}

/* CMD RUNNING COMMANDS
javac -d bin src/com/main/*.java
java -cp bin com.main.AirportMain
*/