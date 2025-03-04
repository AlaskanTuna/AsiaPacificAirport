# Asia Pacific Airport Simulation

This educational project is a Java-based concurrent programming simulation of the Asia Pacific Airport. It models airport operations with multiple threads managing planes, gates, air traffic control (ATC) and a refueling truck, adhering to specified concurrency requirements.

## Overview

### Scenarios
- **Normal Simulation**: 6 planes arrive randomly, land, dock at gates, refuel and take off.
- **Emergency Simulation**: A congested scenario where 2 planes wait to land, 2 gates are occupied, and a 3rd plane (Plane-6) requests an emergency landing due to fuel shortage.

### Features
- **Concurrency**: Utilizes multithreading, priorities, synchronization and resource management.
- **Statistics**: Reports gate wait times, planes served and passengers boarded at the end of simulation.
- **Safety**: Ensures thread-safe access to shared resources like the runway and gates.

## Prerequisites
- **Java**: JDK 8 or higher.
- **IDE**: Any Java-supporting IDE (IntelliJ IDEA, Eclipse etc.) or CMD.

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/asia-pacific-airport.git
    ```
2. Navigate to the project directory:
   ```bash
   cd asia-pacific-airport
   ```
3. Compile the Java files:
   ```bash
    javac -d bin src/com/main/*.java
    ```
4. Run the simulation:
    ```bash
     java -cp bin com.main.AirportMain
     ```

## Usage

- Launch the program to access the main menu:
  * [1] Normal Simulation
  * [2] Emergency Simulation
  * [3] Exit
- Select an option (1-3) and press Enter.
- After each simulation, review the statistics and press Enter to return to the menu.

## Thread Structure

The simulation consists of the following threads:

* **1x Main Thread:** Menu and simulation initiation.
* **1x ATC Thread:** Runway access and plane scheduling.
* **6x Plane Threads:** Plane-1 to Plane-6.
* **3x Gate Threads:** Gate-1 to Gate-3.
* **1x Refueling Truck Thread:** Refueling purpose.

**Total:** 12 threads

## Critical Sections

The following shared resources are protected to ensure thread safety:

| Resource        | Critical Section                              | Shared Variables                                                                 | Protection                                      |
|-----------------|-----------------------------------------------|----------------------------------------------------------------------------------|-------------------------------------------------|
| Runway (ATC)    | `requestLanding`, `requestTakeoff`, `run()`   | `runwayFree`, `groundCount`, `emergencyQueue`, `landingQueue`, `takeoffQueue`    | `synchronized` on `ATC` instance                |
| Gates + Planes  | Gate locking or release in `run()`            | `gateSemaphore`, `occupied` (in `Gates`)                                         | `Semaphore`, `synchronized` on `Gates` instance |
| Refueling Truck | `requestRefuel`                               | `isBusy`                                                                         | `ReentrantLock`                                 |
| Statistics      | `recordPlaneCompletion`, `recordGateWaitTime` | `planesCompleted`, `totalPassengersBoarded`, `gateWaitTimes`                     | `ReentrantReadWriteLock`                        |

## Log Printing Colors

Logs use ANSI colors for clarity:
* Red: Warnings (e.g., ground capacity) and emergency operations.
* Yellow: Pending plane operations (e.g., requesting landing/takeoff).
* Green: Successful plane operations (e.g., landed, departed).
* Cyan: Gate operations (e.g., disembarking, cleaning).
* Orange: Refueling truck operations.
* Bold Red: Ground count warning announcements.
* Bold Green: Plane cycle completion announcements.
* Bold Cyan: All planes completed announcement.
* Bold Yellow: Statistics summary announcement.