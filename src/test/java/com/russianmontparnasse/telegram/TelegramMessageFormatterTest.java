package com.russianmontparnasse.telegram;

import com.russianmontparnasse.news.NewsArticle;
import com.russianmontparnasse.news.NewsCategory;
import com.russianmontparnasse.news.NewsSummary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TelegramMessageFormatterTest {
    private final TelegramMessageFormatter formatter = new TelegramMessageFormatter();



    @Test
    void shouldFormatRussianNewsSummary(){
        NewsArticle article = new NewsArticle(
                "French title",
                "https://example.com/article",
                "2029-09-15"
        );

        NewsSummary summary = new NewsSummary(
                "Крайний срок подачи декларации — 20 мая 2026 года",
                "Декларацию необходимо подать до 20 мая 2026 года.",
                "Проверьте срок для своего департамента."
        );

        String result = formatter.format(article, summary, NewsCategory.TAXES);

        assertEquals("""
            Крайний срок подачи декларации — 20 мая 2026 года

            Декларацию необходимо подать до 20 мая 2026 года.

            Проверьте срок для своего департамента.
            
            #налоги

            Источник: https://example.com/article""", result);
    }

    @ParameterizedTest
    @CsvSource({
            "TAXES, #налоги",
            "SOCIAL_BENEFITS, #соцподдержка",
            "HEALTHCARE, #здоровье",
            "EMPLOYMENT, #работа",
            "HOUSING, #жилье",
            "FAMILY, #семья",
            "EDUCATION, #образование",
            "IMMIGRATION, #иммиграция",
            "ADMINISTRATION, #документы",
            "TRANSPORT, #транспорт",
            "FINANCE, #финансы",
            "CONSUMER_RIGHTS, #правапотребителей",
            "SECURITY, #безопасность",
            "OTHER, #другое"
    })

    void shouldAddHashTegForCategory(NewsCategory category, String expectedHashTag){
        NewsArticle article = new NewsArticle(
                "French title",
                "https://example.com/article",
                "2026-09-16"
        );

        NewsSummary summary = new NewsSummary(
                "Заголовок",
                "Описание",
                "Главное"
        );

        String result = formatter.format(article, summary, category);
        assertTrue(result.contains(expectedHashTag));
    }
}
