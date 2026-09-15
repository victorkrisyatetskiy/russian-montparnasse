package com.russianmontparnasse.news;

import com.russianmontparnasse.openai.OpenAiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
public class NewsSummaryService {

    private final OpenAiClient openAiClient;
    private final Resource summaryPrompt;
    private final ObjectMapper objectMapper;

    public NewsSummaryService(@Value("classpath:prompts/news-summary.txt") Resource summaryPrompt, OpenAiClient openAiClient, ObjectMapper objectMapper) {
        this.summaryPrompt = summaryPrompt;
        this.openAiClient = openAiClient;
        this.objectMapper = objectMapper;
    }

    public NewsSummary summarize(String articleText) {
        String prompt = loadPrompt() + "\n\nARTICLE:\n" + articleText;

        String response = openAiClient.sendStructured(prompt, summarySchema());
        return objectMapper.readValue(response, NewsSummary.class);
    }

    private String loadPrompt() {
        try {
            return summaryPrompt.getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to load news summary prompt", e);
        }
    }

    private Map<String, Object> summarySchema() {
        return Map.of("type", "object", "properties", Map.of(
                        "title", Map.of(
                                "type", "string"),
                        "summary", Map.of(
                                "type", "string"
                        ),
                        "keyPoint", Map.of(
                                "type", "string"
                        )
                ), "required", List.of(
                        "title",
                        "summary",
                        "keyPoint"
                ),
                "additionalProperties", false);
    }
}
