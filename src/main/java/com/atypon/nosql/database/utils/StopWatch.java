package com.atypon.nosql.database.utils;

public class StopWatch {
    private long startTime;

    public void start() {
        startTime = System.nanoTime();
    }

    public double end() {
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        return (double) duration / 1e9;
    }
}
