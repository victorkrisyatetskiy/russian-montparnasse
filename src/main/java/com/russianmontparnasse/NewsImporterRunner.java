package com.russianmontparnasse;

import com.russianmontparnasse.news.NewsArticle;
import com.russianmontparnasse.news.NewsMapper;
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
    private final NewsMapper newsMapper;

    @Value("${rss.feed.url}")
    private String rssUrl;

    public NewsImporterRunner(RssFeedReader rssFeedReader, NewsProcessor newsProcessor, NewsMapper newsMapper) {
        this.rssFeedReader = rssFeedReader;
        this.newsProcessor = newsProcessor;
        this.newsMapper = newsMapper;
    }

    @Override
    public void run(String... args) {
        logger.info("Starting news import process...");
        try {
            List<RssItem> rssItems = rssFeedReader.read(rssUrl);

            List<NewsArticle> newsArticles = rssItems.stream()
                    .map(newsMapper::mapToNewsArticle)
                    .toList(); // Using toList()

            newsProcessor.printNews(newsArticles);

        } catch (Exception e) {
            logger.error("Error occurred while importing news: ", e);
        }
        logger.info("News import process finished.");
    }
}