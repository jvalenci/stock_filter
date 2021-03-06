package com.jvalenc.stock.filter;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.jvalenc.stock.email.client.EmailClient;
import com.jvalenc.stock.models.SMADataPoint;
import com.jvalenc.stock.models.StockSymbol;
import com.jvalenc.stock.util.enums.Trend;
import com.jvalenc.stock.web.rest.AlphaVantageWebClient;
import com.jvalenc.stock.web.rest.IWebClient;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.*;

/**
 * Created by jonat on 1/14/2018.
 */
public class SimpleMovingAverageCrossoverTest {

    @Test
    @Ignore
    public void generateAndSendEmail() throws Exception {

        //Arrange
        boolean expected = true;
        boolean actual;
        Set<StockSymbol> stockSymbols = new HashSet<>();
        for(int i = 0; i < 5; i++){
            StockSymbol stockSymbol = new StockSymbol("testSymbol " + i);
            if( i % 2 == 0) {
                stockSymbol.setTrend(Trend.DOWN);
                stockSymbol.setHasWillR(true);
                stockSymbol.setHasSMACrossover(false);
            } else {
                stockSymbol.setTrend(Trend.UP);
                stockSymbol.setHasWillR(false);
                stockSymbol.setHasSMACrossover(true);
            }
            stockSymbols.add(stockSymbol);
        }

        //Act
        actual = EmailClient.generateAndSendEmail(stockSymbols);

        //Assert
        Assert.assertTrue(actual == expected);
    }

    @Test
    public void parseResponse() throws Exception {

        //Arrange
        List<JsonObject> response = new ArrayList<>();
        SimpleMovingAverageCrossover smaCrossover = new SimpleMovingAverageCrossover(AlphaVantageWebClient.getInstance());
        List< List<SMADataPoint> > actual;
        JsonObject firstDataSet = Json.parse("{\"Meta Data\":{\"1: Symbol\":\"TRNS\",\"2: Indicator\":\"Simple Moving Average (SMA)\",\"3: Last Refreshed\":\"2018-01-12\",\"4: Interval\":\"daily\",\"5: Time Period\":8,\"6: Series Type\":\"close\",\"7: Time Zone\":\"US/Eastern\"},\"Technical Analysis: SMA\":{\"2018-01-12\":{\"SMA\":\"13.7500\"},\"2018-01-11\":{\"SMA\":\"13.7813\"},\"2018-01-10\":{\"SMA\":\"13.8063\"},\"2018-01-09\":{\"SMA\":\"13.8438\"},\"2018-01-08\":{\"SMA\":\"13.8813\"}}}").asObject();
        JsonObject secondDataSet = Json.parse("{\"Meta Data\":{\"1: Symbol\":\"TRNS\",\"2: Indicator\":\"Simple Moving Average (SMA)\",\"3: Last Refreshed\":\"2018-01-12\",\"4: Interval\":\"daily\",\"5: Time Period\":23,\"6: Series Type\":\"close\",\"7: Time Zone\":\"US/Eastern\"},\"Technical Analysis: SMA\":{\"2018-01-12\":{\"SMA\":\"13.6454\"},\"2018-01-11\":{\"SMA\":\"13.6345\"},\"2018-01-10\":{\"SMA\":\"13.6345\"},\"2018-01-09\":{\"SMA\":\"13.6498\"},\"2018-01-08\":{\"SMA\":\"13.6563\"}}}").asObject();
        response.add(firstDataSet);
        response.add(secondDataSet);

        //Act
        actual = smaCrossover.parseResponse(response);

        //Assert
        Assert.assertTrue(actual.size() == 2);
    }

    @Test
    public void analyseForIntersectionFail() throws Exception {

        //Arrange
        SimpleMovingAverageCrossover simpleMovingAverageCrossover = new SimpleMovingAverageCrossover(AlphaVantageWebClient.getInstance());
        StockSymbol stockSymbol = new StockSymbol("AAPL");
        boolean expected = false;
        boolean actual;
        List<JsonObject> response = new ArrayList<>();
        List< List<SMADataPoint> > argument;
        JsonObject firstDataSet = Json.parse("{\"Meta Data\":{\"1: Symbol\":\"TRNS\",\"2: Indicator\":\"Simple Moving Average (SMA)\",\"3: Last Refreshed\":\"2018-01-12\",\"4: Interval\":\"daily\",\"5: Time Period\":8,\"6: Series Type\":\"close\",\"7: Time Zone\":\"US/Eastern\"},\"Technical Analysis: SMA\":{\"2018-01-12\":{\"SMA\":\"13.7500\"},\"2018-01-11\":{\"SMA\":\"13.7813\"},\"2018-01-10\":{\"SMA\":\"13.8063\"},\"2018-01-09\":{\"SMA\":\"13.8438\"},\"2018-01-08\":{\"SMA\":\"13.8813\"}}}").asObject();
        JsonObject secondDataSet = Json.parse("{\"Meta Data\":{\"1: Symbol\":\"TRNS\",\"2: Indicator\":\"Simple Moving Average (SMA)\",\"3: Last Refreshed\":\"2018-01-12\",\"4: Interval\":\"daily\",\"5: Time Period\":23,\"6: Series Type\":\"close\",\"7: Time Zone\":\"US/Eastern\"},\"Technical Analysis: SMA\":{\"2018-01-12\":{\"SMA\":\"13.6454\"},\"2018-01-11\":{\"SMA\":\"13.6345\"},\"2018-01-10\":{\"SMA\":\"13.6345\"},\"2018-01-09\":{\"SMA\":\"13.6498\"},\"2018-01-08\":{\"SMA\":\"13.6563\"}}}").asObject();
        response.add(firstDataSet);
        response.add(secondDataSet);
        argument = simpleMovingAverageCrossover.parseResponse(response);

        //Act
        simpleMovingAverageCrossover.analyseForIntersection(argument, stockSymbol);

        //Assert
        Assert.assertTrue(stockSymbol.isHasSMACrossover() == expected);
    }

