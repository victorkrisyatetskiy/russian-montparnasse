package com.russianmontparnasse.news;


import com.russianmontparnasse.rss.RssFeedReader;
import com.russianmontparnasse.rss.RssItem;
import com.russianmontparnasse.telegram.TelegramMessageFormatter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(properties = {
        "news.import.runner.enabled=false",
        "spring.task.scheduling.enabled=false"})
public class NewsPreviewIntegrationTest {
    @Autowired
    private ArticleContentFetcher articleContentFetcher;

    @Autowired
    private ArticleTextExtractor articleTextExtractor;

    @Autowired
    private NewsRelevanceService newsRelevanceService;

    @Autowired
    private NewsSummaryService newsSummaryService;

    @Autowired
    private TelegramMessageFormatter telegramMessageFormatter;

    @Autowired
    private RssFeedReader rssFeedReader;

    @Test
    @Disabled("Calls real article source and OpenAI API")
    void shouldGenerateTelegramPreviewForRealArticle() {
        String article = "https://www.service-public.gouv.fr/particuliers/actualites/A17692?xtor=RSS-111";

        String html = articleContentFetcher.fetch(article);

        String articleText = articleTextExtractor.extract(html);

        System.out.println("===EXTRACTED ARTICLE===");
        System.out.println(articleText);

        RelevanceResult relevanceResult = newsRelevanceService.evaluate(articleText);

        System.out.println("===RELEVANCE===");
        System.out.println(relevanceResult);

        if (!relevanceResult.relevant()) {
            System.out.println("Article is not relevant");
            return;
        }

        NewsSummary summary = newsSummaryService.summarize(articleText);
        System.out.println("===SUMMARY===");
        System.out.println(summary);

        NewsArticle newsArticle = new NewsArticle(
                summary.title(),
                article,
                null
        );

        String telegramMessage = telegramMessageFormatter.format(
                newsArticle,
                summary,
                relevanceResult.category()
        );

        System.out.println("===TELEGRAM PREVIEW===");
        System.out.println(telegramMessage);


    }

    @Test
    @Disabled("Calls real RSS feed")
    void shouldPreviewRealNewsBatch() {
        String rssUrl =
                "https://www.service-public.gouv.fr/abonnements/rss/actu-actualites-particuliers.rss";

        List<RssItem> rssItems = rssFeedReader.read(rssUrl)
                .stream()
                .limit(30)
                .toList();

        System.out.println("Loaded RSS items: " + rssItems.size());

        for (RssItem item : rssItems) {
            System.out.println("\n========================================");
            System.out.println("RSS: " + item.title());
            System.out.println("========================================");

            try {
                String html = articleContentFetcher.fetch(item.link());
                String articleText = articleTextExtractor.extract(html);

                RelevanceResult relevance =
                        newsRelevanceService.evaluate(articleText);

                System.out.println(
                        "Relevance: " + relevance.relevant()
                                + ", category: " + relevance.category()
                                + ", reason: " + relevance.reason()
                );

                if (!relevance.relevant()) {
                    System.out.println("SKIPPED AS IRRELEVANT");
                    continue;
                }

                NewsSummary summary =
                        newsSummaryService.summarize(articleText);

                NewsArticle article = new NewsArticle(
                        item.title(),
                        item.link(),
                        item.publishedDate()
                );

                String message = telegramMessageFormatter.format(
                        article,
                        summary,
                        relevance.category()
                );

                System.out.println("\nTELEGRAM PREVIEW:");
                System.out.println(message);

            } catch (Exception e) {
                System.out.println(
                        "FAILED TO PROCESS: " + e.getMessage()
                );
            }
        }
    }

    @Test
   @Disabled("Calls real RSS feeds, article sources and OpenAI API")
    void shouldEvaluateRelevanceForRealNewsBatch() {
        List<String> rssUrls = List.of(
                "https://www.service-public.gouv.fr/abonnements/rss/actu-actualites-particuliers.rss",
                "https://www.service-public.fr/abonnements/rss/actu-actu-pro.rss"
        );

        for (String rssUrl : rssUrls) {
            System.out.println("\n========================================");
            System.out.println("RSS FEED: " + rssUrl);
            System.out.println("========================================");

            List<RssItem> rssItems = rssFeedReader.read(rssUrl)
                    .stream()
                    .limit(15)
                    .toList();

            System.out.println("Loaded RSS items: " + rssItems.size());

            for (RssItem item : rssItems) {
                System.out.println("\n----------------------------------------");
                System.out.println("RSS: " + item.title());

                try {
                    String html = articleContentFetcher.fetch(item.link());
                    String articleText = articleTextExtractor.extract(html);

                    RelevanceResult relevance =
                            newsRelevanceService.evaluate(articleText);

                    System.out.println(
                            "Relevance: " + relevance.relevant()
                                    + ", category: " + relevance.category()
                                    + ", reason: " + relevance.reason()
                    );

                } catch (Exception e) {
                    System.out.println(
                            "FAILED TO PROCESS: " + e.getMessage()
                    );
                }
            }
        }
    }
    @Autowired
    private NewsImportService newsImportService;

    @Test
    й@Disabled("Publishes a real article to Telegram")
    void shouldPublishOneProcessedArticleToTelegram() {
        newsImportService.publishProcessedArticleById(285L);
    }

}



