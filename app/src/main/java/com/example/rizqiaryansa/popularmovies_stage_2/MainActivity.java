package com.example.rizqiaryansa.popularmovies_stage_2;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.Toast;

import com.example.rizqiaryansa.popularmovies_stage_2.adapter.MovieAdapter;
import com.example.rizqiaryansa.popularmovies_stage_2.connection.FetchMovies;
import com.example.rizqiaryansa.popularmovies_stage_2.database.MoviesContract;
import com.example.rizqiaryansa.popularmovies_stage_2.model.MovieModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        FetchMovies.Listener, MovieAdapter.Callbacks {

    private static final String EXTRA_MOVIES = "EXTRA_MOVIES";
    private static final String EXTRA_SORT_BY = "EXTRA_SORT_BY";
    private static final int FAVORITE_MOVIES_LOADER = 0;

    private boolean mTwoPane;
    private RetainedFragment mRetainedFragment;
    private MovieAdapter mAdapter;
    private String mSortBy = FetchMovies.MOST_POPULAR;

    @Bind(R.id.movie_list)
    RecyclerView mRecyclerView;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mToolbar.setTitle(R.string.title_movies);
        setSupportActionBar(mToolbar);

        String tag = RetainedFragment.class.getName();
        this.mRetainedFragment = (RetainedFragment) getSupportFragmentManager().findFragmentByTag(tag);
        if (this.mRetainedFragment == null) {
            this.mRetainedFragment = new RetainedFragment();
            getSupportFragmentManager().beginTransaction().add(this.mRetainedFragment, tag).commit();
        }

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, getResources()
                .getInteger(R.integer.grid_number_cols)));

        mAdapter = new MovieAdapter(new ArrayList<MovieModel>(), this);
        mRecyclerView.setAdapter(mAdapter);

        mTwoPane = findViewById(R.id.movie_detail_container) != null;

        if (savedInstanceState != null) {
            mSortBy = savedInstanceState.getString(EXTRA_SORT_BY);
            if (savedInstanceState.containsKey(EXTRA_MOVIES)) {
                List<MovieModel> movies = savedInstanceState.getParcelableArrayList(EXTRA_MOVIES);
                mAdapter.add(movies);
                findViewById(R.id.progressBar).setVisibility(View.GONE);

                if (mSortBy.equals(FetchMovies.FAVORITE)) {
                    getSupportLoaderManager().initLoader(FAVORITE_MOVIES_LOADER, null, this);
                }
            }
            updateEmptyState();
        } else {
            fetchMovies(mSortBy);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<MovieModel> movies = mAdapter.getMovies();
        if (movies != null && !movies.isEmpty()) {
            outState.putParcelableArrayList(EXTRA_MOVIES, movies);
        }
        outState.putString(EXTRA_SORT_BY, mSortBy);

        if (!mSortBy.equals(FetchMovies.FAVORITE)) {
            getSupportLoaderManager().destroyLoader(FAVORITE_MOVIES_LOADER);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        switch (mSortBy) {
            case FetchMovies.MOST_POPULAR:
                menu.findItem(R.id.sort_by_popularity).setChecked(true);
                break;
            case FetchMovies.TOP_RATED:
                menu.findItem(R.id.sort_by_top_rated).setChecked(true);
                break;
            case FetchMovies.FAVORITE:
                menu.findItem(R.id.sort_by_favourite).setChecked(true);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_by_top_rated:
                if (mSortBy.equals(FetchMovies.FAVORITE)) {
                    getSupportLoaderManager().destroyLoader(FAVORITE_MOVIES_LOADER);
                }
                mSortBy = FetchMovies.TOP_RATED;
                fetchMovies(mSortBy);
                item.setChecked(true);
                break;
            case R.id.sort_by_popularity:
                if (mSortBy.equals(FetchMovies.FAVORITE)) {
                    getSupportLoaderManager().destroyLoader(FAVORITE_MOVIES_LOADER);
                }
                mSortBy = FetchMovies.MOST_POPULAR;
                fetchMovies(mSortBy);
                item.setChecked(true);
                break;
            case R.id.sort_by_favourite:
                mSortBy = FetchMovies.FAVORITE;
                item.setChecked(true);
                fetchMovies(mSortBy);
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void open(MovieModel movie, int position) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(FragmentMovieDetail.PARCEL_MOVIE, movie);
            FragmentMovieDetail fragment = new FragmentMovieDetail();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(FragmentMovieDetail.PARCEL_MOVIE, movie);
            startActivity(intent);
        }
    }

    @Override
    public void onFetchFinished(OnTaskCompleted onTaskCompleted) {
        if (onTaskCompleted instanceof FetchMovies.NotifyAboutTaskCompletionCommand) {
            mAdapter.add(((FetchMovies.NotifyAboutTaskCompletionCommand) onTaskCompleted).getMovies());
            updateEmptyState();
            findViewById(R.id.progressBar).setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter.add(cursor);
        updateEmptyState();
        findViewById(R.id.progressBar).setVisibility(View.GONE);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        return new CursorLoader(this,
                MoviesContract.MovieEntry.CONTENT_URI,
                MoviesContract.MovieEntry.MOVIE_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }

    private void fetchMovies(String sortBy) {
        if (!sortBy.equals(FetchMovies.FAVORITE)) {
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            FetchMovies.NotifyAboutTaskCompletionCommand command =
                    new FetchMovies.NotifyAboutTaskCompletionCommand(this.mRetainedFragment);
            new FetchMovies(sortBy, command).execute();
        } else {
            getSupportLoaderManager().initLoader(FAVORITE_MOVIES_LOADER, null, this);
        }
    }

    private void updateEmptyState() {
        if (mAdapter.getItemCount() == 0 && !isNetworkAvailable()) {
            if (mSortBy.equals(FetchMovies.FAVORITE)) {
                findViewById(R.id.empty_connection_container).setVisibility(View.GONE);
                findViewById(R.id.empty_favorites_container).setVisibility(View.VISIBLE);
                Toast.makeText(this, "Not found item movie favorite!", Toast.LENGTH_LONG).show();
            } else {
                findViewById(R.id.empty_connection_container).setVisibility(View.VISIBLE);
                findViewById(R.id.empty_favorites_container).setVisibility(View.GONE);
                Toast.makeText(this, "Check your connection internet!", Toast.LENGTH_LONG).show();
            }
        } else {
            findViewById(R.id.empty_connection_container).setVisibility(View.GONE);
            findViewById(R.id.empty_favorites_container).setVisibility(View.GONE);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static class RetainedFragment extends Fragment implements FetchMovies.Listener {

        private boolean mPaused = false;

        private OnTaskCompleted mWaitingCommand = null;

        public RetainedFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }

        @Override
        public void onPause() {
            super.onPause();
            mPaused = true;
        }

        @Override
        public void onResume() {
            super.onResume();
            mPaused = false;
            if (mWaitingCommand != null) {
                onFetchFinished(mWaitingCommand);
            }
        }

        @Override
        public void onFetchFinished(OnTaskCompleted taskCompleted) {
            if (getActivity() instanceof FetchMovies.Listener && !mPaused) {
                FetchMovies.Listener listener = (FetchMovies.Listener) getActivity();
                listener.onFetchFinished(taskCompleted);
                mWaitingCommand = null;
            } else {
                mWaitingCommand = taskCompleted;
            }
        }
    }
}
