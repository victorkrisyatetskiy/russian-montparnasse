package com.russianmontparnasse.telegram;

import com.russianmontparnasse.news.NewsArticle;
import com.russianmontparnasse.news.NewsCategory;
import com.russianmontparnasse.news.NewsSummary;
import org.springframework.stereotype.Component;

@Component
public class TelegramMessageFormatter {


    public String format(NewsArticle article, NewsSummary summary, NewsCategory category) {
        return "<b>" + escapeHtml(summary.title()) + "</b>"
                + "\n\n<b>Главное:</b> " + escapeHtml(summary.keyPoint())
                + "\n\n<b>Подробности:</b>\n" + escapeHtml(summary.summary())
                + "\n\n" + categoryHashtag(category)
                + "\n\nИсточник: " + escapeHtml(article.link());
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

    private String escapeHtml(String text){
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
