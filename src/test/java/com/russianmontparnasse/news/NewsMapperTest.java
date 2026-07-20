package com.russianmontparnasse.news;

import com.russianmontparnasse.rss.RssItem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NewsMapperTest {

    private final NewsMapper newsMapper = new NewsMapper();

    @Test
    void shouldMapAllFieldsCorrectly() {
        // Given
        String title = "Some News Title";
        String link = "https://news.example.com/article";
        String publishedDate = "2023-11-01T12:00:00Z";

        RssItem rssItem = new RssItem(title, link, publishedDate);

        // When
        NewsArticle newsArticle = newsMapper.mapToNewsArticle(rssItem);

        // Then
        assertNotNull(newsArticle);
        assertEquals(title, newsArticle.title());
        assertEquals(link, newsArticle.link());
        assertEquals(publishedDate, newsArticle.publishedDate());
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenRssItemIsNull() {
        // When
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> newsMapper.mapToNewsArticle(null)
        );

        // Then
        assertEquals("RssItem cannot be null", exception.getMessage());
    }
}