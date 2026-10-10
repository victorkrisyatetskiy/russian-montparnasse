package com.russianmontparnasse.news;

import com.russianmontparnasse.persistence.NewsPersistenceService;
import com.russianmontparnasse.telegram.TelegramMessageFormatter;
import com.russianmontparnasse.telegram.TelegramService;
import org.springframework.stereotype.Service;


@Service
public class NewsPublicationService {
    private final NewsPersistenceService newsPersistenceService;
    private final TelegramMessageFormatter telegramMessageFormatter;
    private final TelegramService telegramService;


    public NewsPublicationService(NewsPersistenceService newsPersistenceService, TelegramMessageFormatter telegramMessageFormatter, TelegramService telegramService) {
        this.newsPersistenceService = newsPersistenceService;
        this.telegramMessageFormatter = telegramMessageFormatter;
        this.telegramService = telegramService;
    }

    public void publish(ProcessedNewsArticle article){
        String message = telegramMessageFormatter.format(
                article.article(),
                article.summary(),
                article.category()
        );

        telegramService.sendMessage(message);

        newsPersistenceService.markAsPublished(article.article().link());
    }
}
