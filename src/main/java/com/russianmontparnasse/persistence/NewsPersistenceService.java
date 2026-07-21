package com.russianmontparnasse.persistence;

import com.russianmontparnasse.news.NewsArticle;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsPersistenceService {

    private final NewsArticleRepository newsArticleRepository;

    public NewsPersistenceService(NewsArticleRepository newsArticleRepository) {
        this.newsArticleRepository = newsArticleRepository;
    }

    public void saveNews(List<NewsArticle> articles) {
        if (articles == null) {
            throw new IllegalArgumentException("Articles list cannot be null");
        }

        for (NewsArticle article : articles) {
            if (!newsArticleRepository.existsByLink(article.link())) {
                NewsArticleEntity entity = new NewsArticleEntity(
                        article.title(),
                        article.link(),
                        article.publishedDate()
                );
                newsArticleRepository.save(entity);
            }
        }
    }
}