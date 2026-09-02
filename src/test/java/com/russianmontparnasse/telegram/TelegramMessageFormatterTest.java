package com.russianmontparnasse.telegram;

import com.russianmontparnasse.news.NewsArticle;
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
}
