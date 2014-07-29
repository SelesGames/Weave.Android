package com.selesgames.weave.model;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class News implements Parcelable {

    @JsonProperty("Id")
    private String id;

    @JsonProperty("FeedId")
    private String feedId;

    @JsonProperty("Title")
    private String title;

    @JsonProperty("Link")
    private String link;

    // @JsonProperty("UtcPublishDateTime")
    // private Date utcPublishDateTime;

    @JsonProperty("ImageUrl")
    private String imageUrl;

    @JsonProperty("IsNew")
    private boolean isNew;

    @JsonProperty("IsFavorite")
    private boolean isFavorite;

    @JsonProperty("HasBeenViewed")
    private boolean hasBeenViewed;

    @JsonProperty("OriginalDownloadDateTime")
    private Date originalDownloadDateTime;

    public News() {

    }

    private News(Parcel in) {
        id = in.readString();
        feedId = in.readString();
        title = in.readString();
        link = in.readString();
        imageUrl = in.readString();
        isNew = in.readByte() == 1;
        isFavorite = in.readByte() == 1;
        hasBeenViewed = in.readByte() == 1;
        originalDownloadDateTime = new Date(in.readLong());
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeString(feedId);
        out.writeString(title);
        out.writeString(link);
        out.writeString(imageUrl);
        out.writeByte((byte) (isNew ? 1 : 0));
        out.writeByte((byte) (isFavorite ? 1 : 0));
        out.writeByte((byte) (hasBeenViewed ? 1 : 0));
        out.writeLong(originalDownloadDateTime.getTime());
    }

    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<News> CREATOR = new Parcelable.Creator<News>() {
        public News createFromParcel(Parcel in) {
            return new News(in);
        }

        public News[] newArray(int size) {
            return new News[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getFeedId() {
        return feedId;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean getIsNew() {
        return isNew;
    }

    public boolean getIsFavorite() {
        return isFavorite;
    }

    public boolean isHasBeenViewed() {
        return hasBeenViewed;
    }

    public Date getOriginalDownloadDateTime() {
        return originalDownloadDateTime;
    }

}
