package com.russianmontparnasse.news;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;


public class NewsPublicationSchedulerTest {

    @Test
    void shouldNotPublishWhenDisabled() {
        // Arrange
        NewsPublicationService publicationService = mock(NewsPublicationService.class);

        NewsPublicationScheduler scheduler = new NewsPublicationScheduler(publicationService, false);

        // Act
        scheduler.publishScheduledNews();

        //Assert
        verifyNoInteractions(publicationService);
    }

    @Test
    void shouldPublishWhenEnabled() {
        // Arrange
        NewsPublicationService publicationService = mock(NewsPublicationService.class);

        NewsPublicationScheduler scheduler = new NewsPublicationScheduler(publicationService,true);

        // Act
        scheduler.publishScheduledNews();

        // Assert
        verify(publicationService, times(1)).publishNext();
    }
}
