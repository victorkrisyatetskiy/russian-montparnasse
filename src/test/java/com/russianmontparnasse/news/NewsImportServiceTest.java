package com.russianmontparnasse.news;

import com.russianmontparnasse.persistence.NewsPersistenceService;
import com.russianmontparnasse.rss.RssFeedReader;
import com.russianmontparnasse.rss.RssItem;
import com.russianmontparnasse.telegram.TelegramMessageFormatter;
import com.russianmontparnasse.telegram.TelegramService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.mockito.Mockito.*;

public class NewsImportServiceTest {

    private final RssFeedReader rssFeedReader = mock(RssFeedReader.class);
    private final NewsMapper newsMapper = mock(NewsMapper.class);
    private final NewsDuplicateFilter newsDuplicateFilter = mock(NewsDuplicateFilter.class);
    private final NewsPersistenceService newsPersistenceService = mock(NewsPersistenceService.class);
    private final NewsProcessor newsProcessor = mock(NewsProcessor.class);
    private final TelegramService telegramService = mock(TelegramService.class);
    private final TelegramMessageFormatter telegramMessageFormatter = mock(TelegramMessageFormatter.class);
    private final ArticleContentFetcher articleContentFetcher = mock(ArticleContentFetcher.class);
    private final ArticleTextExtractor articleTextExtractor = mock(ArticleTextExtractor.class);
    private final NewsRelevanceService newsRelevanceService = mock(NewsRelevanceService.class);
    private final NewsSummaryService newsSummaryService = mock(NewsSummaryService.class);


    @Test
    void shouldFetchAndExtractContentForNewArticle() {
        NewsImportService service = new NewsImportService(
                rssFeedReader,
                newsMapper,
                newsDuplicateFilter,
                newsPersistenceService,
                newsProcessor,
                telegramService,
                telegramMessageFormatter,
                articleContentFetcher,
                articleTextExtractor,
                newsRelevanceService,
                newsSummaryService,
                true

        );

        ReflectionTestUtils.setField(service, "rssUrls", List.of("https://example.com/rss"));

        RssItem rssItem = mock(RssItem.class);

        NewsArticle article = new NewsArticle("Test article", "https://example.com/article", "2026-09-04");


        NewsSummary summary = new NewsSummary(
                "Русский заголовок",
                "Русское описание",
                "Практический вывод"
        );
        when(rssFeedReader.read("https://example.com/rss"))
                .thenReturn(List.of(rssItem));

        when(newsMapper.mapToNewsArticle(rssItem))
                .thenReturn(article);

        when(newsDuplicateFilter.removeDuplicates(List.of(article)))
                .thenReturn(List.of(article));

        when(newsPersistenceService.saveNews(List.of(article)))
                .thenReturn(List.of(article));

        when(articleContentFetcher.fetch(article.link()))
                .thenReturn("<html><body>Article content</body></html>");

        when(articleTextExtractor.extract(
                "<html><body>Article content</body></html>"
        )).thenReturn("Article content");

        when(newsRelevanceService.evaluate("Article content")).thenReturn(new RelevanceResult(
                true,
                NewsCategory.OTHER,
                "Test reason"
        ));

        when(newsSummaryService.summarize("Article content")).thenReturn(summary);


        when(telegramMessageFormatter.format(article, summary, NewsCategory.OTHER))
                .thenReturn("Telegram message");

        service.importNews();

        verify(articleContentFetcher)
                .fetch("https://example.com/article");

        verify(articleTextExtractor)
                .extract("<html><body>Article content</body></html>");

        verify(newsRelevanceService).evaluate("Article content");

        verify(newsSummaryService).summarize("Article content");

        verify(newsPersistenceService).saveProcessedContent(article.link(), NewsCategory.OTHER, summary);

        verify(telegramMessageFormatter).format(article, summary, NewsCategory.OTHER);

        verify(telegramService).sendMessage("Telegram message");

        verify(newsPersistenceService).markAsPublished(article.link());
    }

