package com.ant.juchumjuchum.scraper.koreastock.file;

import com.ant.juchumjuchum.stock.domain.Stock;

public class KosdaqParser implements Parser {

    @Override
    public Stock parse(String line) {
        return Stock.builder()
                .id(line.substring(0, 9).trim())
                .name(line.substring(21, line.length() - 221).trim())
                .group(line.substring(line.length() - 221, line.length() - 219).trim())
                .build();
    }
}
