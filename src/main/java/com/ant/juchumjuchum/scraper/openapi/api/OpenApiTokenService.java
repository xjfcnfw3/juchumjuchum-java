package com.ant.juchumjuchum.scraper.openapi.api;

import com.ant.juchumjuchum.config.StockAccountProperties;
import org.springframework.stereotype.Service;

@Service
public class OpenApiTokenService {
    private final StockAccountProperties stockAccountProperties;


    public OpenApiTokenService(StockAccountProperties stockAccountProperties) {
        this.stockAccountProperties = stockAccountProperties;

        if (stockAccountProperties.getAccounts().isEmpty()) {
            throw new IllegalArgumentException("The number of accounts must be at least one.");
        }

        if (stockAccountProperties.getAccounts().size() != stockAccountProperties.getPasswords().size() ||
                stockAccountProperties.getAccounts().size() != stockAccountProperties.getKeys().size()) {
            throw new IllegalArgumentException("The number of accounts, passwords, and keys must be the same.");
        }
    }
}
