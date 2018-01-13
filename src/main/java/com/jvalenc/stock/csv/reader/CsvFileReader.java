package com.jvalenc.stock.csv.reader;

import com.jvalenc.stock.models.StockTicker;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jonat on 1/13/2018.
 */
public class CsvFileReader {

    //Logger
    private static Logger logger = Logger.getLogger(CsvFileReader.class);
    //CSV file header
    private static final String[] FILE_HEADER_MAPPING = {"Symbol"};
    //Stock ticker attributes
    private static final String STOCK_TICKER = "ticker";

    /**
     * @param filename
     */
    public static void readCsvFile(String filename){

        FileReader fileReader = null;
        CSVParser csvFileParser = null;
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(FILE_HEADER_MAPPING);

        try{
            //list of stock ticker to be filled by the csv
            List stockTickers = new ArrayList();

            //init the file reader
            fileReader = new FileReader(filename);

            //init the csv file parser
            csvFileParser = new CSVParser(fileReader,csvFileFormat);

            //get a list of csv file records
            List csvRecords = csvFileParser.getRecords();

            //read the csv file starting from the second position to skip the header
            for(int i = 1; i < csvRecords.size(); i++){
                CSVRecord record = (CSVRecord)csvRecords.get(i);
                StockTicker stockTicker = new StockTicker( record.get(STOCK_TICKER));
                stockTickers.add(stockTicker);
            }

            stockTickers.forEach( System.out::println);

        }catch (IOException ex){
            logger.info("File was not found", ex);
        }finally {
            try {
                fileReader.close();
                csvFileParser.close();
            }catch (IOException ex){
                logger.error("Could finish clean up", ex);
            }
        }
    }
}
