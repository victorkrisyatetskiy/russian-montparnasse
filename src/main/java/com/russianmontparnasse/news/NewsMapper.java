package com.russianmontparnasse.news;

import com.russianmontparnasse.persistence.NewsArticleEntity;
import com.russianmontparnasse.rss.RssItem;
import org.springframework.stereotype.Component;

@Component
public class NewsMapper {

    // Existing method
    public NewsArticle mapToNewsArticle(RssItem rssItem) {
        if (rssItem == null) {
            throw new IllegalArgumentException("RssItem cannot be null");
        }

        return new NewsArticle(
                rssItem.title(),
                rssItem.link(),
                rssItem.publishedDate()
        );
    }

    // New method for mapping NewsArticleEntity to NewsArticle
    public NewsArticle mapToNewsArticle(NewsArticleEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("NewsArticleEntity cannot be null");
        }

        return new NewsArticle(
                entity.getTitle(),
                entity.getLink(),
                entity.getPublishedDate()
        );
    }
}