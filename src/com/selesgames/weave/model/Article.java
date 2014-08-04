package com.selesgames.weave.model;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Article implements Parcelable {

    private String author;

    private String content;

    @JsonProperty("date_published")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date datePublished;

    private String domain;

    @JsonProperty("lead_image_url")
    private String leadImageUrl;

    private String title;

    private String url;

    @JsonProperty("word_count")
    private int wordCount;

    public Article() {

    }

    private Article(Parcel in) {
        author = in.readString();
        content = in.readString();
        datePublished = new Date(in.readLong());
        domain = in.readString();
        leadImageUrl = in.readString();
        title = in.readString();
        url = in.readString();
        wordCount = in.readInt();
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(author);
        out.writeString(content);
        out.writeLong(datePublished.getTime());
        out.writeString(domain);
        out.writeString(leadImageUrl);
        out.writeString(title);
        out.writeString(url);
        out.writeInt(wordCount);
    }

    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Article> CREATOR = new Parcelable.Creator<Article>() {
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        public Article[] newArray(int size) {
            return new Article[size];
        }
    };

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public Date getDatePublished() {
        return datePublished;
    }

    public String getDomain() {
        return domain;
    }

    public String getLeadImageUrl() {
        return leadImageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public int getWordCount() {
        return wordCount;
    }

}
