package com.sm.popularmovies_stage1.database;

import android.content.ContentResolver;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

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
    private static final int DATABASE_VERSION = 1;
    private ContentResolver contentResolver;
    interface Tables{
        String MOVIES = "movies";
    }
    public MovieDatabaseHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase database){
        database.execSQL("CREATE TABLE " + Tables.MOVIES +" ("
                +BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                +MovieContract.MovieColumns.ID+"TEXT NOT NULL,"
                +MovieContract.MovieColumns.VIDEO+"TEXT NOT NULL,"
                +MovieContract.MovieColumns.VOTE_AVERAGE+"TEXT NOT NULL,"
                +MovieContract.MovieColumns.TITLE+"TEXT NOT NULL,"
                +MovieContract.MovieColumns.POPULARITY+"TEXT NOT NULL,"
                +MovieContract.MovieColumns.POSTER_PATH+"TEXT NOT NULL,"
                +MovieContract.MovieColumns.ORIGINAL_LANGUAGE+"TEXT NOT NULL,"
                +MovieContract.MovieColumns.ORIGINAL_TITLE+"TEXT NOT NULL,"
                +MovieContract.MovieColumns.BACKDROP_PATH+"TEXT NOT NULL,"
                +MovieContract.MovieColumns.ADULT+"TEXT NOT NULL,"
                +MovieContract.MovieColumns.OVERVIEW+"TEXT NOT NULL,"
                +MovieContract.MovieColumns.RELEASE_DATE+"TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public static void deleteDatabase(Context context){
        // Delete the database
        context.deleteDatabase(DATABASE_NAME);
    }
}
