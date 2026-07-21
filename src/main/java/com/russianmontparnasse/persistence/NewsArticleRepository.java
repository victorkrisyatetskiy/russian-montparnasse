package com.russianmontparnasse.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsArticleRepository extends JpaRepository<NewsArticleEntity, Long> {
    boolean existsByLink(String link);
}