package com.russianmontparnasse.telegram;

import com.russianmontparnasse.news.NewsArticle;
import com.russianmontparnasse.news.NewsSummary;
import org.springframework.stereotype.Component;

@Component
public class TelegramMessageFormatter {

    public String format(NewsArticle article){
        return article.title() + "\n\n" + article.link();
    }

    public String format(NewsArticle article, NewsSummary summary){
        return summary.title() + "\n\n" + summary.summary() + "\n\n" + summary.keyPoint()
                + "\n\n" + "Источник: " + article.link();
    }
}
