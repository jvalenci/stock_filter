package com.jvalenc.stock.filter;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.jvalenc.stock.models.QueryCriteria;
import com.jvalenc.stock.models.SMADataPoint;
import com.jvalenc.stock.models.StockSymbol;
import com.jvalenc.stock.web.rest.AlphaVantageWebClient;
import com.jvalenc.stock.web.rest.IWebClient;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by jonat on 1/14/2018.
 */
public class SimpleMovingAverageCrossoverTest {

    @Test
    public void generateAndSendEmail() throws Exception {

        //Arrange
        boolean expected = true;
        boolean actual;
        Set<StockSymbol> stockSymbols = new HashSet<>();
        for(int i = 0; i < 5; i++){
            stockSymbols.add(new StockSymbol("testSymbol " + i));
        }

        //Act
        actual = SimpleMovingAverageCrossover.generateAndSendEmail(stockSymbols);

        //Assert
        Assert.assertTrue(actual == expected);
    }

    @Test
    public void parseResponse() throws Exception {

        //Arrange
        List<JsonObject> response = new ArrayList<>();
        List< List<SMADataPoint> > actual;
        JsonObject firstDataSet = Json.parse("{\"Meta Data\":{\"1: Symbol\":\"TRNS\",\"2: Indicator\":\"Simple Moving Average (SMA)\",\"3: Last Refreshed\":\"2018-01-12\",\"4: Interval\":\"daily\",\"5: Time Period\":8,\"6: Series Type\":\"close\",\"7: Time Zone\":\"US/Eastern\"},\"Technical Analysis: SMA\":{\"2018-01-12\":{\"SMA\":\"13.7500\"},\"2018-01-11\":{\"SMA\":\"13.7813\"},\"2018-01-10\":{\"SMA\":\"13.8063\"},\"2018-01-09\":{\"SMA\":\"13.8438\"},\"2018-01-08\":{\"SMA\":\"13.8813\"}}}").asObject();
        JsonObject secondDataSet = Json.parse("{\"Meta Data\":{\"1: Symbol\":\"TRNS\",\"2: Indicator\":\"Simple Moving Average (SMA)\",\"3: Last Refreshed\":\"2018-01-12\",\"4: Interval\":\"daily\",\"5: Time Period\":23,\"6: Series Type\":\"close\",\"7: Time Zone\":\"US/Eastern\"},\"Technical Analysis: SMA\":{\"2018-01-12\":{\"SMA\":\"13.6454\"},\"2018-01-11\":{\"SMA\":\"13.6345\"},\"2018-01-10\":{\"SMA\":\"13.6345\"},\"2018-01-09\":{\"SMA\":\"13.6498\"},\"2018-01-08\":{\"SMA\":\"13.6563\"}}}").asObject();
        response.add(firstDataSet);
        response.add(secondDataSet);

        //Act
        actual = SimpleMovingAverageCrossover.parseResponse(response);

        //Assert
        Assert.assertTrue(actual.size() == 2);
    }

