package com.russianmontparnasse.news;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ArticleTextExtractorTest {
    private final ArticleTextExtractor extractor = new ArticleTextExtractor();

    @Test
    void shouldExtractTextFromHtml() {
        String html = """
                <html>
                    <body>
                        <div class="article">
                
                            <div class="fr-mb-3w sp-no-print rs_skip">
                                Ajouter à mes alertes
                            </div>
                
                            <p class="fr-tag fr-mb-3w">
                                Aides aux familles
                            </p>
                
                            <h1 id="titlePage">
                                Comment connaître votre quotient familial ?
                            </h1>
                
                            <p class="sp-text--gray fr-text--xs">
                                Publié le 01 septembre 2026
                            </p>
                
                            <div id="intro" class="fr-mb-6w">
                                <p class="fr-text--lg">
                                    Introduction de l'article.
                                </p>
                            </div>
                
                            <div class="fr-my-4w">
                                <div class="sp-actu">
                                    Contenu principal de l'article.
                                </div>
                
                                <div class="fr-mt-6w">
                                    Voir aussi
                                </div>
                
                                <div class="fr-mt-6w">
                                    Agenda
                                </div>
                
                                <a class="fr-tag">
                                    Une remarque ?
                                </a>
                            </div>
                
                        </div>
                    </body>
                </html>
                """;

        String result = extractor.extract(html);

        assertTrue(result.contains("Aides aux familles"));
        assertTrue(result.contains("Comment connaître votre quotient familial ?"));
        assertTrue(result.contains("Publié le 01 septembre 2026"));
        assertTrue(result.contains("Introduction de l'article."));
        assertTrue(result.contains("Contenu principal de l'article."));

        assertFalse(result.contains("Ajouter à mes alertes"));
        assertFalse(result.contains("Voir aussi"));
        assertFalse(result.contains("Agenda"));
        assertFalse(result.contains("Une remarque ?"));
    }
}
