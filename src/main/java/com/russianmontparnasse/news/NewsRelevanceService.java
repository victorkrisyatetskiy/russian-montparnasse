package com.russianmontparnasse.news;

import org.springframework.stereotype.Service;

@Service
public class NewsRelevanceService {
    public RelevanceResult evaluate(String articleText){
        return new RelevanceResult(true, NewsCategory.OTHER, "Temporary relevance result");
    }
}
