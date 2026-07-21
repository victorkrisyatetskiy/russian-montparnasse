package com.russianmontparnasse.news;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NewsDuplicateFilterTest {

    private NewsDuplicateFilter newsDuplicateFilter;

    @BeforeEach
    void setUp() {
        newsDuplicateFilter = new NewsDuplicateFilter();
    }

    @Test
    void testRemoveDuplicateNonNullLinks() {
        List<NewsArticle> articles = new ArrayList<>();
        articles.add(new NewsArticle("Title 1", "https://example.com/1", "2023-01-01"));
        articles.add(new NewsArticle("Title 2", "https://example.com/2", "2023-01-02"));
        articles.add(new NewsArticle("Title 3", "https://example.com/1", "2023-01-03")); // Duplicate link
        articles.add(new NewsArticle("Title 4", "https://example.com/3", "2023-01-04"));

        List<NewsArticle> result = newsDuplicateFilter.removeDuplicates(articles);

        assertEquals(3, result.size());
        assertTrue(result.contains(articles.get(0))); // First occurrence of the link preserved
        assertTrue(result.contains(articles.get(1)));
        assertTrue(result.contains(articles.get(3))); // Last non-duplicate kept
    }

    @Test
    void testOriginalOrderPreserved() {
        List<NewsArticle> articles = new ArrayList<>();
        articles.add(new NewsArticle("Title 1", "https://example.com/1", "2023-01-01"));
        articles.add(new NewsArticle("Title 2", "https://example.com/2", "2023-01-02"));
        articles.add(new NewsArticle("Title 3", "https://example.com/1", "2023-01-03")); // Duplicate link
        articles.add(new NewsArticle("Title 4", "https://example.com/3", "2023-01-04"));

        List<NewsArticle> result = newsDuplicateFilter.removeDuplicates(articles);

        assertEquals(3, result.size());
        assertEquals(articles.get(0), result.get(0)); // Original order preserved
        assertEquals(articles.get(1), result.get(1));
        assertEquals(articles.get(3), result.get(2));
    }

    @Test
    void testAllArticlesWithNullLinksPreserved() {
        List<NewsArticle> articles = new ArrayList<>();
        articles.add(new NewsArticle("Title 1", null, "2023-01-01"));
        articles.add(new NewsArticle("Title 2", "https://example.com/1", "2023-01-02"));
        articles.add(new NewsArticle("Title 3", null, "2023-01-03")); // Null link preserved
        articles.add(new NewsArticle("Title 4", "https://example.com/1", "2023-01-04")); // Duplicate link

        List<NewsArticle> result = newsDuplicateFilter.removeDuplicates(articles);

        assertEquals(3, result.size());
        assertTrue(result.contains(articles.get(0))); // Article with null link preserved
        assertTrue(result.contains(articles.get(1)));
        assertTrue(result.contains(articles.get(2))); // Article with null link preserved
    }

    @Test
    void testEmptyList() {
        List<NewsArticle> articles = new ArrayList<>();

        List<NewsArticle> result = newsDuplicateFilter.removeDuplicates(articles);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testNullInputThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            newsDuplicateFilter.removeDuplicates(null);
        });

        assertEquals("Articles list cannot be null", exception.getMessage());
    }

    @Test
    void testOriginalInputListNotModified() {
        List<NewsArticle> articles = new ArrayList<>();
        articles.add(new NewsArticle("Title 1", "https://example.com/1", "2023-01-01"));
        articles.add(new NewsArticle("Title 2", "https://example.com/2", "2023-01-02"));
        articles.add(new NewsArticle("Title 3", "https://example.com/1", "2023-01-03"));

        List<NewsArticle> articlesCopy = new ArrayList<>(articles);

        newsDuplicateFilter.removeDuplicates(articles);

        assertEquals(articlesCopy, articles); // Verify original list is not modified
    }
}