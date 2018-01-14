package com.jvalenc.stock.models;

/**
 * Created by jonat on 1/13/2018.
 */
public class StockSymbol {
    private String symbol;

    /**
     * @return
     */
    @Override
    public String toString() {
        return "StockSymbol{" +
                "symbol='" + symbol + '\'' +
                '}';
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
