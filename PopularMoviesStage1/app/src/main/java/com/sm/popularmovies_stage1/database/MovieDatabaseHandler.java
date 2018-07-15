package com.sm.popularmovies_stage1.database;

import android.content.ContentResolver;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MovieDatabaseHandler extends SQLiteOpenHelper {
    //
    // int VOTE_COUNT = "";
    //        String ID;
    //        String VIDEO;
    //        String VOTE_AVERAGE;
    //        String TITLE;
    //        String POPULARITY;
    //        String POSTER_PATH;
    //        String ORIGINAL_LAMGUAGE;
    //        String ORIGINAL_TITLE;
    //        String BACKDROP_PATH;
    //        boolean ADULT;
    //        String OVERVIEW;
    //        String RELEASE_DATE;
    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 8;
    private ContentResolver contentResolver;
    public interface Tables{
        String MOVIES = "movies";
    }
    public MovieDatabaseHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase database){
        database.execSQL("CREATE TABLE IF NOT EXISTS " + Tables.MOVIES +" ("
                +MovieContract.MovieColumns.ID+" INTEGER PRIMARY KEY,"
                +MovieContract.MovieColumns.VIDEO+" TEXT,"
                +MovieContract.MovieColumns.VOTE_AVERAGE+" TEXT,"
                +MovieContract.MovieColumns.TITLE+" TEXT,"
                +MovieContract.MovieColumns.POPULARITY+" TEXT,"
                +MovieContract.MovieColumns.POSTER_PATH+" TEXT,"
                +MovieContract.MovieColumns.ORIGINAL_LANGUAGE+" TEXT,"
                +MovieContract.MovieColumns.ORIGINAL_TITLE+" TEXT,"
                +MovieContract.MovieColumns.BACKDROP_PATH+" TEXT,"
                +MovieContract.MovieColumns.OVERVIEW+" TEXT,"
                +MovieContract.MovieColumns.VOTE_COUNT+" TEXT,"
                +MovieContract.MovieColumns.RELEASE_DATE+" TEXT)");
                /*+MovieContract.MovieColumns.ISPOP+" TEXT,"
                +MovieContract.MovieColumns.ISTOP+" TEXT,"
                +MovieContract.MovieColumns.ISFAV+" TEXT)");*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Tables.MOVIES);
        onCreate(db);
    }

    public static void deleteDatabase(Context context){
        // Delete the database
        context.deleteDatabase(DATABASE_NAME);
    }
}
