package com.jvalenc.stock.models;

import org.apache.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jonat on 11/13/2017.
 */
public class SMADataPoint {
    private Date timeStamp;
    private Logger logger = Logger.getLogger(SMADataPoint.class);
    private double simpleMovingAverage;

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        //logger.info("parsing date string");
        try{
            SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
            this.timeStamp = ft.parse(timeStamp);
        }catch (ParseException e){
            logger.error("wrong date string.");
        }
    }

    public double getSimpleMovingAverage() {
        return simpleMovingAverage;
    }

    public void setSimpleMovingAverage(double simpleMovingAverage) {
        this.simpleMovingAverage = simpleMovingAverage;
    }
}
