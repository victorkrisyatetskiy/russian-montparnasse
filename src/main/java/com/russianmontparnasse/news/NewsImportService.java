package com.russianmontparnasse.news;

import com.russianmontparnasse.persistence.NewsPersistenceService;
import com.russianmontparnasse.rss.RssFeedReader;
import com.russianmontparnasse.rss.RssItem;
import com.russianmontparnasse.telegram.TelegramMessageFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class NewsImportService {
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
    private final NewsSummaryService newsSummaryService;


    private final NewsPublicationService newsPublicationService;

    @Value("${rss.feed.urls}")
    private List<String> rssUrls;

    public NewsImportService(
            RssFeedReader rssFeedReader,
            NewsMapper newsMapper,
            NewsDuplicateFilter newsDuplicateFilter,
            NewsPersistenceService newsPersistenceService,
            NewsProcessor newsProcessor,
            TelegramMessageFormatter telegramMessageFormatter, ArticleContentFetcher articleContentFetcher,
            ArticleTextExtractor articleTextExtractor, NewsRelevanceService newsRelevanceService,
            NewsSummaryService newsSummaryService,
            NewsPublicationService newsPublicationService) {
        this.rssFeedReader = rssFeedReader;
        this.newsMapper = newsMapper;
        this.newsDuplicateFilter = newsDuplicateFilter;
        this.newsPersistenceService = newsPersistenceService;
        this.newsProcessor = newsProcessor;
        this.telegramMessageFormatter = telegramMessageFormatter;
        this.articleContentFetcher = articleContentFetcher;
        this.articleTextExtractor = articleTextExtractor;
        this.newsRelevanceService = newsRelevanceService;
        this.newsSummaryService = newsSummaryService;
        this.newsPublicationService = newsPublicationService;
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

            List<NewsArticle> failedArticles = newsPersistenceService.findByStatus(NewsProcessingStatus.FAILED);

            List<NewsArticle> newArticles = newsPersistenceService.findByStatus(NewsProcessingStatus.NEW);

            List<NewsArticle> savedArticles = newsPersistenceService.saveNews(uniqueArticles);

            Map<String, NewsArticle> articlesToProcess = new LinkedHashMap<>();

            for (NewsArticle article : failedArticles) {
                articlesToProcess.put(article.link(), article);
            }

            for (NewsArticle article : newArticles) {
                articlesToProcess.put(article.link(), article);
            }

            for (NewsArticle article : savedArticles) {
                articlesToProcess.put(article.link(), article);
            }


            logger.info(
                    "Processing {} articles: {} pending, {} failed, {} newly saved",
                    articlesToProcess.size(),
                    newArticles.size(),
                    failedArticles.size(),
                    savedArticles.size()
            );


            for (NewsArticle article : articlesToProcess.values()) {
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
                        NewsSummary summary = newsSummaryService.summarize(articleText);

                        newsPersistenceService.saveProcessedContent(article.link(), relevanceResult.category(), summary);

                        String message = telegramMessageFormatter.format(article, summary, relevanceResult.category());

                        logger.info("Telegram preview:\n{}", message);

                    } else {
                        newsPersistenceService.markAsIrrelevant(article.link());
                    }
                    newsPersistenceService.updateStatus(
                            article.link(),
                            NewsProcessingStatus.PROCESSED
                    );
                } catch (Exception e) {
                    logger.error("Failed to process article: {}", article.title(), e);
                    try {
                        newsPersistenceService.updateStatus(
                                article.link(),
                                NewsProcessingStatus.FAILED
                        );
                    } catch (Exception statusException) {
                        logger.error(
                                "Failed to update status for article: {}",
                                article.title(),
                                statusException
                        );
                    }
                }
            }


            logger.info("Saved {} news articles to persistence", savedArticles.size());

            newsProcessor.printNews(uniqueArticles);
            logger.info("Processed and printed {} news articles", uniqueArticles.size());

            logger.info("News import job successfully completed");
        } catch (Exception e) {
            logger.error("An error occurred during the news import job", e);
        }
    }

//    private void publishProcessedArticle(ProcessedNewsArticle processedNewsArticle){
//        String message = telegramMessageFormatter.format(
//                processedNewsArticle.article(),
//                processedNewsArticle.summary(),
//                processedNewsArticle.category()
//        );
//
//        telegramService.sendMessage(message);
//
//        newsPersistenceService.markAsPublished(processedNewsArticle.article().link());
//    }

    public void publishProcessedArticleById(Long id) {
        ProcessedNewsArticle article = newsPersistenceService.findProcessedNotPublishedById(id);

        newsPublicationService.publish(article);
    }
}