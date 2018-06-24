package com.jvalenc.stock.models;

import com.jvalenc.stock.util.enums.Trend;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jonat on 1/13/2018.
 */
public class StockSymbol {
    private String symbol;
    private Trend trend;
    private boolean hasSMACrossover;
    private boolean hasWillR;

    public boolean isHasSMACrossover() {
        return hasSMACrossover;
    }

    public void setHasSMACrossover(boolean hasSMACrossover) {
        this.hasSMACrossover = hasSMACrossover;
    }

    public boolean isHasWillR() {
        return hasWillR;
    }

    public void setHasWillR(boolean hasWillR) {
        this.hasWillR = hasWillR;
    }

    public boolean hasIndicator() {
        return (isHasSMACrossover() || isHasWillR());
    }

    public Trend getTrend() {
        return trend;
    }

    public void setTrend(Trend trend) {
        this.trend = trend;
    }

    /**
     * @return
     */
    @Override
    public String toString() {
        return "StockSymbol{" +
                "symbol='" + symbol + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return symbol.hashCode();
    }

    /**Compare the two symbols
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        StockSymbol compare = (StockSymbol) obj;
        boolean equal = true;
        if(!this.symbol.equals(compare.getSymbol()) ||
                !this.hasWillR == compare.isHasWillR() ||
                !this.hasSMACrossover == compare.isHasSMACrossover()) {
            equal = false;
        }
        return equal;
    }

    /**
     * @return
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * @param symbol
     */
    public void setSymbol(String symbol) {
        //check for a caret and parse accordingly, the caret comes from the stock list I pulled up.
        if(symbol.contains("^")) {
            symbol = symbol.matches(".*\\^.+")? symbol.replace("^",".PR.") : symbol.replace("^", ".PR");
        }
        this.symbol = symbol.trim();
    }

    /**
     *
     * @param symbol
     */
    public StockSymbol(String symbol) {

        setSymbol(symbol);
    }
}