    @Test
    public void analyseForIntersectionFail() throws Exception {

        //Arrange
        boolean expected = false;
        boolean actual;
        List<JsonObject> response = new ArrayList<>();
        List< List<SMADataPoint> > argument;
        JsonObject firstDataSet = Json.parse("{\"Meta Data\":{\"1: Symbol\":\"TRNS\",\"2: Indicator\":\"Simple Moving Average (SMA)\",\"3: Last Refreshed\":\"2018-01-12\",\"4: Interval\":\"daily\",\"5: Time Period\":8,\"6: Series Type\":\"close\",\"7: Time Zone\":\"US/Eastern\"},\"Technical Analysis: SMA\":{\"2018-01-12\":{\"SMA\":\"13.7500\"},\"2018-01-11\":{\"SMA\":\"13.7813\"},\"2018-01-10\":{\"SMA\":\"13.8063\"},\"2018-01-09\":{\"SMA\":\"13.8438\"},\"2018-01-08\":{\"SMA\":\"13.8813\"}}}").asObject();
        JsonObject secondDataSet = Json.parse("{\"Meta Data\":{\"1: Symbol\":\"TRNS\",\"2: Indicator\":\"Simple Moving Average (SMA)\",\"3: Last Refreshed\":\"2018-01-12\",\"4: Interval\":\"daily\",\"5: Time Period\":23,\"6: Series Type\":\"close\",\"7: Time Zone\":\"US/Eastern\"},\"Technical Analysis: SMA\":{\"2018-01-12\":{\"SMA\":\"13.6454\"},\"2018-01-11\":{\"SMA\":\"13.6345\"},\"2018-01-10\":{\"SMA\":\"13.6345\"},\"2018-01-09\":{\"SMA\":\"13.6498\"},\"2018-01-08\":{\"SMA\":\"13.6563\"}}}").asObject();
        response.add(firstDataSet);
        response.add(secondDataSet);
        argument = SimpleMovingAverageCrossover.parseResponse(response);

        //Act
        actual = SimpleMovingAverageCrossover.analyseForIntersection(argument);

        //Assert
        Assert.assertTrue(actual == expected);
    }

    @Test
    public void analyseForIntersectionPass() throws Exception {

        //Arrange
        boolean expected = true;
        boolean actual;
        List<JsonObject> response = new ArrayList<>();
        List< List<SMADataPoint> > argument;
        JsonObject firstDataSet = Json.parse("{\"Meta Data\":{\"1: Symbol\":\"TRNS\",\"2: Indicator\":\"Simple Moving Average (SMA)\",\"3: Last Refreshed\":\"2018-01-12\",\"4: Interval\":\"daily\",\"5: Time Period\":8,\"6: Series Type\":\"close\",\"7: Time Zone\":\"US/Eastern\"},\"Technical Analysis: SMA\":{\"2018-01-12\":{\"SMA\":\"13.7500\"},\"2018-01-11\":{\"SMA\":\"13.7813\"},\"2018-01-10\":{\"SMA\":\"13.8063\"},\"2018-01-09\":{\"SMA\":\"13.7438\"},\"2018-01-08\":{\"SMA\":\"13.8813\"}}}").asObject();
        JsonObject secondDataSet = Json.parse("{\"Meta Data\":{\"1: Symbol\":\"TRNS\",\"2: Indicator\":\"Simple Moving Average (SMA)\",\"3: Last Refreshed\":\"2018-01-12\",\"4: Interval\":\"daily\",\"5: Time Period\":23,\"6: Series Type\":\"close\",\"7: Time Zone\":\"US/Eastern\"},\"Technical Analysis: SMA\":{\"2018-01-12\":{\"SMA\":\"13.6454\"},\"2018-01-11\":{\"SMA\":\"13.6345\"},\"2018-01-10\":{\"SMA\":\"13.6345\"},\"2018-01-09\":{\"SMA\":\"13.7498\"},\"2018-01-08\":{\"SMA\":\"13.6563\"}}}").asObject();
        response.add(firstDataSet);
        response.add(secondDataSet);
        argument = SimpleMovingAverageCrossover.parseResponse(response);

        //Act
        actual = SimpleMovingAverageCrossover.analyseForIntersection(argument);

        //Assert
        Assert.assertTrue(actual == expected);
    }

    //If I'm getting an error I can use this test case to see what the response is. Not an actual test.
    @Test
    public void integrationThruParseResponse() throws Exception {
        List<QueryCriteria> queryCriterias = SimpleMovingAverageCrossover.queryBuilder(new StockSymbol("SMED"));
        IWebClient<JsonObject> webClient = new AlphaVantageWebClient(queryCriterias);
        List<JsonObject> response = SimpleMovingAverageCrossover.webService(webClient);
        List< List<SMADataPoint> > parsedResponse = SimpleMovingAverageCrossover.parseResponse(response);

    }
}