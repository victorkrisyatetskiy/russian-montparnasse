package com.russianmontparnasse.news;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArticleTextExtractorTest {
    private final ArticleTextExtractor extractor = new ArticleTextExtractor();

    @Test
    void shouldExtractTextFromHtml() {
        String html = """
                <html>
                    <body>
                        <h1>Test title</h1>
                        <p>Test article text.</p>
                    </body>
                </html>
                """;

        String result = extractor.extract(html);

        assertEquals("Test title Test article text.", result);
    }
}
