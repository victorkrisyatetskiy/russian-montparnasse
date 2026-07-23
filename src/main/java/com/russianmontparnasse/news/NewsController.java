
package com.russianmontparnasse.news;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsQueryService newsQueryService;

    public NewsController(NewsQueryService newsQueryService) {
        this.newsQueryService = newsQueryService;
    }

    @GetMapping
    public List<NewsArticle> getAllNews() {
        return newsQueryService.getAllNews();
    }
}