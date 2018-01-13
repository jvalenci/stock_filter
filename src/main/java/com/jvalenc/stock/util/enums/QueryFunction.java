package com.jvalenc.stock.util.enums;

/**
 * Created by jonat on 11/12/2017.
 */
public enum QueryFunction {
    SMA("SMA")
    ;

    private final String functionName;

    QueryFunction(String functionName){
        this.functionName = functionName;
    }

    public String getFunctionName(){
        return this.functionName;
    }
}
