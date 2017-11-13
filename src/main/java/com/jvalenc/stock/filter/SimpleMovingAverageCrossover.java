package com.jvalenc.stock.filter;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by jonat on 11/12/2017.
 */
public class SimpleMovingAverageCrossover {

    public static void main(String[] args){
        try {
            URL url = new URL("https://www.alphavantage.co/query?function=SMA&symbol=MSFT&interval=daily&time_period=8&series_type=close&apikey=B5E9IZNOFQ20MEF7");
            JsonObject jsonObject = Json.parse(IOUtils.toString(url, "UTF-8")).asObject();
            System.out.println(jsonObject.get("Technical Analysis: SMA"));
            JsonObject jsonTechAna = jsonObject.get("Technical Analysis: SMA").asObject();
            System.out.println();
            //probably a good idea to have an object with date and sma attached then parse to be able to sort the dates.
            for(String value : jsonTechAna.names()){
                String v = jsonTechAna.get(value).asObject().get("SMA").asString();
                System.out.println(v);
            }
        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
