package com.ant.juchumjuchum.scraper.openapi.token.domain;

import com.ant.juchumjuchum.config.StockAccount;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class OpenApiAccountInfo {
    private StockAccount stockAccount;
    private OpenApiToken openApiToken;
}
