package com.ant.juchumjuchum.scraper.koreastock;


import static java.util.Collections.emptyList;

import com.ant.juchumjuchum.scraper.koreastock.file.KosdaqParser;
import com.ant.juchumjuchum.scraper.koreastock.file.KospiParser;
import com.ant.juchumjuchum.scraper.koreastock.file.StockFileReader;
import com.ant.juchumjuchum.stock.StockRepository;
import com.ant.juchumjuchum.stock.domain.Stock;
import com.ant.juchumjuchum.utils.FileUtil;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KoreaStockService {
    public static final String KOSPI_CODE_MST = "kospi_code.mst";
    public static final String KOSDAQ_CODE_MST = "kosdaq_code.mst";
    private final KoreaStockDownloadService koreaStockDownloadService;
    private final StockRepository stockRepository;

    public CompletableFuture<Integer> initKosdaq() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<Stock> kosdaq = downloadKosdaq();
                stockRepository.saveAll(kosdaq);
                return 1;
            } catch (IOException e) {
                log.error(e.getMessage());
                return -1;
            }
        });
    }

    public CompletableFuture<Integer> initKospi() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<Stock> kospi = downloadKospi();
                stockRepository.saveAll(kospi);
                return 1;
            } catch (IOException e) {
                log.error(e.getMessage());
                return -1;
            }
        });
    }

    private List<Stock> downloadKosdaq() throws IOException {
        Optional<File> downloadFile = koreaStockDownloadService.downloadStockInfo("kosdaq_code");
        boolean unZipResult = unZipFile(downloadFile);
        if (!unZipResult) {
            return emptyList();
        }
        StockFileReader fileReader = new StockFileReader(new KosdaqParser());
        List<Stock> stocks = fileReader.readStockFile(KOSDAQ_CODE_MST);
        new File(KOSDAQ_CODE_MST).delete();
        return stocks;
    }

    private List<Stock> downloadKospi() throws IOException {
        Optional<File> downloadFile = koreaStockDownloadService.downloadStockInfo("kospi_code");
        boolean unZipResult = unZipFile(downloadFile);
        if (!unZipResult) {
            return emptyList();
        }
        StockFileReader fileReader = new StockFileReader(new KospiParser());
        List<Stock> stocks = fileReader.readStockFile(KOSPI_CODE_MST);
        new File(KOSPI_CODE_MST).delete();
        return stocks;
    }

    private boolean unZipFile(Optional<File> downloadFile) {
        if (downloadFile.isEmpty()) {
            return false;
        }
        File file = downloadFile.get();
        FileUtil.unzipFile(file);
        return file.delete();
    }
}
