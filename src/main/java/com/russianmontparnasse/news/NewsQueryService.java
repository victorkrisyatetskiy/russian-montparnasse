package com.russianmontparnasse.news;

import com.russianmontparnasse.persistence.NewsArticleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsQueryService {

    private final NewsArticleRepository newsArticleRepository;
    private final NewsMapper newsMapper;

    public NewsQueryService(NewsArticleRepository newsArticleRepository, NewsMapper newsMapper) {
        this.newsArticleRepository = newsArticleRepository;
        this.newsMapper = newsMapper;
    }

    public List<NewsArticle> getAllNews() {
        return newsArticleRepository.findAll()
                .stream()
                .map(newsMapper::mapToNewsArticle)
                .toList();
    }
}