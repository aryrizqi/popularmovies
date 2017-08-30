package com.example.rizqiaryansa.popularmovies_stage_2.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by RizqiAryansa on 8/01/2017.
 */

public class MoviesDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "movies.db";

    public MoviesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MoviesContract.MovieEntry.TABLE_NAME
                + " (" +
                MoviesContract.MovieEntry._ID + " INTEGER PRIMARY KEY," +
                MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_MOVIE_BACKDROP_PATH + " TEXT NOT NULL " +
                " );";
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
