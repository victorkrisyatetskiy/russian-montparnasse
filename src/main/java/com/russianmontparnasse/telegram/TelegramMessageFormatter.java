package com.russianmontparnasse.telegram;

import com.russianmontparnasse.news.NewsArticle;
import com.russianmontparnasse.news.NewsCategory;
import com.russianmontparnasse.news.NewsSummary;
import org.springframework.stereotype.Component;

@Component
public class TelegramMessageFormatter {

    public String format(NewsArticle article) {
        return article.title() + "\n\n" + article.link();
    }

    public String format(NewsArticle article, NewsSummary summary) {
        return summary.title() + "\n\n" + summary.summary() + "\n\n" + summary.keyPoint()
                + "\n\n" + "Источник: " + article.link();
    }

    public String format(NewsArticle article, NewsSummary summary, NewsCategory category) {
        return summary.title() + "\n\n" + summary.summary() + "\n\n" + summary.keyPoint()
                + "\n\n" + categoryHashtag(category) + "\n\n" + "Источник: " + article.link();
    }

    private String categoryHashtag(NewsCategory category) {
        return switch (category) {
            case TAXES -> "#налоги";
            case SOCIAL_BENEFITS -> "#соцподдержка";
            case HEALTHCARE -> "#здоровье";
            case EMPLOYMENT -> "#работа";
            case HOUSING -> "#жилье";
            case FAMILY -> "#семья";
            case EDUCATION -> "#образование";
            case IMMIGRATION -> "#иммиграция";
            case ADMINISTRATION -> "#документы";
            case TRANSPORT -> "#транспорт";
            case FINANCE -> "#финансы";
            case CONSUMER_RIGHTS -> "#правапотребителей";
            case SECURITY -> "#безопасность";
            case OTHER -> "#другое";
        };
    }
}
