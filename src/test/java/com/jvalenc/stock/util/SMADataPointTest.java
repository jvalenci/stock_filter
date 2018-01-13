package com.jvalenc.stock.util;

import com.jvalenc.stock.models.SMADataPoint;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by jonat on 11/13/2017.
 */
public class SMADataPointTest {

    @org.junit.Test
    public void getAndSetTimeStamp() throws Exception {

        //arrange
        SMADataPoint smaDataPoint = new SMADataPoint();
        //act
        smaDataPoint.setTimeStamp("2017-11-10");
        Date actual = smaDataPoint.getTimeStamp();
        //assert
        assertNotNull(actual);
        assertTrue("This is not of a Date type", actual instanceof Date);
    }

    @org.junit.Test
    public void getAndSetSimpleMovingAverage() throws Exception {

        //arrange
        SMADataPoint smaDataPoint = new SMADataPoint();
        //act
        double expected = 12.2432;
        smaDataPoint.setSimpleMovingAverage(expected);
        double actual = smaDataPoint.getSimpleMovingAverage();
        //assert
        assertNotNull(actual);
        assertTrue("Values are not the same", actual == expected);
    }
}