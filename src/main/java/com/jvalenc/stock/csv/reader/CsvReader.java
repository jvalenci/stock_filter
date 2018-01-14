package com.jvalenc.stock.csv.reader;

import com.jvalenc.stock.models.StockTicker;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jonat on 1/13/2018.
 */
public class CsvReader {

    //Logger
    private Logger logger = Logger.getLogger(CsvReader.class);

    //File header mapping is the same as stock ticker only because there is on one column in the csv.
    //scable to add more column headers then you have to add it to the ticker attributes

    //CSV file header
    private final String[] FILE_HEADER_MAPPING = {"Symbol"};
    //Stock ticker attributes
    private final String STOCK_TICKER = "Symbol";

    public List<StockTicker> readCsvDirectory(String directoyfileNmae){
        File file = new File(directoyfileNmae);
        List<StockTicker> stockTickers = new ArrayList<>();
        if(file.isDirectory() && file.listFiles().length > 0){
            Arrays.stream(file.listFiles()).forEach( path->
                    stockTickers.addAll(readCsvFile(path.getAbsolutePath())));
        }
        return stockTickers;
    }

    /**
     * @param filename
     */
    private List<StockTicker> readCsvFile(String filename){

        FileReader fileReader = null;
        CSVParser csvFileParser = null;
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(FILE_HEADER_MAPPING);

        //list of stock ticker to be filled by the csv
        List<StockTicker> stockTickers = new ArrayList();

        try{

            //init the file reader
            fileReader = new FileReader(filename);

            //init the csv file parser
            csvFileParser = new CSVParser(fileReader,csvFileFormat);

            //get a list of csv file records
            List<CSVRecord> csvRecords = csvFileParser.getRecords();

            //read the csv file starting from the second position to skip the header
            for(int i = 1; i < csvRecords.size(); i++){
                CSVRecord record = (CSVRecord)csvRecords.get(i);
                StockTicker stockTicker = new StockTicker( record.get(STOCK_TICKER));
                stockTickers.add(stockTicker);
            }
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
        return stockTickers;
    }
}
