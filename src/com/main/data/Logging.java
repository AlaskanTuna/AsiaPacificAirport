package com.main.data;

public class Logging {
    public void log(String message) {
        synchronized (this) {
            System.out.println("[" + Thread.currentThread().getName() + "] " + message);
        }
    }
}