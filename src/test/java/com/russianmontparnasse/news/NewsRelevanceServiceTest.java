package com.russianmontparnasse.news;

import com.russianmontparnasse.openai.OpenAiClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NewsRelevanceServiceTest {
    private final Resource relevancePrompt = mock(Resource.class);
    private final OpenAiClient openAiClient = mock(OpenAiClient.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final NewsRelevanceService relevanceService = new NewsRelevanceService(relevancePrompt, openAiClient, objectMapper);

    @Disabled
    @Test
    void shouldConsiderArticleRelevant(){
        String articleText = "Some article content";

        RelevanceResult result = relevanceService.evaluate(articleText);

        assertTrue(result.relevant());
    }

    @Test
    void shouldCallOpenAi() throws IOException {
        when(relevancePrompt.getContentAsString(StandardCharsets.UTF_8)).thenReturn("Classification prompt");

        when(openAiClient.sendStructured(eq("Classification prompt\n\nARTICLE:\nArticle text"), anyMap())).thenReturn("""
                {"relevant": true,
                "category": "TAXES",
                "reason": "Tax rules changed."
                }
                """);

        RelevanceResult result = relevanceService.evaluate("Article text");

        assertTrue(result.relevant());
        assertEquals(NewsCategory.TAXES, result.category());
        assertEquals("Tax rules changed.", result.reason());

        //assertEquals("OpenAI response", result.reason());

        verify(openAiClient).sendStructured(eq("Classification prompt\n\nARTICLE:\nArticle text"), anyMap());
    }

    @Test
    void shouldConsiderArticleIrrelevant() throws IOException{
        when(relevancePrompt.getContentAsString(StandardCharsets.UTF_8)).thenReturn("Classification prompt");

        when(openAiClient.sendStructured(eq("Classification prompt\n\nARTICLE:\nArticle text"),
                anyMap())).thenReturn("""
                {"relevant": false,
                "category": null,
                "reason": "The article has not particular impact"
                }
                """);

        RelevanceResult result = relevanceService.evaluate("Article text");

        assertFalse(result.relevant());
        assertNull(result.category());
        assertEquals("The article has not particular impact", result.reason());
    }
}
