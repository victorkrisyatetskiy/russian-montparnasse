package com.russianmontparnasse.news;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "news.import.runner.enabled=false",
        "spring.task.scheduling.enabled=false"
})
public class NewsRelevanceServiceIntegrationTest {

    @Autowired
    private NewsRelevanceService newsRelevanceService;

    @Test
    void shouldClassifyRealArticle() {
        String articleText = """
                  Le musée organise une nouvelle exposition consacrée
                                                  à l'histoire de la peinture française.
                
                                                  L'exposition sera ouverte au public du 15 octobre
                                                  au 20 décembre à Paris.
                
                                                  Plusieurs conférences et visites guidées sont également prévues.
                """;

        RelevanceResult result = newsRelevanceService.evaluate(articleText);

        System.out.println(result);
    }
}
