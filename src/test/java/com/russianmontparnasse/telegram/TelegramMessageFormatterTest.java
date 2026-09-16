package com.russianmontparnasse.telegram;

import com.russianmontparnasse.news.NewsArticle;
import com.russianmontparnasse.news.NewsSummary;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TelegramMessageFormatterTest {
    private final TelegramMessageFormatter formatter = new TelegramMessageFormatter();

    @Test
    void shouldFormatNewsArticle() {
        NewsArticle article = new NewsArticle("Test title", "https://example.com/news", "2026-09-02");
        String result = formatter.format(article);

        assertEquals("Test title\n\nhttps://example.com/news", result);
    }

    @Test
    void shouldFormatRussianNewsSummary(){
        NewsArticle article = new NewsArticle(
                "French title",
                "https://example.com/article",
                "2029-09-15"
        );

        NewsSummary summary = new NewsSummary(
                "Крайний срок подачи декларации — 20 мая 2026 года",
                "Декларацию необходимо подать до 20 мая 2026 года.",
                "Проверьте срок для своего департамента."
        );

        String result = formatter.format(article, summary);

        assertEquals("""
            Крайний срок подачи декларации — 20 мая 2026 года

            Декларацию необходимо подать до 20 мая 2026 года.

            Проверьте срок для своего департамента.

            Источник: https://example.com/article""", result);
    }
}
