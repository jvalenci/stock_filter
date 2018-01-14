package com.jvalenc.stock.web.rest;

import com.eclipsesource.json.JsonObject;

/**
 * Created by jonat on 1/13/2018.
 */
public interface IWebClient<T> {
    void sendRequest() throws Exception;
    T getResponse();
}
