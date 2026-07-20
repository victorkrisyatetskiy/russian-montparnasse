package com.russianmontparnasse.news;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsProcessor {

    private static final Logger logger = LoggerFactory.getLogger(NewsProcessor.class);


    public void printNews(List<NewsArticle> news) {
        if (news == null || news.isEmpty()) {
            logger.info("No news available.");
            return;
        }

        for (NewsArticle article : news) {
            logger.info("Title: {}\nPublished on: {}\nLink: {}\n---",
                    article.title() != null ? article.title() : "Untitled",
                    article.publishedDate() != null ? article.publishedDate() : "N/A",
                    article.link() != null ? article.link() : "No link provided"
            );
        }
    }
}