package com.example.rizqiaryansa.popularmovies_stage_2.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.rizqiaryansa.popularmovies_stage_2.R;
import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by RizqiAryansa on 8/01/2017.
 */

public class MovieModel implements Parcelable {

    public static final String LOG_TAG = MovieModel.class.getSimpleName();
    public static final float POSTER_ASPECT_RATIO = 1.5f;

    @SerializedName("id")
    private long mId;
    @SerializedName("original_title")
    private String mTitle;
    @SerializedName("poster_path")
    private String mPoster;
    @SerializedName("overview")
    private String mOverview;
    @SerializedName("vote_average")
    private String mUserRating;
    @SerializedName("release_date")
    private String mReleaseDate;
    @SerializedName("backdrop_path")
    private String mBackdrop;

    private String log_error = "The Release data was not parsed successfully: ";

    private MovieModel() {
    }

    public MovieModel(long id, String title, String poster, String overview, String userRating,
                 String releaseDate, String backdrop) {
        this.mId = id;
        this.mTitle = title;
        this.mPoster = poster;
        this.mOverview = overview;
        this.mUserRating = userRating;
        this.mReleaseDate = releaseDate;
        this.mBackdrop = backdrop;
    }

    /*====== GET METHOD ======= */

    @Nullable
    public String getTitle() {
        return mTitle;
    }

    public long getId() {
        return mId;
    }

    @Nullable
    public String getPosterUrl(Context context) {
        if (mPoster != null && !mPoster.isEmpty()) {
            return context.getResources().getString(R.string.url_for_downloading_poster) + mPoster;
        }

        return null;
    }

    public String getPoster() {
        return mPoster;
    }

    public String getReleaseDate(Context context) {
        String inputPattern = "yyyy-MM-dd";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, Locale.US);
        if (mReleaseDate != null && !mReleaseDate.isEmpty()) {
            try {
                Date date = inputFormat.parse(mReleaseDate);
                return DateFormat.getDateInstance().format(date);
            } catch (ParseException e) {
                Log.e(LOG_TAG, log_error + mReleaseDate);
            }
        } else {
            mReleaseDate = context.getString(R.string.release_date_missing);
        }

        return mReleaseDate;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    @Nullable
    public String getOverview() {
        return mOverview;
    }

    @Nullable
    public String getUserRating() {
        return mUserRating;
    }

    @Nullable
    public String getBackdropUrl(Context context) {
        if (mBackdrop != null && !mBackdrop.isEmpty()) {
            return context.getResources().getString(R.string.url_for_downloading_backdrop) +
                    mBackdrop;
        }

        return null;
    }

    public String getBackdrop() {
        return mBackdrop;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<MovieModel> CREATOR = new Creator<MovieModel>() {
        public MovieModel createFromParcel(Parcel source) {
            MovieModel movie = new MovieModel();
            movie.mId = source.readLong();
            movie.mTitle = source.readString();
            movie.mPoster = source.readString();
            movie.mOverview = source.readString();
            movie.mUserRating = source.readString();
            movie.mReleaseDate = source.readString();
            movie.mBackdrop = source.readString();
            return movie;
        }

        public MovieModel[] newArray(int size) {
            return new MovieModel[size];
        }
    };


    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(mId);
        parcel.writeString(mTitle);
        parcel.writeString(mPoster);
        parcel.writeString(mOverview);
        parcel.writeString(mUserRating);
        parcel.writeString(mReleaseDate);
        parcel.writeString(mBackdrop);
    }
}
