package com.russianmontparnasse.news;

import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

@Component
public class ArticleTextExtractor {
    public String extract(String html){
        return Jsoup.parse(html).text();
    }
}
