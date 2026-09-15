package com.russianmontparnasse.news;


import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "news.import.runner.enabled=false",
        "spring.task.scheduling.enabled=false"
})

class NewsSummaryServiceIntegrationTest {

    @Autowired
    private NewsSummaryService newsSummaryService;

    @Test
    @Disabled("Calls real OpenAI API")
    void shouldGenerateRussianNewsSummary() {
        String articleText = """
                La date limite pour effectuer votre déclaration de revenus
                                est fixée au 20 mai 2026.
                
                                Les contribuables qui déclarent leurs revenus en ligne
                                disposent d'un délai supplémentaire selon leur département
                                de résidence.
                
                                Aucun document supplémentaire n'est nécessaire pour les
                                personnes qui effectuent leur déclaration en ligne.
                """;

        NewsSummary result = newsSummaryService.summarize(articleText);

        System.out.println(result);
    }
}
