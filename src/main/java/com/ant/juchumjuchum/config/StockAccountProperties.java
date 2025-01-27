package com.ant.juchumjuchum.config;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "stock")
public class StockAccountProperties {
    private List<StockAccount> accounts;
    private String openApiUrl;
    private String mstUrl;

    public boolean isEmpty() {
        return accounts.isEmpty();
    }
}
