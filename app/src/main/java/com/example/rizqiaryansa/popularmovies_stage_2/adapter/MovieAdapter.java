package com.example.rizqiaryansa.popularmovies_stage_2.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rizqiaryansa.popularmovies_stage_2.R;
import com.example.rizqiaryansa.popularmovies_stage_2.database.MoviesContract;
import com.example.rizqiaryansa.popularmovies_stage_2.model.MovieModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by RizqiAryansa on 8/01/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    @SuppressWarnings("unused")
    private final static String LOG_TAG = MovieAdapter.class.getSimpleName();

    private final ArrayList<MovieModel> mMovies;
    private final Callbacks mCallbacks;

    public interface Callbacks {
        void open(MovieModel movie, int position);
    }

    public MovieAdapter(ArrayList<MovieModel> movies, Callbacks callbacks) {
        mMovies = movies;
        this.mCallbacks = callbacks;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_movie, parent, false);
        final Context context = view.getContext();

        int gridColsNumber = context.getResources()
                .getInteger(R.integer.grid_number_cols);

        view.getLayoutParams().height = (int) (parent.getWidth() / gridColsNumber *
                MovieModel.POSTER_ASPECT_RATIO);

        return new ViewHolder(view);
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.cleanUp();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final MovieModel movie = mMovies.get(position);
        final Context context = holder.mView.getContext();

        holder.mMovie = movie;
        holder.mTitle.setText(movie.getTitle());

        String posterUrl = movie.getPosterUrl(context);

        if (posterUrl == null) {
            holder.mTitle.setVisibility(View.VISIBLE);
        }

        Picasso.with(context)
                .load(movie.getPosterUrl(context))
                .config(Bitmap.Config.RGB_565)
                .into(holder.mThumbnail,
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                if (holder.mMovie.getId() != movie.getId()) {
                                    holder.cleanUp();
                                } else {
                                    holder.mThumbnail.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onError() {
                                holder.mTitle.setVisibility(View.VISIBLE);
                            }
                        }
                );

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.open(movie, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        @Bind(R.id.thumbnail)
        ImageView mThumbnail;
        @Bind(R.id.title)
        TextView mTitle;
        public MovieModel mMovie;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mView = view;
        }

        public void cleanUp() {
            final Context context = mView.getContext();
            Picasso.with(context).cancelRequest(mThumbnail);
            mThumbnail.setImageBitmap(null);
            mThumbnail.setVisibility(View.INVISIBLE);
            mTitle.setVisibility(View.GONE);
        }

    }

    public void add(List<MovieModel> movies) {
        mMovies.clear();
        mMovies.addAll(movies);
        notifyDataSetChanged();
    }

    public void add(Cursor cursor) {
        mMovies.clear();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(MoviesContract.MovieEntry.COL_MOVIE_ID);
                String title = cursor.getString(MoviesContract.MovieEntry.COL_MOVIE_TITLE);
                String posterPath = cursor.getString(MoviesContract.MovieEntry.COL_MOVIE_POSTER_PATH);
                String overview = cursor.getString(MoviesContract.MovieEntry.COL_MOVIE_OVERVIEW);
                String rating = cursor.getString(MoviesContract.MovieEntry.COL_MOVIE_VOTE_AVERAGE);
                String releaseDate = cursor.getString(MoviesContract.MovieEntry.COL_MOVIE_RELEASE_DATE);
                String backdropPath = cursor.getString(MoviesContract.MovieEntry.COL_MOVIE_BACKDROP_PATH);
                MovieModel movie = new MovieModel(id, title, posterPath, overview, rating, releaseDate, backdropPath);
                mMovies.add(movie);
            } while (cursor.moveToNext());
        }
        notifyDataSetChanged();
    }

    public ArrayList<MovieModel> getMovies() {
        return mMovies;
    }
}
