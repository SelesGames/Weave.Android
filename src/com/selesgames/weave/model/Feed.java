package com.selesgames.weave.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Feed {
    
    @JsonProperty("Id")
    private String id;

    @JsonProperty("Uri")
    private String uri;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Category")
    private String category;

    @JsonProperty("ArticleViewingType")
    private String articleViewingType;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getArticleViewingType() {
        return articleViewingType;
    }

    public void setArticleViewingType(String articleViewingType) {
        this.articleViewingType = articleViewingType;
    }

}
