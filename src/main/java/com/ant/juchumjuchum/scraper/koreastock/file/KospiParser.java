package com.ant.juchumjuchum.scraper.koreastock.file;

import com.ant.juchumjuchum.stock.domain.Stock;

public class KospiParser implements Parser {

    @Override
    public Stock parse(String line) {
        return Stock.builder()
                .id(line.substring(0, 9).trim())
                .name(line.substring(21, line.length() - 227).trim())
                .group(line.substring(line.length() - 227, line.length() - 225).trim())
                .build();
    }

}