    @Test
    void shouldNotPublishIrrelevantArticleToTelegram() {
        NewsImportService service = new NewsImportService(
                rssFeedReader,
                newsMapper,
                newsDuplicateFilter,
                newsPersistenceService,
                newsProcessor,
                telegramService,
                telegramMessageFormatter,
                articleContentFetcher,
                articleTextExtractor,
                newsRelevanceService,
                newsSummaryService,
                true
        );

        ReflectionTestUtils.setField(service, "rssUrls", List.of("https://example.rss"));

        RssItem rssItem = mock(RssItem.class);

        NewsArticle article = new NewsArticle("Test article", "https://example.rss", "2026-09-04");

        when(rssFeedReader.read("https://example.rss")).thenReturn(List.of(rssItem));

        when(newsMapper.mapToNewsArticle(rssItem)).thenReturn(article);

        when(newsDuplicateFilter.removeDuplicates(List.of(article))).thenReturn(List.of(article));

        when(newsPersistenceService.saveNews(List.of(article))).thenReturn(List.of(article));

        when(articleContentFetcher.fetch(article.link())).thenReturn("<html><body>Article content</body></html>");

        when(articleTextExtractor.extract("<html><body>Article content</body></html>")).thenReturn("Article content");

        when(newsRelevanceService.evaluate("Article content")).thenReturn(new RelevanceResult(
                false,
                null,
                "Not relevant"));

        service.importNews();

        verify(newsRelevanceService).evaluate("Article content");

        verify(newsPersistenceService).markAsIrrelevant(article.link());

        verifyNoInteractions(telegramMessageFormatter);
        verifyNoInteractions(telegramService);
    }

    @Test
    void shouldContinueProcessingWhenArticleFails() {
        NewsImportService service = new NewsImportService(
                rssFeedReader,
                newsMapper,
                newsDuplicateFilter,
                newsPersistenceService,
                newsProcessor,
                telegramService,
                telegramMessageFormatter,
                articleContentFetcher,
                articleTextExtractor,
                newsRelevanceService,
                newsSummaryService,
                true
        );
        ReflectionTestUtils.setField(
                service,
                "rssUrls",
                List.of("https://example.com/rss")
        );

        RssItem firstRssItem = mock(RssItem.class);
        RssItem secondRssItem = mock(RssItem.class);

        NewsArticle firstArticle = new NewsArticle(
                "First article",
                "https://example.com/first",
                "2026-09-09"
        );

        NewsArticle secondArticle = new NewsArticle(
                "Second article",
                "https://example.com/second",
                "2026-09-09"
        );

        NewsSummary secondSummary = new NewsSummary(
                "Second Russian title",
                "Second Russian summary",
                "Second key point"
        );

        when(rssFeedReader.read("https://example.com/rss")).thenReturn(List.of(firstRssItem, secondRssItem));

        when(newsMapper.mapToNewsArticle(firstRssItem)).thenReturn(firstArticle);
        when(newsMapper.mapToNewsArticle(secondRssItem)).thenReturn(secondArticle);

        when(newsDuplicateFilter.removeDuplicates(List.of(firstArticle, secondArticle))).thenReturn(List.of(firstArticle, secondArticle));

        when(newsPersistenceService.saveNews(List.of(firstArticle, secondArticle))).thenReturn(List.of(firstArticle, secondArticle));

        when(articleContentFetcher.fetch(firstArticle.link())).thenThrow(new RuntimeException("Failed to fetch article"));

        when(articleContentFetcher.fetch(secondArticle.link())).thenReturn("<html><body>Second article content</body></html>");

        when(articleTextExtractor.extract("<html><body>Second article content</body></html>")).thenReturn("Second article content");

        when(newsRelevanceService.evaluate("Second article content")).thenReturn(new RelevanceResult(
                true,
                NewsCategory.OTHER,
                "Relevant"
        ));

        when(telegramMessageFormatter.format(secondArticle, secondSummary, NewsCategory.OTHER)).thenReturn("Second Telegram message");

        when(newsSummaryService.summarize("Second article content")).thenReturn(secondSummary);

        service.importNews();

        verify(newsRelevanceService).evaluate("Second article content");
        verify(telegramService).sendMessage("Second Telegram message");
    }

