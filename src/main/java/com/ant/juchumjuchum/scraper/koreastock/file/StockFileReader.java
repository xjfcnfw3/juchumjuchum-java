package com.ant.juchumjuchum.scraper.koreastock.file;

import com.ant.juchumjuchum.stock.domain.Stock;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StockFileReader {

    private final Parser parser;

    public List<Stock> readStockFile(String path) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                new FileInputStream(path), "ms949"
        ));
        List<Stock> stocks = parseFile(bufferedReader);
        bufferedReader.close();
        return stocks;
    }

    private List<Stock> parseFile(BufferedReader bufferedReader) throws IOException {
        String line;
        List<Stock> stocks = new ArrayList<>();
        while ((line = bufferedReader.readLine()) != null) {
            Stock stock = parser.parse(line);
            stocks.add(stock);
        }
        return stocks;
    }
}
