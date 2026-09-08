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


    @Test
    void shouldFetchAndExtractContentForNewArticle(){
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
                newsRelevanceService
        );

        ReflectionTestUtils.setField(service,"rssUrls", List.of("https://example.com/rss"));

        RssItem rssItem = mock(RssItem.class);

        NewsArticle article = new NewsArticle("Test article", "https://example.com/article", "2026-09-04");

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
        when(telegramMessageFormatter.format(article))
                .thenReturn("Telegram message");

        service.importNews();

        verify(articleContentFetcher)
                .fetch("https://example.com/article");

        verify(articleTextExtractor)
                .extract("<html><body>Article content</body></html>");

        verify(newsRelevanceService).evaluate("Article content");

        verify(telegramMessageFormatter).format(article);

        verify(telegramService).sendMessage("Telegram message");

    }

    @Test
    void shouldNotPublishIrrelevantArticleToTelegram(){
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
                newsRelevanceService
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

        verifyNoInteractions(telegramMessageFormatter);
        verifyNoInteractions(telegramService);
    }

}
