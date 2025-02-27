// AirportMain.java

package com.main;

import com.main.ATC;
import com.main.Gates;
import com.main.Planes;
import com.main.RefuelingTruck;
import com.main.Statistics;
import com.main.Module;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class AirportMain {
    // -------------------- Getters -------------------- //

    public static String getTimecode() {
        return Module.getTimecode();
    }

    // -------------------- Main Method -------------------- //

    public static void main(String[] args) {
        Module.mainMenu();
    }

    // -------------------- Simulation Logic -------------------- //

    public static void normalSimulation(String[] args) {
        // 1. Create and start 3 Gate threads
        Gates[] gates = new Gates[Constants.NUM_GATES];
        Thread[] gateThreads = new Thread[Constants.NUM_GATES];
        Semaphore gateSemaphore = new Semaphore(Constants.NUM_GATES, true); // 3 permits, fair queuing
        for (int i = 0; i < Constants.NUM_GATES; i++) {
            gates[i] = new Gates(i + 1);
            gateThreads[i] = new Thread(gates[i], "Gate-" + (i + 1));
            gateThreads[i].start();
        }

        // 2. Create shared objects with gates
        Statistics statistics = new Statistics(gates);

        // 3. Create and start the ATC thread
        ATC atc = new ATC(statistics);
        Thread atcThread = new Thread(atc, "ATC");
        atcThread.start();

        // 4. Create and start the RefuelingTruck thread
        RefuelingTruck refuelingTruck = new RefuelingTruck();
        Thread refuelTruckThread = new Thread(refuelingTruck, "RefuelingTruck");
        refuelTruckThread.start();

        // 5. Create and start 6 Plane threads with staggered arrivals
        Thread[] planeThreads = new Thread[Constants.NUM_PLANES];
        Random random = new Random();

        for (int i = 0; i < Constants.NUM_PLANES; i++) {
            try {
                // Random delay before creating the next plane
                Thread.sleep(random.nextInt(Constants.PLANE_ARRIVAL_MAX_DELAY_MS));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            Planes plane = new Planes(i + 1, atc, gates, refuelingTruck, statistics, false, gateSemaphore);
            planeThreads[i] = new Thread(plane, "Plane-" + (i + 1));
            planeThreads[i].start();
        }

        // 6. Wait for all plane threads to finish their lifecycle
        for (Thread planeThread : planeThreads) {
            try {
                planeThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // 7. Print final statistics
        statistics.printStatistics();

        // 8. Interrupt all threads to stop the simulation
        for (Thread t : gateThreads) {
            t.interrupt();
        }
        atcThread.interrupt();
        refuelTruckThread.interrupt();
    }

    public static void emergencySimulation(String[] args) {

        /* A congested scenario should be simulated where 2 planes are waiting to land while the 2 gates
        are occupied, and a 3rd plane comes in with fuel shortage, requiring emergency landing. */

        // 1. Create and start 3 Gate threads
        Gates[] gates = new Gates[Constants.NUM_GATES];
        Thread[] gateThreads = new Thread[Constants.NUM_GATES];
        Semaphore gateSemaphore = new Semaphore(Constants.NUM_GATES, true);
        for (int i = 0; i < Constants.NUM_GATES; i++) {
            gates[i] = new Gates(i + 1);
            gateThreads[i] = new Thread(gates[i], "Gate-" + (i + 1));
            gateThreads[i].start();
        }

        // 2. Create shared objects with gates
        Statistics statistics = new Statistics(gates);

        // 3. Create and start the ATC thread
        ATC atc = new ATC(statistics);
        Thread atcThread = new Thread(atc, "ATC");
        atcThread.start();

        // 4. Create and start the RefuelingTruck thread
        RefuelingTruck refuelingTruck = new RefuelingTruck();
        Thread refuelTruckThread = new Thread(refuelingTruck, "RefuelingTruck");
        refuelTruckThread.start();

        // 5. Create plane threads
        Thread[] planeThreads = new Thread[Constants.NUM_PLANES];

        // 5.1. Deploy Plane-1 and Plane-2 to occupy gates
        Planes plane1 = new Planes(1, atc, gates, refuelingTruck, statistics, false, gateSemaphore);
        planeThreads[0] = new Thread(plane1, "Plane-1");
        planeThreads[0].start();
        Planes plane2 = new Planes(2, atc, gates, refuelingTruck, statistics, false, gateSemaphore);
        planeThreads[1] = new Thread(plane2, "Plane-2");
        planeThreads[1].start();

        // 5.2. Deploy Plane-3 slightly later to be landing during congestion
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        Planes plane3 = new Planes(3, atc, gates, refuelingTruck, statistics, false, gateSemaphore);
        planeThreads[2] = new Thread(plane3, "Plane-3");
        planeThreads[2].start();

        // 5.3. Delay to ensure ground is full (2 gates occupied, 1 landing)
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 5.4. Deploy Plane-4 and Plane-5 to wait in landing queue
        Planes plane4 = new Planes(4, atc, gates, refuelingTruck, statistics, false, gateSemaphore);
        planeThreads[3] = new Thread(plane4, "Plane-4");
        planeThreads[3].start();
        Planes plane5 = new Planes(5, atc, gates, refuelingTruck, statistics, false, gateSemaphore);
        planeThreads[4] = new Thread(plane5, "Plane-5");
        planeThreads[4].start();

        // 5.5. Deploy Plane-6 as the emergency plane
        Planes emergencyPlane = new Planes(6, atc, gates, refuelingTruck, statistics, true, gateSemaphore);
        planeThreads[5] = new Thread(emergencyPlane, "Plane-6");
        planeThreads[5].start();

        // 6. Wait for all planes to complete their lifecycle
        for (Thread planeThread : planeThreads) {
            try {
                planeThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // 7. Print final statistics
        statistics.printStatistics();

        // 8. Interrupt all threads
        for (Thread t : gateThreads) {
            t.interrupt();
        }
        atcThread.interrupt();
        refuelTruckThread.interrupt();
    }
}

// CMD RUNNING COMMANDS
// javac -d bin src/com/main/*.java
// java -cp bin com.main.AirportMain