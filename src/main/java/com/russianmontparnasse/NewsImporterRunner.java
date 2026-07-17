package com.russianmontparnasse;

import com.russianmontparnasse.news.NewsProcessor;
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
    private final NewsProcessor newsProcessor;

    @Value("${rss.feed.url}")
    private String rssUrl;

    public NewsImporterRunner(RssFeedReader rssFeedReader, NewsProcessor newsProcessor) {
        this.rssFeedReader = rssFeedReader;
        this.newsProcessor = newsProcessor;
    }

    @Override
    public void run(String... args) {
        logger.info("Starting news import process...");
        try {
            // Read RSS feed
            List<RssItem> news = rssFeedReader.read(rssUrl);

            // Delegate printing of news to NewsProcessor
            newsProcessor.printNews(news);

        } catch (Exception e) {
            logger.error("Error occurred while importing news: ", e);
        }
        logger.info("News import process finished.");
    }
}