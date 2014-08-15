package com.selesgames.weave.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Page implements Parcelable {

    @JsonProperty("Skip")
    private int skip;

    @JsonProperty("Take")
    private int take;

    @JsonProperty("IncludedArticleCount")
    private int includedArticleCount;

    public Page() {

    }

    private Page(Parcel in) {
        skip = in.readInt();
        take = in.readInt();
        includedArticleCount = in.readInt();
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(skip);
        out.writeInt(take);
        out.writeInt(includedArticleCount);
    }

    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Page> CREATOR = new Parcelable.Creator<Page>() {
        public Page createFromParcel(Parcel in) {
            return new Page(in);
        }

        public Page[] newArray(int size) {
            return new Page[size];
        }
    };

    public int getSkip() {
        return skip;
    }

    public int getTake() {
        return take;
    }

    public int getIncludedArticleCount() {
        return includedArticleCount;
    }

}
