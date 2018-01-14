package com.jvalenc.stock.filter;

import com.eclipsesource.json.JsonObject;
import com.jvalenc.stock.csv.reader.CsvReader;
import com.jvalenc.stock.csv.reader.ICsvReader;
import com.jvalenc.stock.models.QueryCriteria;
import com.jvalenc.stock.models.StockSymbol;
import com.jvalenc.stock.util.enums.Interval;
import com.jvalenc.stock.util.enums.QueryFunction;
import com.jvalenc.stock.util.enums.SeriesType;
import com.jvalenc.stock.util.enums.TimePeriod;
import com.jvalenc.stock.web.rest.AlphaVantageWebClient;
import com.jvalenc.stock.web.rest.IWebClient;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by jonat on 11/12/2017.
 */
public class SimpleMovingAverageCrossover {
    private static Logger LOG = Logger.getLogger(SimpleMovingAverageCrossover.class);

    /**
     * For the purpose of simple moving average cross over we need two queries.
     * one for the 8 sma the other for 23 sma.
     * @param stockSymbol
     * @return
     */
    private static List<QueryCriteria> queryBuilder(StockSymbol stockSymbol){

        List<QueryCriteria> queries = new ArrayList<>();

        QueryCriteria query = new QueryCriteria();
        query.setSymbol(stockSymbol.getSymbol());
        query.setQueryFunction(QueryFunction.SMA);
        query.setInterval(Interval.DAILY);
        query.setTimePeriod(TimePeriod.EIGHT);
        query.setSeriesType(SeriesType.CLOSE);

        queries.add(query);

        query.setTimePeriod(TimePeriod.TWENTY_THREE);

        queries.add(query);

        return queries;
    }

    /**
     * @param csvReader
     * @param directoryFileName
     * @return
     */
    private static Set<StockSymbol> getStockSymbols(ICsvReader<StockSymbol> csvReader, String directoryFileName){
        return csvReader.readCsvDirectory(directoryFileName);
    }

    /**
     * @param webClient
     * @param queries
     * @return
     */
    private static JsonObject webService(IWebClient<JsonObject> webClient, List<QueryCriteria> queries){
        //send the query to the web client
        webClient = new AlphaVantageWebClient(query);
        try {
            alphaVantageWebClient.sendRequest();
        }catch (InterruptedException ex){
            LOG.error(ex);
        }
    }

    private static void responseParser(JsonObject response){

    }

    public static void main(String[] args) {

        if( args.length != 1){
            LOG.error("You must provide only the directory where you have your stock list CSVs");
            System.exit(1);
        }

        //List of all the stock symbols
        Set<StockSymbol> stockSymbols = getStockSymbols(new CsvReader(), args[0]);

        stockSymbols.forEach(
                symbol -> {

                    //build query
                    List<QueryCriteria> query = queryBuilder(symbol);


                    //todo maker response parser
                    //parse the response
                    JsonObject jsonObject = alphaVantageWebClient.getResponse();
                    System.out.println(jsonObject.get("Technical Analysis: SMA"));
                    JsonObject jsonTechAna = jsonObject.get("Technical Analysis: SMA").asObject();
                    System.out.println();

                    //probably a good idea to have an object with date and sma attached then parse to be able to sort the dates.
                    for (String value : jsonTechAna.names()) {
                        String v = jsonTechAna.get(value).asObject().get("SMA").asString();
                        System.out.println(v);
                    }
                }
        );
    }
}
