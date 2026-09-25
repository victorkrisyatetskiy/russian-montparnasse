package com.russianmontparnasse.telegram;


public record TelegramMessageRequest(String chat_id, String text, String parse_mode){
}
