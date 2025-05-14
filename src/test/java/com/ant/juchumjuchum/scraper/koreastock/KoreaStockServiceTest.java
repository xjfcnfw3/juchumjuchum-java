package com.ant.juchumjuchum.scraper.koreastock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

import com.ant.juchumjuchum.stock.StockRepository;
import com.ant.juchumjuchum.stock.domain.Stock;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Optional;
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
    void downloadKosdaq() throws IOException {

        File mstFile = new File("kosdaq_code.mst");
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(mstFile), "ms949"))) {
            writer.write(kosdaq);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        File fakeZipFile = new File("kosdaq_code.zip");
        fakeZipFile.createNewFile();

        when(koreaStockDownloadService.downloadStockInfo("kosdaq_code"))
                .thenReturn(Optional.of(fakeZipFile));

        List<Stock> result = koreaStockService.downloadKosdaq();

        assertEquals(2, result.size());
        Stock stock = result.get(0);
        Stock stock1 = result.get(1);

        assertEquals("900110", stock.getId());
        assertEquals("이스트아시아홀딩스", stock.getName());
        assertEquals("FS", stock.getGroup());

        assertEquals("900270", stock1.getId());
        assertEquals("헝셩그룹", stock1.getName());
        assertEquals("FS", stock1.getGroup());

        assertFalse(new File("kosdaq_code.mst").exists());
    }


    @DisplayName("코스피 코드 다운로드")
    @Test
    void downloadKospi() throws IOException {

        File mstFile = new File("kospi_code.mst");
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(mstFile), "ms949"))) {
            writer.write(kospi);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        File fakeZipFile = new File("kospi_code.zip");
        fakeZipFile.createNewFile();

        when(koreaStockDownloadService.downloadStockInfo("kospi_code"))
                .thenReturn(Optional.of(fakeZipFile));

        List<Stock> result = koreaStockService.downloadKospi();

        assertEquals(2, result.size());
        Stock stock = result.get(0);
        Stock stock1 = result.get(1);

        assertEquals("F70100009", stock.getId());
        assertEquals("한투삼성그룹성장테마1호(A)", stock.getName());
        assertEquals("BC", stock.getGroup());

        assertEquals("F70100010", stock1.getId());
        assertEquals("한투삼성그룹성장테마1호(A-e)", stock1.getName());
        assertEquals("BC", stock1.getGroup());

        assertFalse(new File("kosdpi_code.mst").exists());
    }
}
