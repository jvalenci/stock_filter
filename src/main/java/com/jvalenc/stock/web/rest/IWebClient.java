package com.jvalenc.stock.web.rest;

import com.eclipsesource.json.JsonObject;

import java.util.List;

/**
 * Created by jonat on 1/13/2018.
 */
public interface IWebClient<T> {
    List<T> send(List<String> requests);
}
