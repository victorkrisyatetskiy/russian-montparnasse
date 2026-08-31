package com.russianmontparnasse.news;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NewsImportScheduler {

    private final NewsImportService newsImportService;

    public NewsImportScheduler(NewsImportService newsImportService) {
        this.newsImportService = newsImportService;
    }

    @Scheduled(fixedDelayString = "${news.import.interval}")
    public void importNews() {
        newsImportService.importNews();
    }
}
