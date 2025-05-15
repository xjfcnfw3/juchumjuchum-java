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

    String kosdaq =
            "900110   HK0000057197이스트아시아홀딩스                      FS 000000000000NNN NNNNNNNN0NNNNNNNN0000000470000100001NNN00NNN000000100N0900000025938880000000000002010042300000000064265000000000000090451845512       0 NN0000007410000000810000000830005200000000220241231000000302   NNN\n"
                    + "900270   HK0000214814헝셩그룹                                FS 000000000000NNN NNNNNNNN0NNNNNNNN0000002400000100001NNN00NNN000000100N0900000113589040000000000002016081800000000017628200000000000055610722012       0 NN0000011710000000740000000490002300000000120241231000000423   NNN";

    String kospi =
            "F70100009KR5701000097한투삼성그룹성장테마1호(A)              BC 000000000000NN 0NNN NN    N  0  N     0000010080000100000NNN00NNN000000100N09000000000000000000000100020240809000000000017771000000000017771907000         0 NNN00000000000000000000000000000000000000.00        000000179   NNN\n"
                    + "F70100010KR5701000105한투삼성그룹성장테마1호(A-e)            BC 000000000000NN 0NNN NN    N  0  N     0000010090000100000NNN00NNN000000100N09000000000000000000000100020240809000000000011074000000000011074770000         0 NNN00000000000000000000000000000000000000.00        000000111   NNN\n";

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
        writeFile(mstFile, kosdaq);

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
        writeFile(mstFile, kospi);
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

    private void writeFile(File mstFile, String kospi) {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(mstFile), "ms949"))) {
            writer.write(kospi);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
