package com.example.rizqiaryansa.popularmovies_stage_2.connection;

import android.os.AsyncTask;
import android.support.annotation.StringDef;
import android.util.Log;

import com.example.rizqiaryansa.popularmovies_stage_2.BuildConfig;
import com.example.rizqiaryansa.popularmovies_stage_2.OnTaskCompleted;
import com.example.rizqiaryansa.popularmovies_stage_2.model.MovieDBService;
import com.example.rizqiaryansa.popularmovies_stage_2.model.MovieModel;
import com.example.rizqiaryansa.popularmovies_stage_2.model.Movies;

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

public class FetchMovies extends AsyncTask<Void, Void, List<MovieModel>> {

    @SuppressWarnings("unused")
    public static String LOG_TAG = FetchMovies.class.getSimpleName();

    public final static String MOST_POPULAR = "popular";
    public final static String TOP_RATED = "top_rated";
    public final static String FAVORITE = "favorites";
    private String log_error = "A problem occurred talking to the movie db ";

    @StringDef({MOST_POPULAR, TOP_RATED, FAVORITE})
    public @interface SORT_BY {
    }

    private final NotifyAboutTaskCompletionCommand mCommand;
    private
    @SORT_BY
    String mSortBy = MOST_POPULAR;

    public interface Listener {
        void onFetchFinished(OnTaskCompleted onTaskCompleted);
    }


    public static class NotifyAboutTaskCompletionCommand implements OnTaskCompleted {
        private FetchMovies.Listener mListener;

        private List<MovieModel> mMovies;

        public NotifyAboutTaskCompletionCommand(FetchMovies.Listener listener) {
            mListener = listener;
        }

        @Override
        public void execute() {
            mListener.onFetchFinished(this);
        }

        public List<MovieModel> getMovies() {
            return mMovies;
        }
    }

    public FetchMovies(@SORT_BY String sortBy, NotifyAboutTaskCompletionCommand command) {
        mCommand = command;
        mSortBy = sortBy;
    }

    @Override
    protected void onPostExecute(List<MovieModel> movies) {
        if (movies != null) {
            mCommand.mMovies = movies;
        } else {
            mCommand.mMovies = new ArrayList<>();
        }
        mCommand.execute();
    }

    @Override
    protected List<MovieModel> doInBackground(Void... params) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieDBService service = retrofit.create(MovieDBService.class);
        Call<Movies> call = service.loadMovies(mSortBy,
                BuildConfig.MOVIE_API_KEY);
        try {
            Response<Movies> response = call.execute();
            Movies movies = response.body();
            return movies.getMovies();

        } catch (IOException e) {
            Log.e(LOG_TAG, log_error, e);
        }
        return null;
    }
}
