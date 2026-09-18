package com.russianmontparnasse.news;

public record ProcessedNewsArticle(
        NewsArticle article,
        NewsSummary summary,
        NewsCategory category
) {
}
