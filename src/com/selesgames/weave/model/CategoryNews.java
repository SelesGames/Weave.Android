package com.selesgames.weave.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CategoryNews {

    @JsonProperty("UserId")
    private String userId;

    @JsonProperty("FeedCount")
    private int feedCount;

    @JsonProperty("NewArticleCount")
    private int newArticleCount;

    @JsonProperty("UnreadArticleCount")
    private int unreadArticleCount;

    @JsonProperty("TotalArticleCount")
    private int totalArticleCount;

    @JsonProperty("Feeds")
    private List<Feed> feeds;

    @JsonProperty("News")
    private List<News> news;

    @JsonProperty("Page")
    private Page page;

    private String entryType;

    public String getUserId() {
        return userId;
    }

    public int getFeedCount() {
        return feedCount;
    }

    public int getNewArticleCount() {
        return newArticleCount;
    }

    public int getUnreadArticleCount() {
        return unreadArticleCount;
    }

    public int getTotalArticleCount() {
        return totalArticleCount;
    }

    public List<Feed> getFeeds() {
        return feeds;
    }

    public List<News> getNews() {
        return news;
    }

    public Page getPage() {
        return page;
    }

    public String getEntryType() {
        return entryType;
    }

}
