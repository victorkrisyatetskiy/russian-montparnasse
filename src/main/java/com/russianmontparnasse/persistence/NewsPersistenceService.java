package com.russianmontparnasse.persistence;

import com.russianmontparnasse.news.*;
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

    public List<ProcessedNewsArticle> processedNotPublished(){
        return newsArticleRepository.findByStatusAndRelevantTrueAndPublishedFalse(NewsProcessingStatus.PROCESSED)
                .stream().map(entity -> {
                    NewsArticle article = new NewsArticle(
                            entity.getTitle(),
                            entity.getLink(),
                            entity.getPublishedDate()
                    );
                    NewsSummary summary = new NewsSummary(
                            entity.getRussianTitle(),
                            entity.getRussianSummary(),
                            entity.getKeyPoint()
                    );

                    return new ProcessedNewsArticle(
                            article,
                            summary,
                            entity.getCategory()
                    );
                }).toList();
    }

    public void markAsPublished(String link){
        NewsArticleEntity entity = newsArticleRepository.findByLink(link).orElseThrow(() -> new IllegalArgumentException(
                "Article not found" + link
        ));

        entity.setPublished(true);
        newsArticleRepository.save(entity);
    }

    public void saveProcessedContent(String link, NewsCategory category, NewsSummary summary){
        NewsArticleEntity entity = newsArticleRepository.findByLink(link).orElseThrow(() -> new IllegalArgumentException("Article not found: " + link));

        entity.setRelevant(true);
        entity.setCategory(category);
        entity.setRussianTitle(summary.title());
        entity.setRussianSummary(summary.summary());
        entity.setKeyPoint(summary.keyPoint());

        newsArticleRepository.save(entity);
    }

    public void  markAsIrrelevant(String link){
        NewsArticleEntity entity = newsArticleRepository.findByLink(link).orElseThrow(() -> new IllegalArgumentException("Article not found: " + link));

        entity.setRelevant(false);
        entity.setCategory(null);
        entity.setRussianTitle(null);
        entity.setRussianSummary(null);
        entity.setKeyPoint(null);

        newsArticleRepository.save(entity);
    }

    public ProcessedNewsArticle findProcessedNotPublishedById(Long id){
        NewsArticleEntity entity = newsArticleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Article not found: " + id
                ));
        if (entity.getStatus() != NewsProcessingStatus.PROCESSED){
            throw new IllegalStateException(
                    "Article is not processed: " + id
            );
        }

        if (!Boolean.TRUE.equals(entity.getRelevant())) {
            throw new IllegalStateException(
                    "Article not relevant: " + id
            );
        }

        if (entity.isPublished()){
            throw new IllegalStateException(
                    "Article is already published: " + id
            );
        }

        NewsArticle article = new NewsArticle(
                entity.getTitle(),
                entity.getLink(),
                entity.getPublishedDate()
        );

        NewsSummary summary = new NewsSummary(
                entity.getRussianTitle(),
                entity.getRussianSummary(),
                entity.getKeyPoint()
        );
        return new ProcessedNewsArticle(
                article,
                summary,
                entity.getCategory()
        );
    }
}