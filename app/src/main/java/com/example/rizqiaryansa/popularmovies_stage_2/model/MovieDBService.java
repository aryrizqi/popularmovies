package com.example.rizqiaryansa.popularmovies_stage_2.model;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by RizqiAryansa on 8/01/2017.
 */

public interface MovieDBService {

    @GET("3/movie/{sort_by}")
    Call<Movies> loadMovies(@Path("sort_by") String sortBy, @Query("api_key") String apiKey);

    @GET("3/movie/{id}/videos")
    Call<Trailers> loadTrailers(@Path("id") long movieId, @Query("api_key") String apiKey);

    @GET("3/movie/{id}/reviews")
    Call<Reviews> loadReviews(@Path("id") long movieId, @Query("api_key") String apiKey);

}
