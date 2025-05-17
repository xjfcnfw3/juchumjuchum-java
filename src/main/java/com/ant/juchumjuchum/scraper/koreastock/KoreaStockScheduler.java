package com.ant.juchumjuchum.scraper.koreastock;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KoreaStockScheduler {

    private final KoreaStockService koreaStockService;

    @Scheduled(cron = "0 0 0 * * MON-FRI")
    @Scheduled(cron = "0 0 9 * * MON-FRI")
    @PostConstruct
    public void downloadFile() {
        koreaStockService.initKospi();
        koreaStockService.initKosdaq();
    }
}
