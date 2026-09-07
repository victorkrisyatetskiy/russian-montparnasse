package com.russianmontparnasse.news;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NewsRelevanceServiceTest {
    private final NewsRelevanceService relevanceService = new NewsRelevanceService();

    @Test
    void shouldConsiderArticleRelevant(){
        String articleText = "Some article content";

        boolean result = relevanceService.isRelevant(articleText);

        assertTrue(result);
    }
}
