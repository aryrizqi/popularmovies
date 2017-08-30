package com.example.rizqiaryansa.popularmovies_stage_2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by RizqiAryansa on 8/01/2017.
 */

public class TrailerModel implements Parcelable {

    @SuppressWarnings("unused")
    public static final String LOG_TAG = TrailerModel.class.getSimpleName();

    @SerializedName("id")
    private String mId;
    @SerializedName("key")
    private String mKey;
    @SerializedName("name")
    private String mName;
    @SerializedName("site")
    private String mSite;
    @SerializedName("size")
    private String mSize;

    private String link_trailer = "http://www.youtube.com/watch?v=";

     /*=============== GET METHOD ========== */

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getTrailerUrl() {
        return link_trailer + mKey;
    }

    public String getKey() {
        return mKey;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mKey);
        dest.writeString(mName);
        dest.writeString(mSite);
        dest.writeString(mSize);
    }

    public static final Creator<TrailerModel> CREATOR = new Creator<TrailerModel>() {
        @Override
        public TrailerModel createFromParcel(Parcel source) {
            TrailerModel trailer = new TrailerModel();
            trailer.mId = source.readString();
            trailer.mKey = source.readString();
            trailer.mName = source.readString();
            trailer.mSite = source.readString();
            trailer.mSize = source.readString();
            return trailer;
        }

        @Override
        public TrailerModel[] newArray(int size) {
            return new TrailerModel[size];
        }
    };
}
