package com.jvalenc.stock.csv.reader;

import com.jvalenc.stock.models.StockTicker;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by jonat on 1/13/2018.
 */
public class CsvFileReaderTest {
    @Test
    public void readCsvFile() throws Exception {

        //Arrange
        String fileName = "C:\\Users\\jonat\\IdeaProjects\\stock-filter\\src\\main\\resources\\nyseList.csv";
        CsvReader csvFileReader = new CsvReader();

        //Act
        List<StockTicker> stockTickers = csvFileReader.readCsvFile(fileName);

        //Assert
        Assert.assertTrue(stockTickers.size() > 0);
    }

    @Test
    public void ReadCsvDirectory() throws Exception {
        //Arrange
        String directoryFileName = "C:\\Users\\jonat\\IdeaProjects\\stock-filter\\src\\main\\resources\\stock_lists";
        CsvReader csvReader = new CsvReader();

        //Act
        List<StockTicker> stockTickers = csvReader.readCsvDirectory(directoryFileName);

        //Assert
        Assert.assertTrue(stockTickers.size() > 0);
    }

}