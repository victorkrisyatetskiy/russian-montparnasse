package com.russianmontparnasse.news;

import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class NewsDuplicateFilter {

    public List<NewsArticle> removeDuplicates(List<NewsArticle> articles) {
        if (articles == null) {
            throw new IllegalArgumentException("Articles list cannot be null");
        }

        List<NewsArticle> filteredArticles = new ArrayList<>();
        Set<String> seenLinks = new HashSet<>();

        for (NewsArticle article : articles) {
            String link = article.link();
            if (link == null || !seenLinks.contains(link)) {
                filteredArticles.add(article);
                if (link != null) {
                    seenLinks.add(link);
                }
            }
        }

        return filteredArticles;
    }
}