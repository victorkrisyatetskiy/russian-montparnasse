package com.russianmontparnasse.news;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
        name = "news.import.runner.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class NewsImporterRunner implements CommandLineRunner {

    private final NewsImportService newsImportService;

    public NewsImporterRunner(NewsImportService newsImportService) {
        this.newsImportService = newsImportService;
    }

    @Override
    public void run(String... args) {
        newsImportService.importNews();
    }
}