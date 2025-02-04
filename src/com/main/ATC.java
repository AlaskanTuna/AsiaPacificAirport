package com.main;

import com.main.data.Logging;
import com.main.data.Statistics;

public class ATC implements Runnable {
    // Data fields
    private Logger logger;
    private Statistics statistics;
    private boolean runwayFree = true;
    private int groundCount = 0;
    private int maxGroundCount = 3;

    // Constructor
    public ATC(Logger logger, Statistics statistics) {
        this.logger = logger;
        this.statistics = statistics;
    }

    // Methods
    public void requestLanding() {

    }

    public void landingComplete() {

    }

    public void requestTakeoff() {

    }

    public void takeoffComplete() {

    }
}