package com.russianmontparnasse.news;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NewsPublicationScheduler {
    private final NewsPublicationService newsPublicationService;
    private final boolean telegramPublishEnabled;

    public NewsPublicationScheduler(
            NewsPublicationService newsPublicationService,
            @Value("${telegram.publish.enabled:false}")
            boolean telegramPublishEnabled
    ) {
        this.newsPublicationService = newsPublicationService;
        this.telegramPublishEnabled = telegramPublishEnabled;
    }

    @Scheduled(
            fixedDelayString = "${news.publish.interval:3600000}",
            initialDelayString = "${news.publish.initial-delay:3600000}"
    )

    public void publishScheduledNews() {
        if (!telegramPublishEnabled) {
            return;
        }

        newsPublicationService.publishNext();
    }
}
