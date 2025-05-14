package com.ant.juchumjuchum.scraper.koreastock;


import com.ant.juchumjuchum.stock.StockRepository;
import com.ant.juchumjuchum.stock.domain.Stock;
import com.ant.juchumjuchum.utils.FileUtil;
import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KoreaStockService {
    private final KoreaStockDownloadService koreaStockDownloadService;
    private final StockRepository stockRepository;

    @Scheduled(cron = "0 0 0 * * MON-FRI")
    @Scheduled(cron = "0 0 9 * * MON-FRI")
    @PostConstruct
    public void initStockInfo() {
        initKosdaq();
        initKospi();
    }

    private void initKosdaq() {
        CompletableFuture.supplyAsync(() -> {
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

    private void initKospi() {
        CompletableFuture.supplyAsync(() -> {
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

    List<Stock> downloadKosdaq() throws IOException {
        Optional<File> kosdaq = koreaStockDownloadService.downloadStockInfo("kosdaq_code");
        kosdaq.ifPresent(file -> {
            FileUtil.unzipFile(file);
            file.delete();
        });
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                new FileInputStream("kosdaq_code.mst"), "ms949"
        ));
        List<Stock> stocks = getKosdaqStocksFromMst(bufferedReader);
        new File("./kosdaq_code.mst").delete();
        return stocks;
    }

    public List<Stock> downloadKospi() throws IOException {
        Optional<File> kospi = koreaStockDownloadService.downloadStockInfo("kospi_code");
        kospi.ifPresent(file -> {
            FileUtil.unzipFile(file);
            file.delete();
        });
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                new FileInputStream("kospi_code.mst"), "ms949"
        ));
        List<Stock> stocks = getKospiStocksFromMst(bufferedReader);
        new File("./kospi_code.mst").delete();
        return stocks;
    }

    private List<Stock> getKosdaqStocksFromMst(BufferedReader bufferedReader) throws IOException {
        String line;
        List<Stock> stocks = new ArrayList<>();
        while ((line = bufferedReader.readLine()) != null) {
            Stock stock = Stock.builder()
                    .id(line.substring(0, 9).trim())
                    .name(line.substring(21, line.length() - 221).trim())
                    .group(line.substring(line.length() - 221, line.length() - 219).trim())
                    .build();
            stocks.add(stock);
        }
        bufferedReader.close();
        return stocks;
    }


    private List<Stock> getKospiStocksFromMst(BufferedReader bufferedReader) throws IOException {
        String line;
        List<Stock> stocks = new ArrayList<>();
        while ((line = bufferedReader.readLine()) != null) {
            Stock stock = Stock.builder()
                    .id(line.substring(0, 9).trim())
                    .name(line.substring(21, line.length() - 227).trim())
                    .group(line.substring(line.length() - 227, line.length() - 225).trim())
                    .build();
            stocks.add(stock);
        }
        bufferedReader.close();
        return stocks;
    }
}
