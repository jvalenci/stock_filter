package com.jvalenc.stock.web.rest;

import java.util.List;

/**
 * Created by jonat on 1/13/2018.
 */
public interface IWebClient<T> {
    void sendRequest() throws Exception;
    List<T> getResponses();
}
