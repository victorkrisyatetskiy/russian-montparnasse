package com.russianmontparnasse.telegram;

import com.russianmontparnasse.news.NewsArticle;
import org.springframework.stereotype.Component;

@Component
public class TelegramMessageFormatter {

    public String format(NewsArticle article){
        return article.title() + "\n\n" + article.link();
    }
}