    @Test
    public void analyseForIntersectionPass() throws Exception {

        //Arrange
        SimpleMovingAverageCrossover simpleMovingAverageCrossover = new SimpleMovingAverageCrossover(AlphaVantageWebClient.getInstance());
        StockSymbol stockSymbol = new StockSymbol("AAPL");
        boolean expected = true;
        boolean actual;
        List<JsonObject> response = new ArrayList<>();
        List< List<SMADataPoint> > argument;
        JsonObject firstDataSet = Json.parse("{\"Meta Data\":{\"1: Symbol\":\"TRNS\",\"2: Indicator\":\"Simple Moving Average (SMA)\",\"3: Last Refreshed\":\"2018-01-12\",\"4: Interval\":\"daily\",\"5: Time Period\":8,\"6: Series Type\":\"close\",\"7: Time Zone\":\"US/Eastern\"},\"Technical Analysis: SMA\":{\"2018-01-12\":{\"SMA\":\"13.7500\"},\"2018-01-11\":{\"SMA\":\"13.7813\"},\"2018-01-10\":{\"SMA\":\"13.8063\"},\"2018-01-09\":{\"SMA\":\"13.7438\"},\"2018-01-08\":{\"SMA\":\"13.8813\"}}}").asObject();
        JsonObject secondDataSet = Json.parse("{\"Meta Data\":{\"1: Symbol\":\"TRNS\",\"2: Indicator\":\"Simple Moving Average (SMA)\",\"3: Last Refreshed\":\"2018-01-12\",\"4: Interval\":\"daily\",\"5: Time Period\":23,\"6: Series Type\":\"close\",\"7: Time Zone\":\"US/Eastern\"},\"Technical Analysis: SMA\":{\"2018-01-12\":{\"SMA\":\"13.6454\"},\"2018-01-11\":{\"SMA\":\"13.6345\"},\"2018-01-10\":{\"SMA\":\"13.6345\"},\"2018-01-09\":{\"SMA\":\"13.7498\"},\"2018-01-08\":{\"SMA\":\"13.6563\"}}}").asObject();
        response.add(firstDataSet);
        response.add(secondDataSet);
        argument = simpleMovingAverageCrossover.parseResponse(response);

        //Act
        simpleMovingAverageCrossover.analyseForIntersection(argument, stockSymbol);

        //Assert
        Assert.assertTrue(stockSymbol.isHasSMACrossover() == expected);
    }

    //If I'm getting an error I can use this test case to see what the response is. Not an actual test.
    @Test
    @Ignore
    public void integrationThruParseResponse() throws Exception {
        StockSymbol symbol = new StockSymbol("csco");
        Indicator indicator;
        indicator = new SimpleMovingAverageCrossover(AlphaVantageWebClient.getInstance());
        indicator.decorateStockSymbol(symbol);
        indicator = new WilliamsPercentR(AlphaVantageWebClient.getInstance());
        indicator.decorateStockSymbol(symbol);

    }

    @Test
    public void practiceTest() {
        List<Integer> set = new ArrayList<>(Arrays.asList(1,5,7));
        int index = 0;

        getSubsets(set, index);


    }

    private ArrayList<ArrayList<Integer>> getSubsets ( List<Integer> set, int index){
        ArrayList<ArrayList<Integer>> allsubsets;
        if(set.size() == index) { //base case -add empty set
            allsubsets = new ArrayList<ArrayList<Integer>>();
            allsubsets.add(new ArrayList<>());
        } else {
            allsubsets = getSubsets(set, index + 1);
            int item = set.get(index);
            ArrayList<ArrayList<Integer>> moresubsets = new ArrayList<>();
            for (ArrayList<Integer> subset : allsubsets){
                ArrayList<Integer> newsubset = new ArrayList<>();
                newsubset.addAll(subset);
                newsubset.add(item);
                moresubsets.add(newsubset);
            }
            allsubsets.addAll(moresubsets);
        }
        return allsubsets;
    }

    private ArrayList<ArrayList<Integer>> getSubsetInterative( ArrayList<Integer> set) {
        ArrayList<ArrayList<Integer>> response = new ArrayList<>();
        response.add(new ArrayList<>());
        response.add(set);
        for(int i = 0; i < set.size() ; i++) {
            set.add(set.get(i));
            for(int j = 0 ; j < set.size() ; j++) {
                response.add(new ArrayList<Integer>(Arrays.asList(set.get(i), set.get(j))));
            }
        }
        return response;
    }
}