package com.gomezrondon.multithreadingdemo.entities;

public enum BatchStatus {
    INITIAL("I") ,
    START("S"),
    PROCESSING("P"),
    FINISHED("F"),
    ERROR("E");

    private String value;
    BatchStatus(String code) {
        this.value = code;
    }

    public String getValue() {
        return value;
    }
}
