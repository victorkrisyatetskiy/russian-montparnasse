package com.russianmontparnasse.persistence;

import com.russianmontparnasse.news.NewsProcessingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NewsArticleRepository extends JpaRepository<NewsArticleEntity, Long> {

    boolean existsByLink(String link);
    List<NewsArticleEntity> findByStatus(NewsProcessingStatus status);

    Optional<NewsArticleEntity> findByLink(String link);
}