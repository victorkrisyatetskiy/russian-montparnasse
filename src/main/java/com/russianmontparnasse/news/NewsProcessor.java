package com.russianmontparnasse.news;

import com.russianmontparnasse.rss.RssItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsProcessor {

    private static final Logger logger = LoggerFactory.getLogger(NewsProcessor.class);


    public void printNews(List<RssItem> news) {
        if (news == null || news.isEmpty()) {
            logger.info("No news available.");
            return;
        }

        for (RssItem item : news) {
            logger.info("Title: {}\nPublished on: {}\nLink: {}\n---",
                    item.title() != null ? item.title() : "Untitled",
                    item.publishedDate() != null ? item.publishedDate() : "N/A",
                    item.link() != null ? item.link() : "No link provided"
            );
        }
    }
}