package com.ant.juchumjuchum.scraper.koreastock;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ant.juchumjuchum.stock.StockRepository;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KoreaStockServiceTest {

    @InjectMocks
    KoreaStockService koreaStockService;

    @Mock
    KoreaStockDownloadService koreaStockDownloadService;

    @Mock
    StockRepository stockRepository;

    @DisplayName("코스닥 코드 다운로드")
    @Test
    void downloadKosdaq() throws IOException, ExecutionException, InterruptedException {

        File mstFile = new File("kosdaq_code.mst");
        writeFile(mstFile, "test-kosdaq-inputs.txt");

        File fakeZipFile = new File("kosdaq_code.mst.zip");
        fakeZipFile.createNewFile();

        when(koreaStockDownloadService.downloadStockInfo("kosdaq_code"))
                .thenReturn(Optional.of(fakeZipFile));

        int result = koreaStockService.initKosdaq().get();

        verify(stockRepository, times(1)).saveAll(any());
        assertThat(new File("kosdaq_code.mst").exists()).isFalse();
        assertThat(result).isEqualTo(1);
    }


    @DisplayName("코스피 코드 다운로드")
    @Test
    void downloadKospi() throws IOException, ExecutionException, InterruptedException {

        File mstFile = new File("kospi_code.mst");
        writeFile(mstFile, "test-kospi-inputs.txt");
        File fakeZipFile = new File("kospi_code.mst.zip");
        fakeZipFile.createNewFile();
        when(koreaStockDownloadService.downloadStockInfo("kospi_code"))
                .thenReturn(Optional.of(fakeZipFile));

        int result = koreaStockService.initKospi().get();

        verify(stockRepository, times(1)).saveAll(any());
        assertThat(new File("kosdpi_code.mst").exists()).isFalse();
        assertThat(result).isEqualTo(1);
    }


    @DisplayName("빈파일 다운로드")
    @Test
    void downloadBlank() throws ExecutionException, InterruptedException {
        when(koreaStockDownloadService.downloadStockInfo("kospi_code"))
                .thenReturn(Optional.empty());

        int result = koreaStockService.initKospi().get();

        verify(stockRepository, times(1)).saveAll(emptyList());
        assertThat(result).isEqualTo(1);
    }

    private void writeFile(File mstFile, String filename) throws IOException {
        List<String> inputs = Files.readAllLines(Paths.get("src/test/resources/" + filename));
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(mstFile), "ms949"))) {
            for (String line : inputs) {
                writer.write(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
