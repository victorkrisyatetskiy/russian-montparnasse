package com.russianmontparnasse.news;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ArticleContentFetcherTest {
    private final ArticleContentFetcher fetcher = new ArticleContentFetcher();

    @Test
    void shouldFetchArticleContent(){
        String content = fetcher.fetch("https://www.service-public.fr/particuliers/actualites");

        assertNotNull(content);
        assertFalse(content.isBlank());
    }


}
