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
public class NewsRelevanceService {
    private final OpenAiClient openAiClient;
    private final Resource relevancePrompt;
    private final ObjectMapper objectMapper;

    private Map<String, Object> relevanceSchema(){
        return Map.of(
                "type", "object", "properties", Map.of(
                        "relevant", Map.of(
                        "type", "boolean"
                ), "category", Map.of(
                        "anyOf", List.of(
                                Map.of(
                                        "type", "string", "enum", List.of(
                                                "TAXES",
                                                "SOCIAL_BENEFITS",
                                                "HEALTHCARE",
                                                "EMPLOYMENT",
                                                "HOUSING",
                                                "FAMILY",
                                                "EDUCATION",
                                                "IMMIGRATION",
                                                "ADMINISTRATION",
                                                "TRANSPORT",
                                                "FINANCE",
                                                "CONSUMER_RIGHTS",
                                                "SECURITY",
                                                "OTHER"
                                        )
                                ), Map.of(
                                        "type", "null"
                                        )
                                )
                        ), "reason", Map.of("type", "string")
        ), "required", List.of(
                "relevant", "category","reason"),
                        "additionalProperties", false);
    }

    public NewsRelevanceService(@Value("classpath:prompts/news-relevance.txt")
                                Resource relevancePrompt, OpenAiClient openAiClient, ObjectMapper objectMapper){
        this.relevancePrompt = relevancePrompt;
        this.openAiClient = openAiClient;
        this.objectMapper = objectMapper;
    }

    public RelevanceResult evaluate(String articleText){
        String prompt = loadPrompt() + "\n\nARTICLE:\n" + articleText;
        String response = openAiClient.sendStructured(prompt, relevanceSchema());

        return objectMapper.readValue(response, RelevanceResult.class);
    }

    private String loadPrompt(){
        try {
            return relevancePrompt.getContentAsString(StandardCharsets.UTF_8);
        }catch (IOException e){
            throw new IllegalArgumentException(
                    "Failed to load news relevance prompt", e
            );
        }
    }
}
