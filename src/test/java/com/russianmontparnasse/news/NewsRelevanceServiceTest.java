package com.russianmontparnasse.news;

import com.russianmontparnasse.openai.OpenAiClient;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NewsRelevanceServiceTest {
    private final Resource relevancePrompt = mock(Resource.class);
    private final OpenAiClient openAiClient = mock(OpenAiClient.class);
    private final NewsRelevanceService relevanceService = new NewsRelevanceService(relevancePrompt, openAiClient);

    @Test
    void shouldConsiderArticleRelevant(){
        String articleText = "Some article content";

        RelevanceResult result = relevanceService.evaluate(articleText);

        assertTrue(result.relevant());
    }

    void shouldCallOpenAi() throws IOException {
        when(relevancePrompt.getContentAsString(StandardCharsets.UTF_8)).thenReturn("Classification prompt");

        when(openAiClient.sendStructured(eq("Classification prompt\n\nARTICLE:\\Article text"), anyMap())).thenReturn("""
                {"relevant": true,
                "category": "TAXES",
                "reason": "Tax rules changed."
                """);

        RelevanceResult result = relevanceService.evaluate("Article text");

        assertEquals("OpenAI response", result.reason());

        verify(openAiClient).sendStructured(eq("Classification prompt\n\nARTICLE:\\Article text"), anyMap());
    }
}
