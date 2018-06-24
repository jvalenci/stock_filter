package com.jvalenc.stock.models;

import org.apache.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataPoint {
    private Logger logger = Logger.getLogger(DataPoint.class);
    private Date timeStamp;
    private double value;

    DataPoint(double value, String date) {
        this.value = value;
        setTimeStamp(date);
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Date getTimeStamp() {
        return new Date(timeStamp.getTime());
    }

    public void setTimeStamp(String timeStamp) {
        logger.info("parsing date string");
        try{
            SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
            this.timeStamp = ft.parse(timeStamp);
        }catch (ParseException e){
            logger.error("wrong date string.");
        }
    }
}
