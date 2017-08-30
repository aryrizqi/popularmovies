package com.example.rizqiaryansa.popularmovies_stage_2;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rizqiaryansa.popularmovies_stage_2.adapter.ReviewAdapter;
import com.example.rizqiaryansa.popularmovies_stage_2.adapter.TrailerAdapter;
import com.example.rizqiaryansa.popularmovies_stage_2.connection.FetchReviews;
import com.example.rizqiaryansa.popularmovies_stage_2.connection.FetchTrailers;
import com.example.rizqiaryansa.popularmovies_stage_2.database.MoviesContract;
import com.example.rizqiaryansa.popularmovies_stage_2.model.MovieModel;
import com.example.rizqiaryansa.popularmovies_stage_2.model.ReviewModel;
import com.example.rizqiaryansa.popularmovies_stage_2.model.TrailerModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FragmentMovieDetail extends Fragment implements FetchTrailers.Listener,
        TrailerAdapter.Callbacks, FetchReviews.Listener, ReviewAdapter.Callbacks {

    @SuppressWarnings("unused")
    public static final String LOG_TAG = FragmentMovieDetail.class.getSimpleName();

    public static final String PARCEL_MOVIE = "PARCEL_MOVIE";
    public static final String EXTRA_TRAILERS = "EXTRA_TRAILERS";
    public static final String EXTRA_REVIEWS = "EXTRA_REVIEWS";

    private MovieModel mMovie;
    private TrailerAdapter mTrailerListAdapter;
    private ReviewAdapter mReviewListAdapter;
    private ShareActionProvider mShareActionProvider;

    @Bind(R.id.linearLayout)
    LinearLayout mLinearLayourSnackbar;

    @Bind(R.id.trailer_list)
    RecyclerView mRecyclerViewForTrailers;
    @Bind(R.id.review_list)
    RecyclerView mRecyclerViewForReviews;

    @Bind(R.id.movie_title)
    TextView mMovieTitle;
    @Bind(R.id.movie_overview)
    TextView mMovieOverview;
    @Bind(R.id.movie_release_date)
    TextView mMovieReleaseDate;
    @Bind(R.id.movie_user_rating)
    TextView mMovieRating;
    @Bind(R.id.movie_poster)
    ImageView mMoviePoster;

    @Bind(R.id.mark_as_favorite)
    ImageView mAdAsFavorite;
    @Bind(R.id.remove_from_favorite)
    ImageView mRemoveFromFavorite;

    @Bind({R.id.rating_first_star, R.id.rating_second_star, R.id.rating_third_star,
            R.id.rating_fourth_star, R.id.rating_fifth_star})
    List<ImageView> ratingStar;

    private String release_date = "Release date: ";

    public FragmentMovieDetail() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(PARCEL_MOVIE)) {
            mMovie = getArguments().getParcelable(PARCEL_MOVIE);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Activity activity = getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout)
                activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null && activity instanceof DetailActivity) {
            appBarLayout.setTitle(mMovie.getTitle());
        }

        ImageView movieBackdrop = ((ImageView) activity.findViewById(R.id.movie_backdrop));
        if (movieBackdrop != null) {
            Picasso.with(activity)
                    .load(mMovie.getBackdropUrl(getContext()))
                    .config(Bitmap.Config.RGB_565)
                    .into(movieBackdrop);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, rootView);

        mMovieTitle.setText(mMovie.getTitle());
        mMovieOverview.setText(mMovie.getOverview());
        mMovieReleaseDate.setText(release_date + mMovie.getReleaseDate(getContext()));

        Picasso.with(getContext())
                .load(mMovie.getPosterUrl(getContext()))
                .config(Bitmap.Config.RGB_565)
                .into(mMoviePoster);

        updateRatingBar();
        updateFavoriteButtons();

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerViewForTrailers.setLayoutManager(layoutManager);
        mTrailerListAdapter = new TrailerAdapter(new ArrayList<TrailerModel>(), this);
        mRecyclerViewForTrailers.setAdapter(mTrailerListAdapter);
        mRecyclerViewForTrailers.setNestedScrollingEnabled(false);

        mReviewListAdapter = new ReviewAdapter(new ArrayList<ReviewModel>(), this);
        mRecyclerViewForReviews.setAdapter(mReviewListAdapter);

        if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_TRAILERS)) {
            List<TrailerModel> trailers = savedInstanceState.getParcelableArrayList(EXTRA_TRAILERS);
            mTrailerListAdapter.add(trailers);
        } else {
            if(isNetworkAvailable())
            fetchTrailers();
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_REVIEWS)) {
            List<ReviewModel> reviews = savedInstanceState.getParcelableArrayList(EXTRA_REVIEWS);
            mReviewListAdapter.add(reviews);
        } else {
            if(isNetworkAvailable())
            fetchReviews();
        }

        return rootView;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<TrailerModel> trailers = mTrailerListAdapter.getTrailers();
        if (trailers != null && !trailers.isEmpty()) {
            outState.putParcelableArrayList(EXTRA_TRAILERS, trailers);
        }

        ArrayList<ReviewModel> reviews = mReviewListAdapter.getReviews();
        if (reviews != null && !reviews.isEmpty()) {
            outState.putParcelableArrayList(EXTRA_REVIEWS, reviews);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail, menu);
        MenuItem shareTrailerMenuItem = menu.findItem(R.id.share_trailer);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareTrailerMenuItem);
    }

    @Override
    public void watch(TrailerModel trailer, int position) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(trailer.getTrailerUrl())));
    }

    @Override
    public void read(ReviewModel review, int position) {
        startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(review.getUrl())));
    }

    @Override
    public void onTrailerFetchFinished(List<TrailerModel> trailers) {
        mTrailerListAdapter.add(trailers);

        if (mTrailerListAdapter.getItemCount() > 0) {
            TrailerModel trailer = mTrailerListAdapter.getTrailers().get(0);
            updateShareActionProvider(trailer);
        }
    }

    @Override
    public void onReviewsFetchFinished(List<ReviewModel> reviews) {
        mReviewListAdapter.add(reviews);
    }

    private void fetchTrailers() {
        FetchTrailers task = new FetchTrailers(this);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mMovie.getId());
    }

    private void fetchReviews() {
        FetchReviews task = new FetchReviews(this);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mMovie.getId());
    }

    public void addAsFavorite() {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                if (!isFavorite()) {
                    ContentValues movieValues = new ContentValues();
                    movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_ID,
                            mMovie.getId());
                    movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_TITLE,
                            mMovie.getTitle());
                    movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH,
                            mMovie.getPoster());
                    movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_OVERVIEW,
                            mMovie.getOverview());
                    movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE,
                            mMovie.getUserRating());
                    movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE,
                            mMovie.getReleaseDate());
                    movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_BACKDROP_PATH,
                            mMovie.getBackdrop());
                    getContext().getContentResolver().insert(
                            MoviesContract.MovieEntry.CONTENT_URI,
                            movieValues
                    );
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                updateFavoriteButtons();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void removeFromFavorites() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                if (isFavorite()) {
                    getContext().getContentResolver().delete(MoviesContract.MovieEntry.CONTENT_URI,
                            MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " = " + mMovie.getId(), null);

                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                updateFavoriteButtons();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void updateRatingBar() {
        if (mMovie.getUserRating() != null && !mMovie.getUserRating().isEmpty()) {
            String userRatingStr = getResources().getString(R.string.user_rating_movie,
                    mMovie.getUserRating());
            mMovieRating.setText(userRatingStr);

            float userRating = Float.valueOf(mMovie.getUserRating()) / 2;
            int integerPart = (int) userRating;

            for (int i = 0; i < integerPart; i++) {
                ratingStar.get(i).setImageResource(R.drawable.ic_star_black_24dp);
            }

            if (Math.round(userRating) > integerPart) {
                ratingStar.get(integerPart).setImageResource(
                        R.drawable.ic_star_half_black_24dp);
            }

        } else {
            mMovieRating.setVisibility(View.GONE);
        }
    }

    private void updateFavoriteButtons() {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                return isFavorite();
            }

            @Override
            protected void onPostExecute(Boolean isFavorite) {
                if (isFavorite) {
                    mRemoveFromFavorite.setVisibility(View.VISIBLE);
                    mAdAsFavorite.setVisibility(View.GONE);
                } else {
                    mAdAsFavorite.setVisibility(View.VISIBLE);
                    mRemoveFromFavorite.setVisibility(View.GONE);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        mAdAsFavorite.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addAsFavorite();
                        Toast.makeText(getActivity(), "Add Favorite Movie " + mMovie.getTitle() + " Success!",
                                Toast.LENGTH_LONG).show();
                    }
                });

        mRemoveFromFavorite.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeFromFavorites();
                        Toast.makeText(getActivity(), "Remove Favorite Movie " + mMovie.getTitle() + " Success!",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private boolean isFavorite() {
        Cursor movieCursor = getContext().getContentResolver().query(
                MoviesContract.MovieEntry.CONTENT_URI,
                new String[]{MoviesContract.MovieEntry.COLUMN_MOVIE_ID},
                MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " = " + mMovie.getId(),
                null,
                null);

        if (movieCursor != null && movieCursor.moveToFirst()) {
            movieCursor.close();
            return true;
        } else {
            return false;
        }
    }

    private void updateShareActionProvider(TrailerModel trailer) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(android.content.Intent.EXTRA_SUBJECT, mMovie.getTitle());
        share.putExtra(android.content.Intent.EXTRA_TEXT, "Watch Trailer " + mMovie.getTitle() + "! \n"
                + trailer.getName() + ": " + trailer.getTrailerUrl());
        mShareActionProvider.setShareIntent(share);

    }
}