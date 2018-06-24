package com.jvalenc.stock.models;

public class WillRDataPoint extends DataPoint {

    public static final int THRESHOLD = -80;

    public WillRDataPoint(double value, String date) {
        super(value, date);
    }
}
