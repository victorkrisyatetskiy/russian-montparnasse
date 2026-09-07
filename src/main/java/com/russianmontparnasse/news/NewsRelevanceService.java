package com.russianmontparnasse.news;

import org.springframework.stereotype.Service;

@Service
public class NewsRelevanceService {
    public boolean isRelevant(String articleText){
        return true;
    }
}
