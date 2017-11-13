package com.jvalenc.stock.util;

/**
 * Created by jonat on 11/12/2017.
 */
public enum TimePeriod {
    EIGHT(8),
    TWENTY_THREE(23)
    ;

    //field
    private final int timePeriod;

    //constructor
    TimePeriod(int timePeriod){
        this.timePeriod = timePeriod;
    }

    public int getTimePeriod(){
        return timePeriod;
    }

}
