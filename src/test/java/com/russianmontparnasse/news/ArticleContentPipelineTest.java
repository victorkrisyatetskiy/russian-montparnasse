package com.russianmontparnasse.news;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ArticleContentPipelineTest {

    private final ArticleContentFetcher fetcher = new ArticleContentFetcher();
    private final ArticleTextExtractor extractor = new ArticleTextExtractor();

    @Test
    void shouldFetchAndExtractArticleText(){
        String html = fetcher.fetch("https://www.service-public.gouv.fr/particuliers/actualites/A17675?xtor=RSS-111");

        String text = extractor.extract(html);
        System.out.println(text);

        assertNotNull(text);
        assertFalse(text.isBlank());

    }
}
