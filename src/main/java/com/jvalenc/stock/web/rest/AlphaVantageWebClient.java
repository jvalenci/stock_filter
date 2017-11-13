package com.jvalenc.stock.web.rest;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.jvalenc.stock.util.*;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;

/**
 * Created by jonat on 11/12/2017.
 */
public class AlphaVantageWebClient {

    //this key was auto generated by alpha vantage
    private String API_KEY = "B5E9IZNOFQ20MEF7";
    private String BASE_URL = "https://www.alphavantage.co/query?";
    private String AND = "&";
    private Query query;
    private String request;
    private JsonObject response;

    public AlphaVantageWebClient(Query query){
        this.query = query;
        if(this.query.isValid()) {
            buildUrl();
        }
    }

    public AlphaVantageWebClient(){
        query = new Query();
        query.setInterval(Interval.DAILY);
        query.setQueryFunction(QueryFunction.SMA);
        query.setSymbol("SPY");
        query.setSeriesType(SeriesType.CLOSE);
        query.setTimePeriod(TimePeriod.EIGHT);

        if(query.isValid()) {
            buildUrl();
        }
    }

    private void buildUrl(){

        if (query != null) {

            StringBuilder sb = new StringBuilder();
            sb.append(BASE_URL);
            sb.append("function=" + query.getQueryFunction().getFunctionName());
            sb.append(AND);
            sb.append("symbol=" + query.getSymbol());
            sb.append(AND);
            sb.append("interval=" + query.getInterval().getInterval());
            sb.append(AND);
            sb.append("time_period=" + query.getTimePeriod().getTimePeriod());
            sb.append(AND);
            sb.append("series_type=" + query.getSeriesType().getSeriesType());
            sb.append(AND);
            sb.append("apikey=" + API_KEY);

            request = sb.toString();
        }
    }

    public void sendRequest(){

        if(request != null) {
            try {
                URL url = new URL(request);
                response = Json.parse(IOUtils.toString(url, "UTF-8")).asObject();
            } catch (IOException e) {
                System.err.println("There was an error with the request string: " + e.getMessage());
            }
        }
    }

    public JsonObject getResponse(){
        return response;
    }
}
