package com.example.rizqiaryansa.popularmovies_stage_2.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RizqiAryansa on 8/01/2017.
 */

public class Trailers {

    @SerializedName("results")
    private List<TrailerModel> trailers = new ArrayList<>();

    public List<TrailerModel> getTrailers() {
        return trailers;
    }
}
