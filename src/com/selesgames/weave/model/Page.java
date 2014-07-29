package com.selesgames.weave.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Page {

    @JsonProperty("Skip")
    private int skip;

    @JsonProperty("Take")
    private int take;

    @JsonProperty("IncludedArticleCount")
    private int includedArticleCount;

}
