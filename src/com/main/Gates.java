package com.main;

import com.main.data.Logging;
import com.main.data.Statistics;

public class Gates implements Runnable {
    // Data fields
    private Logger logger;
    private int gateId;
    private boolean gateOccupied = false;

    // Constructor
    public Gates(Logger logger, int gateId) {
        this.logger = logger;
        this.gateId = gateId;
    }

    // Methods

}