package com.ant.juchumjuchum.scraper.koreastock;


import com.ant.juchumjuchum.stock.StockRepository;
import com.ant.juchumjuchum.stock.domain.Stock;
import jakarta.annotation.PostConstruct;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KoreaStockService {
    private final KoreaStockDownloadService koreaStockDownloadService;
    private final StockRepository stockRepository;

    @Scheduled(cron = "0 0 0 * * MON-FRI")
    @Scheduled(cron = "0 0 9 * * MON-FRI")
    @PostConstruct
    public void initStockInfo() {
        try {
            List<Stock> kosdaq = downloadKosdaq();
            stockRepository.saveAll(kosdaq);
            List<Stock> kospi = downloadKospi();
            stockRepository.saveAll(kospi);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Stock> downloadKosdaq() throws IOException {
        Optional<File> kosdaq = koreaStockDownloadService.downloadStockInfo("kosdaq_code");
        kosdaq.ifPresent(file -> {
            unzipFile(file);
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
            unzipFile(file);
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
                    .group(line.substring(line.length() - 221, line.length() - 220).trim())
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

    private void unzipFile(File file) {
        try {
            ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file));
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                int length;
                BufferedOutputStream out = new BufferedOutputStream(
                        new FileOutputStream("./" + zipEntry.getName()));
                while ((length = zipInputStream.read()) != -1) {
                    out.write(length);
                }
                zipInputStream.closeEntry();
                out.close();
            }
            zipInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
