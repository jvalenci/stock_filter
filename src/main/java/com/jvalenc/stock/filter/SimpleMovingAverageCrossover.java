package com.jvalenc.stock.filter;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.jvalenc.stock.util.*;
import com.jvalenc.stock.web.rest.AlphaVantageWebClient;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by jonat on 11/12/2017.
 */
public class SimpleMovingAverageCrossover {

    public static void main(String[] args) {

        Query query = new Query();
        query.setSymbol("SPY");
        query.setQueryFunction(QueryFunction.SMA);
        query.setInterval(Interval.DAILY);
        query.setTimePeriod(TimePeriod.EIGHT);
        query.setSeriesType(SeriesType.CLOSE);

        AlphaVantageWebClient alphaVantageWebClient = new AlphaVantageWebClient(query);
        alphaVantageWebClient.sendRequest();
        JsonObject jsonObject = alphaVantageWebClient.getResponse();
        System.out.println(jsonObject.get("Technical Analysis: SMA"));
        JsonObject jsonTechAna = jsonObject.get("Technical Analysis: SMA").asObject();
        System.out.println();
        //probably a good idea to have an object with date and sma attached then parse to be able to sort the dates.
        for (String value : jsonTechAna.names()) {
            String v = jsonTechAna.get(value).asObject().get("SMA").asString();
            System.out.println(v);
        }
    }
}
