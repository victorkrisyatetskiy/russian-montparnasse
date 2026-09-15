package com.russianmontparnasse.openai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class OpenAiClient {
    private final RestClient restClient;
    private final String model;

    public String sendStructured(String input, Map<String, Object> schema) {
        Map<String, Object> request = Map.of(
                "model", model,
                "input", input,
                "text", Map.of(
                        "format", Map.of(
                                "type", "json_schema",
                                "name", "relevance_result",
                                "strict", true,
                                "schema", schema
                        )
                )
        );

        OpenAiResponse response = restClient.post()
                .uri("https://api.openai.com/v1/responses")
                .body(request)
                .retrieve()
                .body(OpenAiResponse.class);

        return extractText(response);
    }

    public OpenAiClient(@Value("${openai.api.key}") String apiKey,
                        @Value("${openai.model}") String model) {
        this.model = model;
        this.restClient = RestClient.builder()
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    public String send(String input) {
        Map<String, Object> request = Map.of(
                "model", model,
                "input", input
        );

        OpenAiResponse response = restClient.post()
                .uri("https://api.openai.com/v1/responses")
                .body(request)
                .retrieve()
                .body(OpenAiResponse.class);

        return extractText(response);
    }

    private String extractText(OpenAiResponse response) {
        return response.output().stream().filter(output -> output.content() != null)
                .flatMap(output -> output.content().stream())
                .map(OpenAiResponse.Content::text)
                .filter(text -> text != null && !text.isBlank())
                .findFirst().orElseThrow(() -> new IllegalStateException(
                        "OpenAI response contains no output text"
                ));
    }
}
