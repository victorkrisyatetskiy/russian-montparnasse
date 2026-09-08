package com.russianmontparnasse.news;

public record RelevanceResult(boolean relevant, NewsCategory category, String reason) {
}
