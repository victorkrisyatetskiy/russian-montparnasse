package com.russianmontparnasse.news;

import com.russianmontparnasse.persistence.NewsPersistenceService;
import com.russianmontparnasse.rss.RssFeedReader;
import com.russianmontparnasse.rss.RssItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsImportService {

    private static final Logger logger = LoggerFactory.getLogger(NewsImportService.class);

    private final RssFeedReader rssFeedReader;
    private final NewsMapper newsMapper;
    private final NewsDuplicateFilter newsDuplicateFilter;
    private final NewsPersistenceService newsPersistenceService;
    private final NewsProcessor newsProcessor;

    @Value("${rss.feed.url}")
    private String rssUrl;

    public NewsImportService(
            RssFeedReader rssFeedReader,
            NewsMapper newsMapper,
            NewsDuplicateFilter newsDuplicateFilter,
            NewsPersistenceService newsPersistenceService,
            NewsProcessor newsProcessor) {
        this.rssFeedReader = rssFeedReader;
        this.newsMapper = newsMapper;
        this.newsDuplicateFilter = newsDuplicateFilter;
        this.newsPersistenceService = newsPersistenceService;
        this.newsProcessor = newsProcessor;
    }

    public void importNews() {
        try {
            logger.info("Starting news import job, reading RSS feed from {}", rssUrl);

            List<RssItem> rssItems = rssFeedReader.read(rssUrl);
            logger.info("Successfully read {} RSS items", rssItems.size());

            List<NewsArticle> articles = rssItems.stream()
                    .map(newsMapper::mapToNewsArticle)
                    .toList();
            logger.info("Mapped {} RSS items to news articles", articles.size());

            List<NewsArticle> uniqueArticles = newsDuplicateFilter.removeDuplicates(articles);
            logger.info("{} unique news articles remain after duplicate filtering", uniqueArticles.size());

            newsPersistenceService.saveNews(uniqueArticles);
            logger.info("Passed {} news articles to persistence", uniqueArticles.size());

            newsProcessor.printNews(uniqueArticles);
            logger.info("Processed and printed {} news articles", uniqueArticles.size());

            logger.info("News import job successfully completed");
        } catch (Exception e) {
            logger.error("An error occurred during the news import job", e);
        }
    }
}