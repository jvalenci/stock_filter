package com.jvalenc.stock.filter;

import com.eclipsesource.json.JsonObject;
import com.jvalenc.stock.csv.reader.CsvReader;
import com.jvalenc.stock.csv.reader.ICsvReader;
import com.jvalenc.stock.models.QueryCriteria;
import com.jvalenc.stock.models.SMADataPoint;
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

        query = new QueryCriteria();
        query.setSymbol(stockSymbol.getSymbol());
        query.setQueryFunction(QueryFunction.SMA);
        query.setInterval(Interval.DAILY);
        query.setTimePeriod(TimePeriod.TWENTY_THREE);
        query.setSeriesType(SeriesType.CLOSE);
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
     * @return
     */
    private static List<JsonObject> webService(IWebClient<JsonObject> webClient){
        //send the query to the web client
        try {
            webClient.sendRequest();
        }catch (Exception ex){
            LOG.error(ex);
        }
        return webClient.getResponses();
    }

    private static void parseResponse(List<JsonObject> response){
        LOG.info("parsing the Response");
        List< List<SMADataPoint> > dataPoints = new ArrayList<>();
        //parse the response
        response.forEach(
                jsonObject -> {
                    List<SMADataPoint> data = new ArrayList<SMADataPoint>();
                    LOG.info(jsonObject.get("Technical Analysis: SMA"));
                    JsonObject jsonTechAna = jsonObject.get("Technical Analysis: SMA").asObject();

                    for(int i = 0; i < 5; i++){
                        SMADataPoint point = new SMADataPoint();
                        String timeStamp = jsonTechAna.names().get(i);
                        String sma = jsonTechAna.get(timeStamp).asObject().get("SMA").asString();
                        point.setTimeStamp(timeStamp);
                        point.setSimpleMovingAverage(Double.parseDouble(sma));
                        LOG.info(timeStamp + " : " + sma);
                        data.add(point);
                    }
                    dataPoints.add(data);
                }
        );
        analysis(dataPoints);

    }

    private static void analysis(List< List<SMADataPoint> > data){

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
                    List<QueryCriteria> queries = queryBuilder(symbol);

                    //make the call and get a response
                    List<JsonObject> response = webService(new AlphaVantageWebClient(queries));

                    //parse the response to find where the cross over occurred
                    parseResponse(response);
                }
        );
    }
}