    @Test
    void shouldProcessPreviouslySavedNewArticle() {
        NewsImportService service = new NewsImportService(
                rssFeedReader,
                newsMapper,
                newsDuplicateFilter,
                newsPersistenceService,
                newsProcessor,
                telegramService,
                telegramMessageFormatter,
                articleContentFetcher,
                articleTextExtractor,
                newsRelevanceService,
                newsSummaryService,
                true
        );

        ReflectionTestUtils.setField(
                service,
                "rssUrls",
                List.of("https://example.com/rss")
        );

        NewsArticle pendingArticle = new NewsArticle(
                "Pending article",
                "https://example.com/pending",
                "2026-09-15"
        );

        when(rssFeedReader.read("https://example.com/rss"))
                .thenReturn(List.of());

        when(newsDuplicateFilter.removeDuplicates(List.of()))
                .thenReturn(List.of());

        when(newsPersistenceService.findByStatus(NewsProcessingStatus.FAILED))
                .thenReturn(List.of());

        when(newsPersistenceService.findByStatus(NewsProcessingStatus.NEW))
                .thenReturn(List.of(pendingArticle));

        when(newsPersistenceService.saveNews(List.of()))
                .thenReturn(List.of());

        when(articleContentFetcher.fetch(pendingArticle.link()))
                .thenReturn("<html>content</html>");

        when(articleTextExtractor.extract("<html>content</html>"))
                .thenReturn("Article content");

        when(newsRelevanceService.evaluate("Article content"))
                .thenReturn(
                        new RelevanceResult(
                                false,
                                null,
                                "Not relevant"
                        )
                );

        service.importNews();

        verify(articleContentFetcher).fetch(pendingArticle.link());
        verify(newsRelevanceService).evaluate("Article content");

        verify(newsPersistenceService).updateStatus(
                pendingArticle.link(),
                NewsProcessingStatus.PROCESSED
        );
    }

    @Test
    void shouldRetryPreviouslyFailedArticle() {
        NewsImportService service = new NewsImportService(
                rssFeedReader,
                newsMapper,
                newsDuplicateFilter,
                newsPersistenceService,
                newsProcessor,
                telegramService,
                telegramMessageFormatter,
                articleContentFetcher,
                articleTextExtractor,
                newsRelevanceService,
                newsSummaryService,
                true
        );

        ReflectionTestUtils.setField(
                service,
                "rssUrls",
                List.of("https://example.com/rss")
        );

        NewsArticle failedArticle = new NewsArticle(
                "Failed article",
                "https://example.com/failed",
                "2026-09-15"
        );

        when(rssFeedReader.read("https://example.com/rss"))
                .thenReturn(List.of());

        when(newsDuplicateFilter.removeDuplicates(List.of()))
                .thenReturn(List.of());

        when(newsPersistenceService.findByStatus(NewsProcessingStatus.FAILED))
                .thenReturn(List.of(failedArticle));

        when(newsPersistenceService.findByStatus(NewsProcessingStatus.NEW))
                .thenReturn(List.of());

        when(newsPersistenceService.saveNews(List.of()))
                .thenReturn(List.of());

        when(articleContentFetcher.fetch(failedArticle.link()))
                .thenReturn("<html>content</html>");

        when(articleTextExtractor.extract("<html>content</html>"))
                .thenReturn("Article content");

        when(newsRelevanceService.evaluate("Article content"))
                .thenReturn(
                        new RelevanceResult(
                                false,
                                null,
                                "Not relevant"
                        )
                );

        service.importNews();

        verify(articleContentFetcher)
                .fetch(failedArticle.link());

        verify(newsRelevanceService)
                .evaluate("Article content");

        verify(newsPersistenceService).updateStatus(
                failedArticle.link(),
                NewsProcessingStatus.PROCESSED
        );
    }

