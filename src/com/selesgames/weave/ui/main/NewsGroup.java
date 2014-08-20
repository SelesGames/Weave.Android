package com.selesgames.weave.ui.main;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.selesgames.weave.model.Feed;
import com.selesgames.weave.model.News;

public class NewsGroup implements Parcelable {

    public static class NewsItem implements Parcelable {

        public News news;

        public Feed feed;

        private NewsItem(News news, Feed feed) {
            this.news = news;
            this.feed = feed;
        }
        
        private NewsItem(Parcel in) {
            news = in.readParcelable(News.class.getClassLoader());
            feed = in.readParcelable(Feed.class.getClassLoader());
        }

        public void writeToParcel(Parcel out, int flags) {
            out.writeParcelable(news, flags);
            out.writeParcelable(feed, flags);
        }

        public int describeContents() {
            return 0;
        }

        public static final Parcelable.Creator<NewsItem> CREATOR = new Parcelable.Creator<NewsItem>() {
            public NewsItem createFromParcel(Parcel in) {
                return new NewsItem(in);
            }

            public NewsItem[] newArray(int size) {
                return new NewsItem[size];
            }
        };
    }

    private List<NewsItem> mNews;
    
    public NewsGroup() {
        mNews = new ArrayList<NewsItem>();
    }

    private NewsGroup(Parcel in) {
        mNews = new ArrayList<NewsItem>();
        in.readList(mNews, NewsItem.class.getClassLoader());
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeList(mNews);
    }

    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<NewsGroup> CREATOR = new Parcelable.Creator<NewsGroup>() {
        public NewsGroup createFromParcel(Parcel in) {
            return new NewsGroup(in);
        }

        public NewsGroup[] newArray(int size) {
            return new NewsGroup[size];
        }
    };

    public void addNews(News news, Feed feed) {
        mNews.add(new NewsItem(news, feed));
    }

    public List<NewsItem> getNews() {
        return mNews;
    }

}
