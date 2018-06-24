package com.jvalenc.stock.filter;

import com.eclipsesource.json.JsonObject;
import com.jvalenc.stock.models.QueryCriteria;
import com.jvalenc.stock.models.SMADataPoint;
import com.jvalenc.stock.models.StockSymbol;
import com.jvalenc.stock.util.enums.*;
import com.jvalenc.stock.web.rest.IWebClient;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by jonat on 11/12/2017.
 */
public class SimpleMovingAverageCrossover implements Indicator {
    private static Logger logger = Logger.getLogger(SimpleMovingAverageCrossover.class);
    private IWebClient<JsonObject> webClient;

    public SimpleMovingAverageCrossover(IWebClient<JsonObject> webClient) {
        this.webClient = Objects.requireNonNull(webClient, "Webclient cannot be null.");
    }

    /**
     * For the purpose of simple moving average cross over we need two queries.
     * one for the 8 sma the other for 23 sma.
     * @param stockSymbol
     * @return List of query criteria to make the rest calls.
     */
    protected List<String> queryBuilder(StockSymbol stockSymbol){

        List<QueryCriteria> queries = new ArrayList<>();

        QueryCriteria query = new QueryCriteria();
        query.setSymbol(stockSymbol.getSymbol());
        query.setQueryFunction(QueryFunction.SMA);
        query.setInterval(Interval.DAILY);
        query.setTimePeriod(TimePeriod.EIGHT);
        query.setSeriesType(SeriesType.CLOSE);
        queries.add(query);

        query = new QueryCriteria();
        query.setSymbol(stockSymbol.getSymbol());
        query.setQueryFunction(QueryFunction.SMA);
        query.setInterval(Interval.DAILY);
        query.setTimePeriod(TimePeriod.TWENTY_THREE);
        query.setSeriesType(SeriesType.CLOSE);
        queries.add(query);

        return buildUrls(queries);
    }

    /**Parsing the response. I'm only interested in the first
     * 5 days. that a potential crossover could have occurred.
     * @param response
     * @return Two data sets each containing 5 days worth of data points
     */
    protected List<List<SMADataPoint>> parseResponse(List<JsonObject> response){

        logger.info("parsing the Response \n" + response.toString());

        List< List<SMADataPoint> > dataPoints = new ArrayList<>();

        if(response != null  && response.size() == 2) {
            if( getResponseValueSize(response.get(0)) >= 5 && getResponseValueSize(response.get(1)) >= 5) {
                //parse the response
                response.forEach(
                        jsonObject -> {
                            List<SMADataPoint> data = new ArrayList<>();
//                            logger.debug(jsonObject.get("Technical Analysis: SMA"));
                            JsonObject jsonTechAna = jsonObject.get("Technical Analysis: SMA").asObject();

                            for (int i = 0; i < 5; i++) {

                                String timeStamp = jsonTechAna.names().get(i);
                                String sma = jsonTechAna.get(timeStamp).asObject().get("SMA").asString();
                                SMADataPoint point = new SMADataPoint(Double.parseDouble(sma), timeStamp);
//                                logger.debug(timeStamp + " : " + sma);
                                data.add(point);
                            }
                            dataPoints.add(data);
                        }
                );
            }
        }
        return dataPoints;
    }

    /**
     * determine the trend of the stock based on a 6 month period of 180 days.
     * This will use the SMA of 8 day difference between the first point and the
     * point on the 180th day.
     * @param response
     */
    protected void setTrend(List<JsonObject> response, StockSymbol stockSymbol) {

        if( getResponseValueSize(response.get(0)) > 180) {
            logger.info("Starting to set the trend.");

            JsonObject jsonTechAna = response.get(0).get("Technical Analysis: SMA").asObject();
            //getting and setting trend
            String timeStamp = jsonTechAna.names().get(0);
            String sma = jsonTechAna.get(timeStamp).asObject().get("SMA").asString();

            timeStamp = jsonTechAna.names().get(180);
            String sma2 = jsonTechAna.get(timeStamp).asObject().get("SMA").asString();
            if (Double.parseDouble(sma) - Double.parseDouble(sma2) > 0) {
                stockSymbol.setTrend(Trend.UP);
            } else {
                stockSymbol.setTrend(Trend.DOWN);
            }
            logger.info("Trend has been set.");
        }
    }

    /**Examine both data sets and try to find the intersection within 0.01
     * @param data
     * @return true if the data sets have an intersection: otherwise return false
     */
    protected void analyseForIntersection(List<List<SMADataPoint>> data, StockSymbol stockSymbol){
        logger.info("Starting the analysis for both set of data");

        if(data != null && data.size() == 2 ) {
            if(data.get(0).size() > 0 && data.get(1).size() > 0) {
                List<SMADataPoint> firstDataPoints = data.get(0);
                List<SMADataPoint> secondDataPoints = data.get(1);
                double difference;
                for (int i = 0; i < firstDataPoints.size(); i++) {
                    difference = Math.abs(firstDataPoints.get(i).getValue() - secondDataPoints.get(i).getValue());
                    if (difference <= SMADataPoint.THRESHOLD) {
                        logger.info("Analysis has found an intersection.");
                        stockSymbol.setHasSMACrossover(Boolean.TRUE);
                        break;
                    }
                }
            }
        }
        logger.info("Analysis found no intersection.");
    }

    /**
     * Builds the url with given queries from constructor
     */
    private List<String> buildUrls(List<QueryCriteria> queries){
        logger.info("building URLs from queries");
        List<String> requests = new ArrayList<>();
        if (queries != null && queries.size() == 2) {
            queries.forEach(
                    queryCriteria -> {

                        StringBuilder sb = new StringBuilder();
                        sb.append("function=" + queryCriteria.getQueryFunction().getFunctionName())
                                .append("&")
                                .append("symbol=" + queryCriteria.getSymbol())
                                .append("&")
                                .append("interval=" + queryCriteria.getInterval().getInterval())
                                .append("&")
                                .append("time_period=" + queryCriteria.getTimePeriod().getTimePeriod())
                                .append("&")
                                .append("series_type=" + queryCriteria.getSeriesType().getSeriesType());

                        requests.add(sb.toString());
                    }
            );
        }else{
            logger.error("query Criterias were null or there wasn't 2 queryCriterias");
        }
        return requests;
    }

    /**
     * Gets the response value size
     * @param response
     * @return
     */
    private int getResponseValueSize(final JsonObject response){
        int size = 0;
        if(!response.isEmpty()) {
            JsonObject sizeObject = response.get("Technical Analysis: SMA").asObject();
            size = sizeObject.size();
        }
        return size;
    }

    @Override
    public void decorateStockSymbol(StockSymbol stockSymbol) {
        List<String> requests = queryBuilder(stockSymbol);
        List<JsonObject> response = webClient.send(requests);
        setTrend(response, stockSymbol);
        List<List<SMADataPoint>> parseResponse = parseResponse(response);
        analyseForIntersection(parseResponse, stockSymbol);
    }
}
