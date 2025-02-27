// Module.java

package com.main;

import com.main.AirportMain;
import com.main.ATC;
import com.main.Gates;
import com.main.RefuelingTruck;
import com.main.Statistics;

import java.io.IOException;
import java.util.Scanner;

public class Module {
    // -------------------- Data Fields -------------------- //

    private static final Object printLock = new Object();
    public static long startTime = 0;

    // -------------------- Getters -------------------- //

    private static int getTerminalWidth() {
        String columns = System.getenv("COLUMNS");
        return (columns != null && !columns.isEmpty()) ? Integer.parseInt(columns) : Constants.DIVIDER_LENGTH;
    }

    public static String getTimecode() {
        if (startTime == 0) return "[00:00]";
        long elapsedMs = System.currentTimeMillis() - startTime;
        long seconds = (elapsedMs / 1000) % 60;
        long minutes = (elapsedMs / (1000 * 60)) % 60;
        return String.format("[%02d:%02d]", minutes, seconds);
    }

    // -------------------- Time Management -------------------- //

    public static void startTime() {
        startTime = System.currentTimeMillis();
    }

    public static void resetTime() {
        startTime = 0;
    }

    // -------------------- Print Utilities -------------------- //

    private static void clearScreen() {
        System.out.print(Constants.ANSI_CLEAR_SCREEN);
        System.out.flush();
    }

    private static void printDivider() {
        int width = getTerminalWidth();
        System.out.println(new String(new char[width]).replace('\0', Constants.DIVIDER_LINE.charAt(0)));
    }

    public static void printMessage(String message) {
        synchronized (printLock) {
            clearScreen();
            System.out.println(message);
        }
    }

    public static void printStatsMessage(String message) {
        synchronized (printLock) {
            clearScreen();
            System.out.print(Constants.ANSI_YELLOW);
            System.out.println(AirportMain.getTimecode() + " " + message);
            System.out.print(Constants.ANSI_RESET);
        }
    }

    // -------------------- Announcements Reporting -------------------- //

    private static void printAnnouncement(String message, String color, boolean bold) {
        synchronized (printLock) {
            clearScreen();
            if (bold) System.out.print(Constants.ANSI_BOLD);
            System.out.print(color);
            printDivider();
            System.out.print(Constants.INDICATOR);
            System.out.println(message);
            printDivider();
            System.out.print(Constants.ANSI_RESET);
            if (message.contains("Simulation complete")) {
                System.out.println("Simulation Completed.");
                System.out.println(Constants.CONTINUE_MSG);
                try {
                    System.in.read();
                } catch (IOException ignored) {}
                resetTime();
            }
        }
    }

    public static void announceGroundCountWarning(int groundCount, boolean bold) {
        String message = String.format("%s [ATC] Ground count is at maximum capacity: %d.",
                AirportMain.getTimecode(), groundCount);
        printAnnouncement(message, Constants.ANSI_RED, bold);
    }

    public static void announcePlaneCompletion(int planeId, int totalCompleted, boolean bold) {
        String message = String.format("%s [%s] Plane %d has completed its cycle. Total completed: %d.",
                AirportMain.getTimecode(), Thread.currentThread().getName(), planeId, totalCompleted);
        printAnnouncement(message, Constants.ANSI_GREEN, bold);
    }

    public static void announceFinalStatistics(int totalCompleted, boolean bold) {
        String message = String.format("%s [%s] Simulation complete. Total planes completed: %d.",
                AirportMain.getTimecode(), Thread.currentThread().getName(), totalCompleted);
        printAnnouncement(message, Constants.ANSI_CYAN, bold);
    }

    // -------------------- Menus -------------------- //

    public static void mainMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            clearScreen();
            printDivider();
            System.out.println(Constants.MENU_TITLE);
            printDivider();
            System.out.println("[1] Normal Simulation");
            System.out.println("[2] Emergency Simulation");
            System.out.println("[3] Exit");
            printDivider();
            System.out.print("Enter your choice (1-3): ");

            String input = scanner.nextLine();
            switch (input) {
                case "1":
                    startTime();
                    AirportMain.normalSimulation(new String[0]);
                    break;
                case "2":
                    startTime();
                    AirportMain.emergencySimulation(new String[0]);
                    break;
                case "3":
                    running = false;
                    clearScreen();
                    System.out.println("Exiting simulation.");
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1, 2, or 3.");
            }
        }
        scanner.close();
    }
}