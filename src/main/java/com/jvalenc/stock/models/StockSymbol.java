package com.jvalenc.stock.models;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jonat on 1/13/2018.
 */
public class StockSymbol {
    private String ticker;

    /**
     * @return
     */
    @Override
    public String toString() {
        return "StockSymbol{" +
                "ticker='" + ticker + '\'' +
                '}';
    }

    /**
     * @return
     */
    public String getTicker() {
        return ticker;
    }

    /**
     * @param ticker
     */
    public void setTicker(String ticker) {
        if(ticker.contains("^")) {
            //Checks if there is one or more characters after the "^".
            Pattern patternWordsAfter = Pattern.compile(".*\\^.+");
           //Pattern patternJustCaret = Pattern.compile("\\w\\^");
            //Matcher m1 = patternJustCaret.matcher(ticker);
            Matcher m2 = patternWordsAfter.matcher(ticker);
            boolean b = m2.matches();
            ticker = b? ticker.replace("^",".PR.") : ticker.replace("^", ".PR");
        }
        this.ticker = ticker;
    }

    /**
     *
     * @param ticker
     */
    public StockSymbol(String ticker) {

        setTicker(ticker);
    }
}
