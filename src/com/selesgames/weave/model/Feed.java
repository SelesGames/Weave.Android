package com.selesgames.weave.model;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Feed implements Parcelable {

    @JsonProperty("Id")
    private String id;

    @JsonProperty("Uri")
    private String uri;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("IconUri")
    private String iconUri;

    @JsonProperty("Category")
    private String category;

    @JsonProperty("ArticleViewingType")
    private String articleViewingType;

    @JsonProperty("NewArticleCount")
    private int newArticleCount;

    @JsonProperty("UnreadArticleCount")
    private int unreadArticleCount;

    @JsonProperty("TotalArticleCount")
    private int totalArticleCount;

    @JsonProperty("TeaserImageUrl")
    private String teaserImageUrl;

    @JsonProperty("LastRefreshedOn")
    private Date lastRefreshedOn;

    @JsonProperty("MostRecentEntrance")
    private Date mostRecentEntrance;

    @JsonProperty("PreviousEntrance")
    private Date previousEntrance;

    public Feed() {

    }

    private Feed(Parcel in) {
        id = in.readString();
        uri = in.readString();
        name = in.readString();
        iconUri = in.readString();
        category = in.readString();
        articleViewingType = in.readString();
        newArticleCount = in.readInt();
        unreadArticleCount = in.readInt();
        totalArticleCount = in.readInt();
        teaserImageUrl = in.readString();
        lastRefreshedOn = new Date(in.readLong());
        mostRecentEntrance = new Date(in.readLong());
        previousEntrance = new Date(in.readLong());
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeString(uri);
        out.writeString(name);
        out.writeString(iconUri);
        out.writeString(category);
        out.writeString(articleViewingType);
        out.writeInt(newArticleCount);
        out.writeInt(unreadArticleCount);
        out.writeInt(totalArticleCount);
        out.writeString(teaserImageUrl);
        out.writeLong(lastRefreshedOn.getTime());
        out.writeLong(mostRecentEntrance.getTime());
        out.writeLong(previousEntrance.getTime());
    }

    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Feed> CREATOR = new Parcelable.Creator<Feed>() {
        public Feed createFromParcel(Parcel in) {
            return new Feed(in);
        }

        public Feed[] newArray(int size) {
            return new Feed[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getUri() {
        return uri;
    }

    public String getName() {
        return name;
    }

    public String getIconUri() {
        return iconUri;
    }

    public String getCategory() {
        return category;
    }

    public String getArticleViewingType() {
        return articleViewingType;
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

    public String getTeaserImageUrl() {
        return teaserImageUrl;
    }

    public Date getLastRefreshedOn() {
        return lastRefreshedOn;
    }

    public Date getMostRecentEntrance() {
        return mostRecentEntrance;
    }

    public Date getPreviousEntrance() {
        return previousEntrance;
    }

}
