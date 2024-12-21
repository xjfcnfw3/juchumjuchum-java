package com.ant.juchumjuchum.scraper.openapi.token;

import com.ant.juchumjuchum.config.StockAccountProperties;
import com.ant.juchumjuchum.scraper.openapi.token.domain.OpenApiAccountInfo;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Getter
@Service
public class OpenApiTokenService {
    private List<OpenApiAccountInfo> openApiAccountInfos;

    public OpenApiTokenService(StockAccountProperties stockAccountProperties) {
        if (stockAccountProperties.getAccounts().isEmpty()) {
            throw new IllegalArgumentException("The number of accounts must be at least one.");
        }

        if (stockAccountProperties.getAccounts().size() != stockAccountProperties.getPasswords().size() ||
                stockAccountProperties.getAccounts().size() != stockAccountProperties.getKeys().size()) {
            throw new IllegalArgumentException("The number of accounts, passwords, and keys must be the same.");
        }
        init(stockAccountProperties);
    }

    private void init(StockAccountProperties stockAccountProperties) {
        openApiAccountInfos = new ArrayList<>();
        for (int i = 0; i < stockAccountProperties.getAccounts().size(); i++) {
            OpenApiAccountInfo openApiAccountInfo = OpenApiAccountInfo.builder()
                    .account(stockAccountProperties.getAccounts().get(i))
                    .password(stockAccountProperties.getPasswords().get(i))
                    .key(stockAccountProperties.getKeys().get(i))
                    .build();
            openApiAccountInfos.add(openApiAccountInfo);
        }
    }
}
