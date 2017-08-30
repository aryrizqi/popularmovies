package com.example.rizqiaryansa.popularmovies_stage_2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by RizqiAryansa on 8/01/2017.
 */

public class ReviewModel implements Parcelable {

    @SerializedName("id")
    private String mId;
    @SerializedName("author")
    private String mAuthor;
    @SerializedName("content")
    private String mContent;
    @SerializedName("url")
    private String mUrl;

    /*=============== GET METHOD ========== */

    public String getId() {
        return mId;
    }

    public String getContent() {
        return mContent;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getUrl() {
        return mUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mContent);
        dest.writeString(mAuthor);
        dest.writeString(mUrl);
    }

    public static final Creator<ReviewModel> CREATOR = new Creator<ReviewModel>() {
        @Override
        public ReviewModel createFromParcel(Parcel source) {
            ReviewModel review = new ReviewModel();
            review.mId = source.readString();
            review.mAuthor = source.readString();
            review.mContent = source.readString();
            review.mUrl = source.readString();
            return review;
        }

        @Override
        public ReviewModel[] newArray(int size) {
            return new ReviewModel[size];
        }
    };

}
