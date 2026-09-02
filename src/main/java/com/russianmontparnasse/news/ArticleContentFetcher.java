package com.russianmontparnasse.news;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ArticleContentFetcher {
    private final RestClient restClient = RestClient.create();

    public String fetch(String url){
        return restClient.get().uri(url).retrieve().body(String.class);
    }
}
