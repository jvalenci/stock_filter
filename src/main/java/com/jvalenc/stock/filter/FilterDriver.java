package com.jvalenc.stock.filter;

import com.jvalenc.stock.csv.reader.CsvReader;
import com.jvalenc.stock.csv.reader.ICsvReader;
import com.jvalenc.stock.email.client.EmailClient;
import com.jvalenc.stock.models.StockSymbol;
import com.jvalenc.stock.web.rest.AlphaVantageWebClient;
import org.apache.log4j.Logger;

import javax.mail.MessagingException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class FilterDriver {

    private static final Logger logger = Logger.getLogger(FilterDriver.class);

    /**
     * arg[0]: absolute file path to directory with all the stock lists.
     * @param args file location of stock list csv
     */
    public static void main(String[] args) {

        if( args.length != 1){
            logger.error("You must provide only the directory where you have your stock list CSVs");
            System.exit(1);
        }

        //List of all the stock symbols
        List<StockSymbol> stockSymbols = Objects.requireNonNull(getStockSymbols(new CsvReader(), args[0]), "stock list cannot be null");
        Set<StockSymbol> stocksToEmail = new HashSet<>();
        Indicator indicator;

        for(int i = 0; i < stockSymbols.size(); i++){
            StockSymbol symbol = stockSymbols.get(i);
            try {

                //todo: add future indictors here. BEWARE every indicator that you add it double the time to complete

                //SMA
                indicator = new SimpleMovingAverageCrossover(AlphaVantageWebClient.getInstance());
                indicator.decorateStockSymbol(symbol);

                //WILLR
                indicator = new WilliamsPercentR(AlphaVantageWebClient.getInstance());
                indicator.decorateStockSymbol(symbol);

                //manditory to wait 1 minute between 5 calls to the api
                Thread.sleep(61000);
                if( symbol.hasIndicator()) {
                    stocksToEmail.add(symbol);
                }

                //loading sign
                printLoading(i, stockSymbols.size());

            } catch (Exception e) {
                logger.error("Api Might have thrown an error for call frequency. break and try to send stocks to email if there are any: " + e.getMessage());
                break;
            }
        }
        try {
            if(stocksToEmail.size() > 0) {
                EmailClient.generateAndSendEmail(stocksToEmail);
            }else {
                logger.info("There were no stocks with an intersection found.");
            }
        }catch (MessagingException ex){
            logger.error("The email was unsuccessful", ex);
        }
    }

    private static void printLoading(int i, int size) {
        //This is just a loading symbol
        StringBuilder sb = new StringBuilder();
        sb.append("LOADING:");
        sb.append(" ");
        float percentage = ((i * 100.0f)/size);
        sb.append(String.format("%.02f", percentage));
        sb.append("% Completed");
        logger.info(sb.toString());
    }

    /**
     * @param csvReader
     * @param directoryFileName
     * @return
     */
    protected static List<StockSymbol> getStockSymbols(ICsvReader<StockSymbol> csvReader, String directoryFileName){
        return csvReader.readCsvDirectory(directoryFileName);
    }

}
