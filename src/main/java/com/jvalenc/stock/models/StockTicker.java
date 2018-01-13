package com.jvalenc.stock.models;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jonat on 1/13/2018.
 */
public class StockTicker {
    private String ticker;

    /**
     * @return
     */
    @Override
    public String toString() {
        return "StockTicker{" +
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
    public StockTicker(String ticker) {

        setTicker(ticker);
    }
}
