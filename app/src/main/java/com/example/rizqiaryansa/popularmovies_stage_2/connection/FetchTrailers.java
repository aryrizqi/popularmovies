package com.example.rizqiaryansa.popularmovies_stage_2.connection;

import android.os.AsyncTask;
import android.util.Log;

import com.example.rizqiaryansa.popularmovies_stage_2.BuildConfig;
import com.example.rizqiaryansa.popularmovies_stage_2.model.MovieDBService;
import com.example.rizqiaryansa.popularmovies_stage_2.model.TrailerModel;
import com.example.rizqiaryansa.popularmovies_stage_2.model.Trailers;

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

public class FetchTrailers extends AsyncTask<Long, Void, List<TrailerModel>> {

    @SuppressWarnings("unused")
    public static String LOG_TAG = FetchTrailers.class.getSimpleName();
    private final Listener mListener;
    private String log_error = "A problem occurred talking to the movie db ";

    public interface Listener {
        void onTrailerFetchFinished(List<TrailerModel> trailers);
    }

    public FetchTrailers(Listener listener) {
        mListener = listener;
    }

    @Override
    protected List<TrailerModel> doInBackground(Long... params) {

        if (params.length == 0) {
            return null;
        }
        long movieId = params[0];

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieDBService service = retrofit.create(MovieDBService.class);
        Call<Trailers> call = service.loadTrailers(movieId,
                BuildConfig.MOVIE_API_KEY);
        try {
            Response<Trailers> response = call.execute();
            Trailers trailers = response.body();
            return trailers.getTrailers();
        } catch (IOException e) {
            Log.e(LOG_TAG, log_error, e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<TrailerModel> trailers) {
        if (trailers != null) {
            mListener.onTrailerFetchFinished(trailers);
        } else {
            mListener.onTrailerFetchFinished(new ArrayList<TrailerModel>());
        }
    }
}