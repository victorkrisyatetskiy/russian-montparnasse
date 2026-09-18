package com.russianmontparnasse.persistence;

import com.russianmontparnasse.news.NewsCategory;
import com.russianmontparnasse.news.NewsProcessingStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "news_articles")
public class NewsArticleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(unique = true)
    private String link;

    private String publishedDate;

    @Enumerated(EnumType.STRING)
    private NewsProcessingStatus status;

    private Boolean published;

    private Boolean relevant;

    @Enumerated(EnumType.STRING)
    private NewsCategory category;

    private String russianTitle;

    @Column(length = 4000)
    private String russianSummary;

    @Column(length = 2000)
    private String keyPoint;

    public NewsArticleEntity() {
    }

    public NewsArticleEntity(String title, String link, String publishedDate) {
        this.title = title;
        this.link = link;
        this.publishedDate = publishedDate;
        this.status = NewsProcessingStatus.NEW;
        this.published = false;
    }

    public NewsProcessingStatus getStatus(){
        return status;
    }

    public void setStatus(NewsProcessingStatus status){
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Boolean getRelevant() {
        return relevant;
    }

    public void setRelevant(Boolean relevant) {
        this.relevant = relevant;
    }

    public NewsCategory getCategory() {
        return category;
    }

    public void setCategory(NewsCategory category) {
        this.category = category;
    }

    public String getRussianTitle() {
        return russianTitle;
    }

    public void setRussianTitle(String russianTitle) {
        this.russianTitle = russianTitle;
    }

    public String getRussianSummary() {
        return russianSummary;
    }

    public void setRussianSummary(String russianSummary) {
        this.russianSummary = russianSummary;
    }

    public String getKeyPoint() {
        return keyPoint;
    }

    public void setKeyPoint(String keyPoint) {
        this.keyPoint = keyPoint;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isPublished() {
        return Boolean.TRUE.equals(published);
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

}