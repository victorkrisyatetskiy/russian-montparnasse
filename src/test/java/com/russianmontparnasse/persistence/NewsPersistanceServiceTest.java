package com.russianmontparnasse.persistence;

import com.russianmontparnasse.news.NewsArticle;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NewsPersistenceServiceTest {

    private final NewsArticleRepository newsArticleRepository = mock(NewsArticleRepository.class);
    private final NewsPersistenceService newsPersistenceService = new NewsPersistenceService(newsArticleRepository);

    @Test
    void testOnlyNewArticlesAreSaved() {
        NewsArticle oldArticle = new NewsArticle("Existing Title", "http://existing-link.com", "2023-10-15");
        NewsArticle newArticle = new NewsArticle("New Title", "http://new-link.com", "2023-10-16");
        when(newsArticleRepository.existsByLink("http://existing-link.com")).thenReturn(true);
        when(newsArticleRepository.existsByLink("http://new-link.com")).thenReturn(false);

        newsPersistenceService.saveNews(List.of(oldArticle, newArticle));

        ArgumentCaptor<NewsArticleEntity> captor = ArgumentCaptor.forClass(NewsArticleEntity.class);
        verify(newsArticleRepository, times(1)).save(captor.capture());
        NewsArticleEntity savedEntity = captor.getValue();

        assertEquals("New Title", savedEntity.getTitle());
        assertEquals("http://new-link.com", savedEntity.getLink());
        assertEquals("2023-10-16", savedEntity.getPublishedDate());
    }

    @Test
    void testEmptyList() {
        newsPersistenceService.saveNews(List.of());
        verifyNoInteractions(newsArticleRepository);
    }

    @Test
    void testNullInput() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> newsPersistenceService.saveNews(null));
        assertEquals("Articles list cannot be null", exception.getMessage());
    }

    @Test
    void testInputListIsNotModified() {
        List<NewsArticle> originalList = new ArrayList<>();
        originalList.add(new NewsArticle("Title", "http://link.com", "2023-10-17"));
        List<NewsArticle> copy = new ArrayList<>(originalList);

        newsPersistenceService.saveNews(originalList);

        assertEquals(copy, originalList);
    }
}