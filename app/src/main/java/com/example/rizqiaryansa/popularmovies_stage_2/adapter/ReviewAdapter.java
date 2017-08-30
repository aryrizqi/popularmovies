package com.example.rizqiaryansa.popularmovies_stage_2.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.rizqiaryansa.popularmovies_stage_2.R;
import com.example.rizqiaryansa.popularmovies_stage_2.model.ReviewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by RizqiAryansa on 8/01/2017.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    @SuppressWarnings("unused")
    private final static String LOG_TAG = ReviewAdapter.class.getSimpleName();

    private final ArrayList<ReviewModel> mReviews;
    private final Callbacks mCallbacks;

    public ReviewAdapter(ArrayList<ReviewModel> reviews, Callbacks callbacks) {
        mReviews = reviews;
        mCallbacks = callbacks;
    }

    public interface Callbacks {
        void read(ReviewModel review, int position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ReviewModel review = mReviews.get(position);

        holder.mReview = review;
        holder.mContent.setText(review.getContent());
        holder.mAuthor.setText(review.getAuthor());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.read(review, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        @Bind(R.id.review_content)
        TextView mContent;
        @Bind(R.id.review_author)
        TextView mAuthor;
        public ReviewModel mReview;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mView = view;
        }
    }

    public void add(List<ReviewModel> reviews) {
        mReviews.clear();
        mReviews.addAll(reviews);
        notifyDataSetChanged();
    }

    public ArrayList<ReviewModel> getReviews() {
        return mReviews;
    }
}
