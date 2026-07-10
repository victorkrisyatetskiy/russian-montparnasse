package com.russianmontparnasse;

import com.russianmontparnasse.rss.RssFeedReader;
import com.russianmontparnasse.rss.RssItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NewsImporterRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(NewsImporterRunner.class);

    private final RssFeedReader rssFeedReader;

    @Value("${rss.feed.url}")
    private String rssUrl;

    public NewsImporterRunner(RssFeedReader rssFeedReader) {
        this.rssFeedReader = rssFeedReader;
    }

    @Override
    public void run(String... args) {
        try {
            logger.info("Starting to read RSS feed from URL: {}", rssUrl);
            List<RssItem> rssItems = rssFeedReader.read(rssUrl);

            if (rssItems.isEmpty()) {
                logger.warn("No items were found in the RSS feed.");
            } else {
                for (RssItem item : rssItems) {
                    logger.info("Title: {}", item.title());
                    logger.info("Link: {}", item.link());
                    logger.info("Published Date: {}", item.publishedDate());
                    logger.info("---------------");
                }
            }
        } catch (Exception e) {
            logger.error("An error occurred while reading the RSS feed: {}", e.getMessage(), e);
        }
    }
}