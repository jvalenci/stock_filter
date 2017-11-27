package com.jvalenc.stock.util;

import org.apache.log4j.Logger;

/**
 * Created by jonat on 11/12/2017.
 */
public class Query {
    //fields
    private QueryFunction queryFunction;
    private TimePeriod timePeriod;
    private Interval interval;
    private String symbol;
    private SeriesType seriesType;
    private Logger logger = Logger.getLogger(Query.class);

    //validation
    public Boolean isValid(){
        return queryFunction != null && timePeriod != null && interval != null && symbol != null && seriesType != null;
    }

    //setters and getters
    public QueryFunction getQueryFunction() {
        return queryFunction;
    }

    public void setQueryFunction(QueryFunction queryFunction) {
        this.queryFunction = queryFunction;
    }

    public TimePeriod getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(TimePeriod timePeriod) {
        this.timePeriod = timePeriod;
    }

    public Interval getInterval() {
        return interval;
    }

    public void setInterval(Interval interval) {
        this.interval = interval;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public SeriesType getSeriesType() {
        return seriesType;
    }

    public void setSeriesType(SeriesType seriesType) {
        this.seriesType = seriesType;
    }
}
