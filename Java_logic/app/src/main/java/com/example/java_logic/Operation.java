package com.example.java_logic;

public class Operation {
    private String type;
    private double price;
    private  String comment;
    private long timestamp;

    public Operation(String type, double price, String comment, long timestamp) {
        this.type = type;
        this.price = price;
        this.comment = comment;
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public String getComment() {
        return comment;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
