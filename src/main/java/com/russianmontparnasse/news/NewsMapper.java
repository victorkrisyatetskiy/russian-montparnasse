package com.russianmontparnasse.news;

import com.russianmontparnasse.rss.RssItem;
import org.springframework.stereotype.Component;

@Component
public class NewsMapper {


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
}