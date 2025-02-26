package com.main;

public class Constants {
    // -------------------- ATC Constants -------------------- //

    public static final int RUNWAY_CHECK_TIME_MS = 1000;

    // -------------------- Plane Constants -------------------- //

    public static final int MAX_GROUND = 3; // Max planes on the ground (incl. runway)
    public static final int NUM_PLANES = 6;
    public static final int LANDING_REQUEST_TIME_MS = 2000;
    public static final int LANDING_TIME_MS = 3000;
    public static final int TAKEOFF_TIME_MS = 3000;
    public static final int PLANE_ARRIVAL_MAX_DELAY_MS = 5000;

    // -------------------- Gate Constants -------------------- //

    public static final int NUM_GATES = 3;
    public static final int GATE_DOCKING_TIME_MS = 2000;
    public static final int GATE_OPERATION_TIME_MS = 500;

    // -------------------- Refueling Truck Constants -------------------- //

    public static final int REFUEL_TIME_MS = 3000;

    // Menu constants

    public static final String DIVIDER_LINE = "-";
    public static final String INDICATOR = ">>> ";
    public static final int DIVIDER_LENGTH = 80;

    // -------------------- ANSI Escape Codes -------------------- //

    public static final String ANSI_CLEAR_SCREEN = "\u001B[2J\u001B[H";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_BOLD = "\u001B[1m";

    // -------------------- Menu Constants -------------------- //

    public static final String MENU_TITLE = "Airport Management System";

    // -------------------- Message Constants -------------------- //
    public static final String WAIT_MSG = "Press Enter key to continue...";
}