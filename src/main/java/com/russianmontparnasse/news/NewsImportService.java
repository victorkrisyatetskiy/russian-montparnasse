package com.russianmontparnasse.news;

import com.russianmontparnasse.persistence.NewsPersistenceService;
import com.russianmontparnasse.rss.RssFeedReader;
import com.russianmontparnasse.rss.RssItem;
import com.russianmontparnasse.telegram.TelegramMessageFormatter;
import com.russianmontparnasse.telegram.TelegramService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsImportService {
    private final TelegramService telegramService;
    private final TelegramMessageFormatter telegramMessageFormatter;

    private static final Logger logger = LoggerFactory.getLogger(NewsImportService.class);

    private final RssFeedReader rssFeedReader;
    private final NewsMapper newsMapper;
    private final NewsDuplicateFilter newsDuplicateFilter;
    private final NewsPersistenceService newsPersistenceService;
    private final NewsProcessor newsProcessor;

    private final ArticleContentFetcher articleContentFetcher;
    private final ArticleTextExtractor articleTextExtractor;

    private final NewsRelevanceService newsRelevanceService;

    @Value("${rss.feed.urls}")
    private List<String> rssUrls;

    public NewsImportService(
            RssFeedReader rssFeedReader,
            NewsMapper newsMapper,
            NewsDuplicateFilter newsDuplicateFilter,
            NewsPersistenceService newsPersistenceService,
            NewsProcessor newsProcessor, TelegramService telegramService,
            TelegramMessageFormatter telegramMessageFormatter, ArticleContentFetcher articleContentFetcher, ArticleTextExtractor articleTextExtractor, NewsRelevanceService newsRelevanceService) {
        this.rssFeedReader = rssFeedReader;
        this.newsMapper = newsMapper;
        this.newsDuplicateFilter = newsDuplicateFilter;
        this.newsPersistenceService = newsPersistenceService;
        this.newsProcessor = newsProcessor;
        this.telegramService = telegramService;
        this.telegramMessageFormatter = telegramMessageFormatter;
        this.articleContentFetcher = articleContentFetcher;
        this.articleTextExtractor = articleTextExtractor;
        this.newsRelevanceService = newsRelevanceService;
    }

    public void importNews() {
        try {
            logger.info("Starting news import job, reading {} RSS feeds", rssUrls.size());

            List<RssItem> rssItems = rssUrls.stream().flatMap(url -> rssFeedReader.read(url).stream()).toList();
            logger.info("Successfully read {} RSS items", rssItems.size());

            List<NewsArticle> articles = rssItems.stream()
                    .map(newsMapper::mapToNewsArticle)
                    .toList();
            logger.info("Mapped {} RSS items to news articles", articles.size());

            List<NewsArticle> uniqueArticles = newsDuplicateFilter.removeDuplicates(articles);
            logger.info("{} unique news articles remain after duplicate filtering", uniqueArticles.size());

            List<NewsArticle> savedArticles = newsPersistenceService.saveNews(uniqueArticles);

            int publishedCount = 0;

            for (NewsArticle article : savedArticles){
                try {
                    String html = articleContentFetcher.fetch(article.link());
                    String articleText = articleTextExtractor.extract(html);

                    logger.info("Extracted {} characters from article: {}", articleText.length(), article.title());

                    RelevanceResult relevanceResult = newsRelevanceService.evaluate(articleText);

                    logger.info("Article relevance: {}, category: {}, reason: {} - {}",
                            relevanceResult.relevant(),
                            relevanceResult.category(),
                            relevanceResult.reason(),
                            article.title());

                    if (relevanceResult.relevant()) {
                        String message = telegramMessageFormatter.format(article);
                        telegramService.sendMessage(message);
                        publishedCount++;
                    }
                }catch (Exception e){
                    logger.error("Failed to process article: {}", article.title(), e);


                }
            }



            logger.info("Saved {} news articles to persistence", savedArticles.size());
            logger.info("Published {} new articles to Telegram", publishedCount);

            newsProcessor.printNews(uniqueArticles);
            logger.info("Processed and printed {} news articles", uniqueArticles.size());

            logger.info("News import job successfully completed");
        } catch (Exception e) {
            logger.error("An error occurred during the news import job", e);
        }
    }
}