package com.jvalenc.stock.filter;

import com.eclipsesource.json.JsonObject;
import com.jvalenc.stock.models.StockSymbol;
import com.jvalenc.stock.models.WillRDataPoint;
import com.jvalenc.stock.web.rest.IWebClient;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class WilliamsPercentR implements Indicator {

    private IWebClient<JsonObject> webClient;

    protected WilliamsPercentR(IWebClient<JsonObject> webClient) {
        this.webClient = Objects.requireNonNull(webClient, "Webclient cannot be null");
    }

    /**
     * Build the query String
     * @param stockSymbol
     * @return
     */
    protected List<String> queryBuilder(StockSymbol stockSymbol) {
        Objects.requireNonNull(stockSymbol);
        StringBuilder sb = new StringBuilder();
        sb.append("function=WILLR")
                .append("&")
                .append("symbol=" + stockSymbol.getSymbol())
                .append("&")
                .append("interval=daily")
                .append("&")
                .append("time_period=10");

        return Collections.singletonList(sb.toString());
    }

    /**
     * Finds if the stock meets my requirements for williams percent R
     * @param response
     * @param stockSymbol
     */
    protected void parseResponse(List<JsonObject> response, StockSymbol stockSymbol) {
        Objects.requireNonNull(response, "response cannot be null");
        Objects.requireNonNull(stockSymbol, "stock symbol cannot be null");

        //parse the response
        response.forEach(
                jsonObject -> {
//                            logger.debug(jsonObject.get("Technical Analysis: SMA"));
                    JsonObject jsonTechAna = jsonObject.get("Technical Analysis: WILLR").asObject();

                    for (int i = 0; i < 5; i++) {
                        String timeStamp = jsonTechAna.names().get(i);
                        String willr = jsonTechAna.get(timeStamp).asObject().get("WILLR").asString();
                        WillRDataPoint point = new WillRDataPoint(Double.parseDouble(willr), timeStamp);
//                                logger.debug(timeStamp + " : " + sma);
                        if(point.getValue() <= WillRDataPoint.THRESHOLD) {
                           stockSymbol.setHasWillR(Boolean.TRUE);
                           break;
                        }
                    }
                }
        );

    }

    /**
     * orchestrates the decoration of the symbol if there is a williams Percent R
     * @param stockSymbol
     */
    @Override
    public void decorateStockSymbol(StockSymbol stockSymbol) {
        List<String> requests = queryBuilder(stockSymbol);
        List<JsonObject> response = webClient.send(requests);
        parseResponse(response, stockSymbol);
    }
}