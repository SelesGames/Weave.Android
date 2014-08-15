package com.selesgames.weave.model;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CategoryNews implements Parcelable {

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
    
    public CategoryNews() {

    }

    private CategoryNews(Parcel in) {
        userId = in.readString();
        feedCount = in.readInt();
        newArticleCount = in.readInt();
        unreadArticleCount = in.readInt();
        totalArticleCount = in.readInt();
        feeds = new ArrayList<Feed>();
        in.readList(feeds, Feed.class.getClassLoader());
        news = new ArrayList<News>();
        in.readList(news, News.class.getClassLoader());
        page = in.readParcelable(Page.class.getClassLoader());
        entryType = in.readString();
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(userId);
        out.writeInt(feedCount);
        out.writeInt(newArticleCount);
        out.writeInt(unreadArticleCount);
        out.writeInt(totalArticleCount);
        out.writeList(feeds);
        out.writeList(news);
        out.writeParcelable(page, flags);
        out.writeString(entryType);
    }

    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<CategoryNews> CREATOR = new Parcelable.Creator<CategoryNews>() {
        public CategoryNews createFromParcel(Parcel in) {
            return new CategoryNews(in);
        }

        public CategoryNews[] newArray(int size) {
            return new CategoryNews[size];
        }
    };

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
