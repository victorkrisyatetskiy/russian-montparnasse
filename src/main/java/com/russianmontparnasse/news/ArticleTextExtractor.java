package com.russianmontparnasse.news;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;


@Component
public class ArticleTextExtractor {

    public String extract(String html) {
        Document document = Jsoup.parse(html);

        Element article = document.selectFirst(".article");

        if (article == null) {
            return document.text();
        }

        StringBuilder text = new StringBuilder();

        appendText(text, article.selectFirst("p.fr-tag"));
        appendText(text, article.selectFirst("h1#titlePage"));
        appendText(text, article.selectFirst("p.sp-text--gray"));
        appendText(text, article.selectFirst("#intro"));
        appendText(text, article.selectFirst("div.sp-actu"));


        return text.toString().trim();
    }

    private void appendText(StringBuilder text, Element element) {
        if (element != null) {
            text.append(element.text()).append("\n\n");
        }
    }
}
