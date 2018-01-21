package com.jvalenc.stock.csv.reader;

import java.util.List;
import java.util.Set;

/**
 * Created by jonat on 1/13/2018.
 */
public interface ICsvReader<T> {
    List<T> readCsvDirectory(String directory);
}
