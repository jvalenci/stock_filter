package com.jvalenc.stock.models;

import org.junit.Assert;
import org.junit.Test;

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
        StockSymbol stockTicker = new StockSymbol(testString);
        StockSymbol stockTicker2 = new StockSymbol(testString2);
        StockSymbol stockTicker3 = new StockSymbol(testString3);
        StockSymbol stockTicker4 = new StockSymbol(testString4);

        //Assert
        Assert.assertTrue(stockTicker.getTicker().equals(expected));
        Assert.assertTrue(stockTicker2.getTicker().equals(expected2));
        Assert.assertTrue(stockTicker3.getTicker().equals(expected3));
        Assert.assertTrue(stockTicker4.getTicker().equals(expected4));
    }

}