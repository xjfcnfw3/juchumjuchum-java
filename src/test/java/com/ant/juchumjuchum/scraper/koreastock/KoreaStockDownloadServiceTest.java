package com.ant.juchumjuchum.scraper.koreastock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.ant.juchumjuchum.common.http.CustomHttpConnection;
import com.ant.juchumjuchum.config.StockAccountProperties;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KoreaStockDownloadServiceTest {

    @Mock
    private CustomHttpConnection connection;

    @Mock
    private StockAccountProperties stockAccountProperties;

    @InjectMocks
    private KoreaStockDownloadService koreaStockDownloadService;


    @Test
    @DisplayName("주식 정보 다운로드")
    void testDownloadStockInfo() throws Exception {
        String target = "kosdaq_code";
        String mstFileName = target + ".mst.zip";
        Path downloadZipFilePath = Path.of("./", target + ".mst.zip");
        File mockFile = downloadZipFilePath.toFile();
        when(connection.downloadFileStream(anyString())).thenReturn(createMockZipFileStream(mstFileName));

        Optional<File> result = koreaStockDownloadService.downloadStockInfo(target);

        assertTrue(result.isPresent());
        assertEquals(mockFile, result.get());
        result.get().delete();
    }

    private InputStream createMockZipFileStream(String path) throws Exception {
        try (ByteArrayOutputStream byteArrayOutputStream = new java.io.ByteArrayOutputStream();
             ZipOutputStream zipOutputStream = new java.util.zip.ZipOutputStream(byteArrayOutputStream)) {

            zipOutputStream.putNextEntry(new ZipEntry(path));
            zipOutputStream.write("Hello, Mock Zip!".getBytes());
            zipOutputStream.closeEntry();

            return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        }
    }
}
