package com.russianmontparnasse.persistence;

import com.russianmontparnasse.news.NewsArticle;
import com.russianmontparnasse.news.NewsProcessingStatus;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Test
    void shouldUpdateArticleStatus(){
        NewsArticleEntity entity = new NewsArticleEntity(
                "Test article",
                "https://example.com/article",
                "2026-09-15"
        );
        when(newsArticleRepository.findByLink("https://example.com/article")).thenReturn(Optional.of(entity));

        newsPersistenceService.updateStatus("https://example.com/article", NewsProcessingStatus.PROCESSED);

        assertEquals(NewsProcessingStatus.PROCESSED, entity.getStatus());
    }

    @Test
    void shouldFindArticleByStatus() {
        NewsArticleEntity entity = new NewsArticleEntity(
                "Failed article",
                "https://example.com/article",
                "2026-09-15"
        );

        entity.setStatus(NewsProcessingStatus.FAILED);

        when(newsArticleRepository.findByStatus(NewsProcessingStatus.FAILED)).thenReturn(List.of(entity));

        List<NewsArticle> result = newsPersistenceService.findByStatus(NewsProcessingStatus.FAILED);

        assertEquals(1, result.size());
        assertEquals("Failed article", result.get(0).title());
        assertEquals("https://example.com/article", result.get(0).link());
        assertEquals("2026-09-15", result.get(0).publishedDate());

        verify(newsArticleRepository).findByStatus(NewsProcessingStatus.FAILED);
    }


}