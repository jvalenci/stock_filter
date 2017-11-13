package com.jvalenc.stock.util;

/**
 * Created by jonat on 11/12/2017.
 */
public enum SeriesType {
    CLOSE("close")
    ;

    private final String seriesType;

    SeriesType(String seriesType){
        this.seriesType = seriesType;
    }

    public String getSeriesType(){
        return seriesType;
    }
}
