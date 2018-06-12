package com.sm.popularmovies_stage1.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

public class MovieContentProvider extends ContentProvider {
    private static final String TAG = "MovieContentProvider";
    private MovieDatabaseHandler movieDatabase;
    // Checks for valid URIs
    static UriMatcher sUriMatcher;
    static final int DATABASE_VERSION = 1;
    private static final int MOVIE = 100;
    private static final int MOVIE_ID = 101;

     static {
        // Initalize
         sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

         sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY,"movies", MOVIE);
         sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY,"movies/#", MOVIE_ID);

    }

    @Override
    public boolean onCreate() {
        movieDatabase = new MovieDatabaseHandler(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch(match){
            case MOVIE:
                return MovieContract.Movie.CONTENT_TYPE;
            case MOVIE_ID:
                return MovieContract.Movie.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI : "+ uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Log.v(TAG,"insert(uri="+ uri + ", values="+values.toString());
        final SQLiteDatabase db = movieDatabase.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match){
            case MOVIE:
                // Create a new record
                long recordId = db.insertOrThrow(MovieDatabaseHandler.Tables.MOVIES, null, values);
                return MovieContract.Movie.buildMovieUri(String.valueOf(recordId));
            default:
                throw new IllegalArgumentException("Unknown URI : "+ uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.v(TAG,"delete(uri="+ uri);
        final SQLiteDatabase db = movieDatabase.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch(match){
            case MOVIE:
                // Do nothing
                break;
            case MOVIE_ID:
                String id = MovieContract.Movie.getMovieId(uri);
                String selectionCriteria = MovieContract.Movie.ID + "=" + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
                return db.delete(MovieDatabaseHandler.Tables.MOVIES, selectionCriteria, selectionArgs);
            default:
                throw new IllegalArgumentException("Unknown URI : " + uri);
        }

        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.v(TAG,"update(uri="+ uri + ", values="+values.toString());
        final SQLiteDatabase db = movieDatabase.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        String selectionCriteria = selection;

        switch (match){
            case MOVIE:
                // Do nothing
                break;
            case MOVIE_ID:
                String id = MovieContract.Movie.getMovieId(uri);
                selectionCriteria = MovieContract.Movie.ID + "=" + id
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
                break;
            default:
                throw new IllegalArgumentException("Unknown URI : "+ uri);
        }

        int updateCount = db.update(MovieDatabaseHandler.Tables.MOVIES, values, selectionCriteria, selectionArgs);
        return updateCount;

    }

    @Override
    public Cursor query(Uri uri, String[] projection,
                        String selection,String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = movieDatabase.getReadableDatabase();
        final int match = sUriMatcher.match(uri);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        queryBuilder.setTables(MovieDatabaseHandler.Tables.MOVIES);

        switch(match){
            case MOVIE:
                break;
            case MOVIE_ID:
                String id  = MovieContract.Movie.getMovieId(uri);
                queryBuilder.appendWhere(MovieContract.Movie.ID + "="+ id);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI : "+ uri);
        }

        // Projection : Columns to return
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                
            }
        }
        return cursor;
    }
}
