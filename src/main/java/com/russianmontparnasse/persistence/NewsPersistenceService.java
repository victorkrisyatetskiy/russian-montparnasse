package com.russianmontparnasse.persistence;

import com.russianmontparnasse.news.NewsArticle;
import com.russianmontparnasse.news.NewsProcessingStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NewsPersistenceService {

    public void updateStatus(String link, NewsProcessingStatus status){
        NewsArticleEntity entity = newsArticleRepository.findByLink(link).orElseThrow(() -> new IllegalArgumentException(
                "Article not found: " + link
        ));

        entity.setStatus(status);
        newsArticleRepository.save(entity);
    }

    private final NewsArticleRepository newsArticleRepository;

    public NewsPersistenceService(NewsArticleRepository newsArticleRepository) {
        this.newsArticleRepository = newsArticleRepository;
    }

    public List<NewsArticle> saveNews(List<NewsArticle> articles) {
        if (articles == null) {
            throw new IllegalArgumentException("Articles list cannot be null");
        }

        List<NewsArticle> savedArticles = new ArrayList<>();

        for (NewsArticle article : articles) {
            if (!newsArticleRepository.existsByLink(article.link())) {
                NewsArticleEntity entity = new NewsArticleEntity(
                        article.title(),
                        article.link(),
                        article.publishedDate()
                );
                newsArticleRepository.save(entity);
                savedArticles.add(article);
            }
        }
        return savedArticles;
    }

    public List<NewsArticle> findByStatus(NewsProcessingStatus status){
        return newsArticleRepository.findByStatus(status).stream().map(entity -> new NewsArticle(
                entity.getTitle(),
                entity.getLink(),
                entity.getPublishedDate()
        )).toList();
    }

    public void markAsPublished(String link){
        NewsArticleEntity entity = newsArticleRepository.findByLink(link).orElseThrow(() -> new IllegalArgumentException(
                "Article not found" + link
        ));

        entity.setPublished(true);
        newsArticleRepository.save(entity);
    }
}