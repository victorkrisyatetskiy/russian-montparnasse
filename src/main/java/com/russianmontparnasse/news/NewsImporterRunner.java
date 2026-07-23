package com.russianmontparnasse.news;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
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