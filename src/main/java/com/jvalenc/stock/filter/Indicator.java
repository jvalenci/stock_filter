package com.jvalenc.stock.filter;

import com.eclipsesource.json.JsonObject;
import com.jvalenc.stock.models.StockSymbol;
import com.jvalenc.stock.web.rest.IWebClient;

public interface Indicator {
    void decorateStockSymbol(StockSymbol stockSymbol);
}