    @Test
    void shouldNotSendTelegramMessageWhenPublishingIsDisabled() {
        NewsImportService service = new NewsImportService(
                rssFeedReader,
                newsMapper,
                newsDuplicateFilter,
                newsPersistenceService,
                newsProcessor,
                telegramService,
                telegramMessageFormatter,
                articleContentFetcher,
                articleTextExtractor,
                newsRelevanceService,
                newsSummaryService,
                false
        );

        ReflectionTestUtils.setField(service, "rssUrls", List.of("https://example.com/rss"));

        RssItem rssItem = mock(RssItem.class);

        NewsArticle article = new NewsArticle("Test article", "https://example.com/article", "2026-09-04");


        NewsSummary summary = new NewsSummary(
                "Русский заголовок",
                "Русское описание",
                "Практический вывод"
        );
        when(rssFeedReader.read("https://example.com/rss"))
                .thenReturn(List.of(rssItem));

        when(newsMapper.mapToNewsArticle(rssItem))
                .thenReturn(article);

        when(newsDuplicateFilter.removeDuplicates(List.of(article)))
                .thenReturn(List.of(article));

        when(newsPersistenceService.saveNews(List.of(article)))
                .thenReturn(List.of(article));

        when(articleContentFetcher.fetch(article.link()))
                .thenReturn("<html><body>Article content</body></html>");

        when(articleTextExtractor.extract(
                "<html><body>Article content</body></html>"
        )).thenReturn("Article content");

        when(newsRelevanceService.evaluate("Article content")).thenReturn(new RelevanceResult(
                true,
                NewsCategory.OTHER,
                "Test reason"
        ));

        when(newsSummaryService.summarize("Article content")).thenReturn(summary);


        when(telegramMessageFormatter.format(article, summary, NewsCategory.OTHER))
                .thenReturn("Telegram message");

        service.importNews();

        verify(articleContentFetcher)
                .fetch("https://example.com/article");

        verify(articleTextExtractor)
                .extract("<html><body>Article content</body></html>");

        verify(newsRelevanceService).evaluate("Article content");

        verify(newsSummaryService).summarize("Article content");

        verify(telegramMessageFormatter).format(article, summary, NewsCategory.OTHER);

        verifyNoInteractions(telegramService);

        verify(newsPersistenceService, never()).markAsPublished(article.link());

        //verify(telegramService).sendMessage("Telegram message");
    }

    @Test
    void shouldNotMarkArticleAsPublishedWhenTelegramFails() {
        NewsImportService service = new NewsImportService(
                rssFeedReader,
                newsMapper,
                newsDuplicateFilter,
                newsPersistenceService,
                newsProcessor,
                telegramService,
                telegramMessageFormatter,
                articleContentFetcher,
                articleTextExtractor,
                newsRelevanceService,
                newsSummaryService,
                true
        );

        ReflectionTestUtils.setField(
                service,
                "rssUrls",
                List.of("https://example.com/rss")
        );

        RssItem rssItem = mock(RssItem.class);

        NewsArticle article = new NewsArticle(
                "Text article",
                "https://example.com/article",
                "2026-09-16"
        );

        NewsSummary summary = new NewsSummary(
                "Русский заголовок",
                "Русское саммари",
                "Практический вывод"
        );

        when(rssFeedReader.read("https://example.com/rss")).thenReturn(List.of(rssItem));
        when(newsMapper.mapToNewsArticle(rssItem)).thenReturn(article);
        when(newsDuplicateFilter.removeDuplicates(List.of(article))).thenReturn(List.of(article));
        when(newsPersistenceService.saveNews(List.of(article))).thenReturn(List.of(article));
        when(articleContentFetcher.fetch(article.link())).thenReturn("<html><body>Article content</body></html>");
        when(articleTextExtractor.extract("<html><body>Article content</body></html>")).thenReturn("Article content");
        when(newsRelevanceService.evaluate("Article content")).thenReturn(new RelevanceResult(true, NewsCategory.OTHER, "Test reason"));
        when(newsSummaryService.summarize("Article content")).thenReturn(summary);
        when(telegramMessageFormatter.format(article, summary, NewsCategory.OTHER)).thenReturn("Telegram message");

        doThrow(new RuntimeException("Telegram unavailable")).when(telegramService)
                .sendMessage("Telegram message");

        service.importNews();

        verify(telegramService).sendMessage("Telegram message");

        verify(newsPersistenceService, never()).markAsPublished(article.link());

        verify(newsPersistenceService).updateStatus(article.link(), NewsProcessingStatus.FAILED);



    }

}
