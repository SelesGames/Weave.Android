package com.selesgames.weave.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Parcelable {

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

    public User() {

    }

    private User(Parcel in) {
        id = in.readString();
        feeds = new ArrayList<Feed>();
        in.readTypedList(feeds, Feed.CREATOR);
        previousLoginTime = new Date(in.readLong());
        currentLoginTime = new Date(in.readLong());
        latestNews = new ArrayList<News>();
        in.readTypedList(latestNews, News.CREATOR);
        articleDeletionTimeForMarkedRead = in.readString();
        articleDeletionTimeForUnread = in.readString();
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeTypedList(feeds);
        out.writeLong(previousLoginTime.getTime());
        out.writeLong(currentLoginTime.getTime());
        out.writeTypedList(latestNews);
        out.writeString(articleDeletionTimeForMarkedRead);
        out.writeString(articleDeletionTimeForUnread);
    }

    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

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
