package com.russianmontparnasse.news;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NewsController.class)
class NewsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NewsQueryService newsQueryService;

    @Test
    void testGetAllNews() throws Exception {
        NewsArticle article1 = new NewsArticle("Title 1", "https://example.com/1", "2023-10-01T10:00:00");
        NewsArticle article2 = new NewsArticle("Title 2", "https://example.com/2", "2023-10-02T12:30:00");

        List<NewsArticle> mockNewsArticles = List.of(article1, article2);

        when(newsQueryService.getAllNews()).thenReturn(mockNewsArticles);

        mockMvc.perform(get("/api/news")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("Title 1")))
                .andExpect(jsonPath("$[0].link", is("https://example.com/1")))
                .andExpect(jsonPath("$[0].publishedDate", is("2023-10-01T10:00:00")))
                .andExpect(jsonPath("$[1].title", is("Title 2")))
                .andExpect(jsonPath("$[1].link", is("https://example.com/2")))
                .andExpect(jsonPath("$[1].publishedDate", is("2023-10-02T12:30:00")));

        verify(newsQueryService, times(1)).getAllNews();
    }
}