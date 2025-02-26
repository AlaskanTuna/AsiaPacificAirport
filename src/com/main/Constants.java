package com.main;

public class Constants {
    // ATC constants
    public static final int RUNWAY_CHECK_TIME_MS = 1000;

    // Plane constants
    public static final int MAX_GROUND = 3; // Max planes on the ground (incl. runway)
    public static final int NUM_PLANES = 6;
    public static final int LANDING_REQUEST_TIME_MS = 2000;
    public static final int LANDING_TIME_MS = 3000;
    public static final int TAKEOFF_TIME_MS = 3000;
    public static final int PLANE_ARRIVAL_MAX_DELAY_MS = 5000;

    // Gate constants
    public static final int NUM_GATES = 3;
    public static final int GATE_DOCKING_TIME_MS = 2000;
    public static final int GATE_OPERATION_TIME_MS = 500;

    // Refuel truck constants
    public static final int REFUEL_TIME_MS = 3000;
}