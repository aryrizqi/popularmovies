package com.example.rizqiaryansa.popularmovies_stage_2.connection;

import android.os.AsyncTask;
import android.util.Log;

import com.example.rizqiaryansa.popularmovies_stage_2.BuildConfig;
import com.example.rizqiaryansa.popularmovies_stage_2.model.MovieDBService;
import com.example.rizqiaryansa.popularmovies_stage_2.model.ReviewModel;
import com.example.rizqiaryansa.popularmovies_stage_2.model.Reviews;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by RizqiAryansa on 8/01/2017.
 */

public class FetchReviews extends AsyncTask<Long, Void, List<ReviewModel>> {

    @SuppressWarnings("unused")
    public static String LOG_TAG = FetchReviews.class.getSimpleName();
    private final Listener mListener;
    private String log_error = "A problem occurred talking to the movie db ";

    public interface Listener {
        void onReviewsFetchFinished(List<ReviewModel> reviews);
    }

    public FetchReviews(Listener listener) {
        mListener = listener;
    }

    @Override
    protected List<ReviewModel> doInBackground(Long... params) {

        if (params.length == 0) {
            return null;
        }
        long movieId = params[0];

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieDBService service = retrofit.create(MovieDBService.class);
        Call<Reviews> call = service.loadReviews(movieId,
                BuildConfig.MOVIE_API_KEY);
        try {
            Response<Reviews> response = call.execute();
            Reviews reviews = response.body();
            return reviews.getReviews();
        } catch (IOException e) {
            Log.e(LOG_TAG, log_error, e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<ReviewModel> reviews) {
        if (reviews != null) {
            mListener.onReviewsFetchFinished(reviews);
        } else {
            mListener.onReviewsFetchFinished(new ArrayList<ReviewModel>());
        }
    }
}
