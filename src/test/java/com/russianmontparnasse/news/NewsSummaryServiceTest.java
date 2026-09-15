package com.russianmontparnasse.news;

import com.russianmontparnasse.openai.OpenAiClient;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NewsSummaryServiceTest {

    private final Resource summaryPrompt = mock(Resource.class);
    private final OpenAiClient openAiClient = mock(OpenAiClient.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldCreateNewsSummary() throws Exception {
        when(summaryPrompt.getContentAsString(StandardCharsets.UTF_8)).thenReturn("Summary prompt");

        when(openAiClient.sendStructured(eq("Summary prompt\n\nARTICLE:\nArticle text"), anyMap()))
                .thenReturn("""
                        {
                        "title": "Новые сроки подачи декларации",
                        "summary": "Во Франции изменились сроки подачи декларации.",
                        "keyPoint": "Проверьте сроки для своего департамента."
                        }
                        """);

        NewsSummaryService service = new NewsSummaryService(
                summaryPrompt,
                openAiClient,
                objectMapper
        );

        NewsSummary result = service.summarize("Article text");

        assertEquals("Новые сроки подачи декларации", result.title());

        assertEquals("Во Франции изменились сроки подачи декларации.", result.summary());

        assertEquals("Проверьте сроки для своего департамента.", result.keyPoint());

    }


}
