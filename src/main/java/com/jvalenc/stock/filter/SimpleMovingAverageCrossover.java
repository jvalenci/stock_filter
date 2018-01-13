package com.jvalenc.stock.filter;

import com.eclipsesource.json.JsonObject;
import com.jvalenc.stock.models.Query;
import com.jvalenc.stock.util.enums.Interval;
import com.jvalenc.stock.util.enums.QueryFunction;
import com.jvalenc.stock.util.enums.SeriesType;
import com.jvalenc.stock.util.enums.TimePeriod;
import com.jvalenc.stock.web.rest.AlphaVantageWebClient;
import org.apache.log4j.Logger;

/**
 * Created by jonat on 11/12/2017.
 */
public class SimpleMovingAverageCrossover {

    public static void main(String[] args) {
        Logger LOG = Logger.getLogger(SimpleMovingAverageCrossover.class);

        //csv reader
        //todo make csv reader
        //build query
        //todo make query builder
        Query query = new Query();
        query.setSymbol("SPY");
        query.setQueryFunction(QueryFunction.SMA);
        query.setInterval(Interval.DAILY);
        query.setTimePeriod(TimePeriod.EIGHT);
        query.setSeriesType(SeriesType.CLOSE);

        //send the query to the web client
        AlphaVantageWebClient alphaVantageWebClient = new AlphaVantageWebClient(query);
        alphaVantageWebClient.sendRequest();

        //todo maker response parser
        //parse the response
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
