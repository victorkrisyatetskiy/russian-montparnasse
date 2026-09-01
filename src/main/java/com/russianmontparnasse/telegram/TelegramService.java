package com.russianmontparnasse.telegram;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class TelegramService {

    private final RestClient restClient = RestClient.create();

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.channel.id}")
    private String channelId;




    public void sendMessage(String text){
        String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";

        restClient.post().uri(url).body(new TelegramMessageRequest(channelId, text)).retrieve().toBodilessEntity();
    }
}
