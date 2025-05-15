package com.ant.juchumjuchum.scraper.koreastock.file;

import com.ant.juchumjuchum.stock.domain.Stock;

@FunctionalInterface
public interface Parser {
    Stock parse(String line);
}
