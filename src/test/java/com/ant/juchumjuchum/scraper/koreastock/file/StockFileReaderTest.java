package com.ant.juchumjuchum.scraper.koreastock.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ant.juchumjuchum.stock.domain.Stock;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StockFileReaderTest {

    @Test
    @DisplayName("주식 파일로 주식 클래스로 읽기")
    void readStockFile() throws IOException {
        File tempFile = File.createTempFile("test-stock", ".mst");
        String firstLine = "900110   HK0000057197이스트아시아홀딩스                      FS 00";
        String secondLine = "000250   KR7000250001삼천당제약                              ST1";
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(tempFile), Charset.forName("ms949"))) {
            writer.write(firstLine + "\n");
            writer.write(secondLine);
        }
        Parser mockParser = mock(Parser.class);
        when(mockParser.parse(firstLine))
                .thenReturn(Stock.builder().name("이스트아시아홀딩스").id("900110").group("FS").build());
        when(mockParser.parse(secondLine))
                .thenReturn(Stock.builder().name("삼천당제약").id("000250").group("ST").build());

        StockFileReader reader = new StockFileReader(mockParser);

        List<Stock> stocks = reader.readStockFile(tempFile.getAbsolutePath());

        assertThat(stocks.size()).isEqualTo(2);

        tempFile.delete();
    }

}
