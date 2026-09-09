package com.russianmontparnasse.openai;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled("Calls real OpenAI API")
@SpringBootTest
class OpenAiClientIntegrationTest {

    @Autowired
    private OpenAiClient openAiClient;

    @Test
    void shouldCallOpenAi(){
        String response = openAiClient.send("Reply with exactly: Ok");
        assertEquals("Ok", response);
    }
}
