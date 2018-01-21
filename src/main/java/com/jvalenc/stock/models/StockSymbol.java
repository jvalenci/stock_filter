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
        return this.symbol.equals(compare.getSymbol());
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
