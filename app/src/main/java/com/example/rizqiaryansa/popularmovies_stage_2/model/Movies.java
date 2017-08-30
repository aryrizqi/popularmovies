package com.example.rizqiaryansa.popularmovies_stage_2.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RizqiAryansa on 8/01/2017.
 */

public class Movies {
    @SerializedName("results")
    private List<MovieModel> movies = new ArrayList<>();

    public List<MovieModel> getMovies() {
        return movies;
    }
}
