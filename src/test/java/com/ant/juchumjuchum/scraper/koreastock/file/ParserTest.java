package com.ant.juchumjuchum.scraper.koreastock.file;

import static org.assertj.core.api.Assertions.assertThat;

import com.ant.juchumjuchum.stock.domain.Stock;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ParserTest {

    static Stream<Arguments> kospiLineAndStock() throws IOException {
        List<String> inputs = Files.readAllLines(Paths.get("src/test/resources/test-kospi-inputs.txt"));
        List<String> outputs = Files.readAllLines(Paths.get("src/test/resources/test-kospi-outputs.txt"));

        return IntStream.range(0, inputs.size())
                .mapToObj(i -> {
                    String input = inputs.get(i);
                    String json = outputs.get(i);
                    Stock expected = parseStockFromJson(json);
                    return Arguments.of(input, expected);
                });
    }

    private static Stock parseStockFromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, Stock.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @ParameterizedTest
    @DisplayName("코스피 파일")
    @MethodSource("kospiLineAndStock")
    void parseKospi(String line, Stock stock) {
        Parser parser = new KospiParser();

        Stock result = parser.parse(line);

        assertThat(result.getId()).isEqualTo(stock.getId());
        assertThat(result.getGroup()).isEqualTo(stock.getGroup());
        assertThat(result.getName()).isEqualTo(stock.getName());
    }
}
