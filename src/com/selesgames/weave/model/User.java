package com.selesgames.weave.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    @JsonProperty("Id")
    private String id;

    @JsonProperty("Feeds")
    private List<Feed> feeds;

    @JsonProperty("PreviousLoginTime")
    private Date previousLoginTime;

    @JsonProperty("CurrentLoginTime")
    private Date currentLoginTime;

    @JsonProperty("LatestNews")
    private List<News> latestNews;

    @JsonProperty("ArticleDeletionTimeForMarkedRead")
    private String articleDeletionTimeForMarkedRead;

    @JsonProperty("ArticleDeletionTimeForUnread")
    private String articleDeletionTimeForUnread;

    public String getId() {
        return id;
    }

    public List<Feed> getFeeds() {
        return feeds;
    }

    public Date getPreviousLoginTime() {
        return previousLoginTime;
    }

    public Date getCurrentLoginTime() {
        return currentLoginTime;
    }

    public List<News> getLatestNews() {
        return latestNews;
    }

    public String getArticleDeletionTimeForMarkedRead() {
        return articleDeletionTimeForMarkedRead;
    }

    public String getArticleDeletionTimeForUnread() {
        return articleDeletionTimeForUnread;
    }

}
