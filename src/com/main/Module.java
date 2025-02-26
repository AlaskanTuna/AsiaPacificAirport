// Module.java

package com.main;

import com.main.AirportMain;
import com.main.ATC;
import com.main.Gates;
import com.main.RefuelingTruck;
import com.main.Statistics;

import java.io.IOException;

public class Module {
    // -------------------- Data Fields -------------------- //

    private static final Object printLock = new Object();

    // -------------------- Getters -------------------- //

    private static int getTerminalWidth() {
        String columns = System.getenv("COLUMNS");
        return (columns != null && !columns.isEmpty()) ? Integer.parseInt(columns) : Constants.DIVIDER_LENGTH;
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

    // -------------------- Announcements Reporting -------------------- //

    private static void printAnnouncement(String message, String color, boolean bold) {
        synchronized (printLock) {
            clearScreen();
            if (bold) {
                System.out.print(Constants.ANSI_BOLD);
            }
            System.out.print(color);
            printDivider();
            System.out.print(Constants.INDICATOR);
            System.out.println(message);
            printDivider();
            System.out.print(Constants.ANSI_RESET);
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

    public static void mainMenu() {
        // TODO: Implement Normal Simulation / Emergency Simulation / Exit
    }
}