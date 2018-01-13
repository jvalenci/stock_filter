package com.jvalenc.stock.models;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by jonat on 1/13/2018.
 */
public class StockTickerTest {
    @Test
    public void setTicker() throws Exception {

        //Arrange
        String testString = "AAPL^A";
        String testString2 = "AAPL^";
        String testString3 = "AAPL";
        String testString4 = "AAPL^AB";

        String expected = "AAPL.PR.A";
        String expected2 = "AAPL.PR";
        String expected3 = "AAPL";
        String expected4 = "AAPL.PR.AB";

        //Act
        StockTicker stockTicker = new StockTicker(testString);
        StockTicker stockTicker2 = new StockTicker(testString2);
        StockTicker stockTicker3 = new StockTicker(testString3);
        StockTicker stockTicker4 = new StockTicker(testString4);

        //Assert
        Assert.assertTrue(stockTicker.getTicker().equals(expected));
        Assert.assertTrue(stockTicker2.getTicker().equals(expected2));
        Assert.assertTrue(stockTicker3.getTicker().equals(expected3));
        Assert.assertTrue(stockTicker4.getTicker().equals(expected4));
    }

}