package com.selesgames.weave.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfo {

    @JsonProperty("UserId")
    private String userId;

    public String getUserId() {
        return userId;
    }

}
