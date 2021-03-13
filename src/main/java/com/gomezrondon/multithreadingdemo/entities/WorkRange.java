package com.gomezrondon.multithreadingdemo.entities;

public class WorkRange {
    public Long iniRange = 0l;
    public Long endRange = 0l;

    @Override
    public String toString() {
        return "WorkRange{" +
                "iniRange=" + iniRange +
                ", endRange=" + endRange +
                '}';
    }
}
