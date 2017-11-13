package com.jvalenc.stock.filter;

/**
 * Created by jonat on 11/12/2017.
 */
public enum Interval {
    DAILY("daily")
    ;

    private final String interval;

    Interval(String interval){
        this.interval = interval;
    }

    public String getInterval(){
        return interval;
    }
}
