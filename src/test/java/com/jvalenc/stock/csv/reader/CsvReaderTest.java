package com.jvalenc.stock.csv.reader;

import com.jvalenc.stock.models.StockSymbol;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Set;

/**
 * Created by jonat on 1/13/2018.
 */
public class CsvReaderTest {

    @Test
    public void ReadCsvDirectory() throws Exception {
        //Arrange
        String directoryFileName = "C:\\Users\\jonat\\IdeaProjects\\stock-filter\\src\\main\\resources\\stock_lists";
        CsvReader csvReader = new CsvReader();

        //Act
        List<StockSymbol> stockTickers = csvReader.readCsvDirectory(directoryFileName);

        //Assert
        Assert.assertTrue(stockTickers.size() > 0);
    }

}