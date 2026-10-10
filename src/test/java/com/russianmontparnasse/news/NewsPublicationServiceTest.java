package com.russianmontparnasse.news;

import com.russianmontparnasse.persistence.NewsPersistenceService;
import com.russianmontparnasse.telegram.TelegramMessageFormatter;
import com.russianmontparnasse.telegram.TelegramService;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class NewsPublicationServiceTest {

    private final NewsPersistenceService newsPersistenceService = mock(NewsPersistenceService.class);

    private final TelegramMessageFormatter telegramMessageFormatter = mock(TelegramMessageFormatter.class);

    private final TelegramService telegramService = mock(TelegramService.class);

    private final NewsPublicationService newsPublicationService = new NewsPublicationService(
            newsPersistenceService,
            telegramMessageFormatter,
            telegramService
    );

    @Test
    void shouldMarkArticlePublishedAfterSuccessfulTelegramSend() {
        NewsArticle article = new NewsArticle(
                "French news",
                "https://example.com/article",
                "2026-10-10"
        );

        NewsSummary summary = new NewsSummary(
                "Новость на русском",
                "Подробное описание новости",
                "Главная информация"
        );

        ProcessedNewsArticle processedNewsArticle = new ProcessedNewsArticle(
                article,
                summary,
                NewsCategory.ADMINISTRATION
        );

        String message = "Formatted Telegram message";

        when(telegramMessageFormatter.format(
                article,
                summary,
                NewsCategory.ADMINISTRATION
        )).thenReturn(message);

        newsPublicationService.publish(processedNewsArticle);

        InOrder inOrder = Mockito.inOrder(
                telegramService,
                newsPersistenceService
        );

        inOrder.verify(telegramService).sendMessage(message);
        inOrder.verify(newsPersistenceService).markAsPublished(article.link());
    }

    @Test
    void shouldMarkArticlePublishedAfterUnSuccessfulTelegramSend() {
        NewsArticle article = new NewsArticle(
                "French news",
                "https://example.com/article",
                "2026-10-10"
        );

        NewsSummary summary = new NewsSummary(
                "Новость на русском",
                "Подробное описание новости",
                "Главная информация"
        );

        ProcessedNewsArticle processedNewsArticle = new ProcessedNewsArticle(
                article,
                summary,
                NewsCategory.ADMINISTRATION
        );

        String message = "Formatted Telegram message";

        when(telegramMessageFormatter.format(
                article,
                summary,
                NewsCategory.ADMINISTRATION
        )).thenReturn(message);

        doThrow(new RuntimeException("Telegram unavailable"))
                .when(telegramService)
                .sendMessage(message);

        assertThrows(
                RuntimeException.class,
                () -> newsPublicationService.publish(processedNewsArticle)
        );

        verify(newsPersistenceService, never())
                .markAsPublished(anyString());

    }

}
