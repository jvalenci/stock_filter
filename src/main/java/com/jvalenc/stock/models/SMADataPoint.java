package com.jvalenc.stock.models;

/**
 * Created by jonat on 11/13/2017.
 */
public class SMADataPoint extends DataPoint {

    public static final double THRESHOLD = 0.01;
    public SMADataPoint(double value, String date) {
        super(value, date);
    }
}
