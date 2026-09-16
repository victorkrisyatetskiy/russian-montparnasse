package com.russianmontparnasse.news;


import com.russianmontparnasse.telegram.TelegramMessageFormatter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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

    @Test
    @Disabled("Calls real article source and OpenAI API")
    void shouldGenerateTelegramPreviewForRealArticle() {
        String article = "https://www.service-public.gouv.fr/particuliers/actualites/A18394?xtor=RSS-112";

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
}
