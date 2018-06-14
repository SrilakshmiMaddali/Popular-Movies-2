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
    private static final int P_MOVIE = 100;
    private static final int P_MOVIE_ID = 101;
    private static final int T_MOVIE = 200;
    private static final int T_MOVIE_ID = 201;
    private static final int F_MOVIE = 300;
    private static final int F_MOVIE_ID = 301;
     static {
        // Initalize
         sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

         sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY,"movies", P_MOVIE);
         sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY,"movies/#", P_MOVIE_ID);
         sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY,"movies", T_MOVIE);
         sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY,"movies/#", T_MOVIE_ID);
         sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY,"movies", F_MOVIE);
         sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY,"movies/#", F_MOVIE_ID);

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
            case P_MOVIE:
            case T_MOVIE:
            case F_MOVIE:
                return MovieContract.Movie.CONTENT_TYPE;
            case P_MOVIE_ID:
            case T_MOVIE_ID:
            case F_MOVIE_ID:
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
        long recordId;
        switch (match){
            case P_MOVIE:
                // Create a new record
                recordId = db.insertOrThrow(MovieDatabaseHandler.Tables.POPULAR_MOVIES, null, values);
                return MovieContract.Movie.buildMovieUri(String.valueOf(recordId));
            case T_MOVIE:
                // Create a new record
                recordId = db.insertOrThrow(MovieDatabaseHandler.Tables.TOPRATED_MOVIES, null, values);
                return MovieContract.Movie.buildMovieUri(String.valueOf(recordId));
            case F_MOVIE:
                // Create a new record
                recordId = db.insertOrThrow(MovieDatabaseHandler.Tables.FAVORITE_MOVIES, null, values);
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
        String selectionCriteria;
        String id;
        switch(match){
            case P_MOVIE:
            case T_MOVIE:
            case F_MOVIE:
                // Do nothing
                break;
            case P_MOVIE_ID:
                 id = MovieContract.Movie.getMovieId(uri);
                 selectionCriteria = MovieContract.Movie.ID + "=" + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
                return db.delete(MovieDatabaseHandler.Tables.POPULAR_MOVIES, selectionCriteria, selectionArgs);
            case T_MOVIE_ID:
                 id = MovieContract.Movie.getMovieId(uri);
                 selectionCriteria = MovieContract.Movie.ID + "=" + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
                return db.delete(MovieDatabaseHandler.Tables.TOPRATED_MOVIES, selectionCriteria, selectionArgs);
            case F_MOVIE_ID:
                id = MovieContract.Movie.getMovieId(uri);
                selectionCriteria = MovieContract.Movie.ID + "=" + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
                return db.delete(MovieDatabaseHandler.Tables.FAVORITE_MOVIES, selectionCriteria, selectionArgs);
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
        int updateCount = -1;
        String id;

        String selectionCriteria = selection;

        switch (match){
            case P_MOVIE:
            case T_MOVIE:
            case F_MOVIE:
                // Do nothing
                break;
            case P_MOVIE_ID:
                id = MovieContract.Movie.getMovieId(uri);
                selectionCriteria = MovieContract.Movie.ID + "=" + id
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
                updateCount = db.update(MovieDatabaseHandler.Tables.POPULAR_MOVIES, values, selectionCriteria, selectionArgs);
                break;
            case T_MOVIE_ID:
                id = MovieContract.Movie.getMovieId(uri);
                selectionCriteria = MovieContract.Movie.ID + "=" + id
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
                updateCount = db.update(MovieDatabaseHandler.Tables.TOPRATED_MOVIES, values, selectionCriteria, selectionArgs);
                break;
            case F_MOVIE_ID:
                id = MovieContract.Movie.getMovieId(uri);
                selectionCriteria = MovieContract.Movie.ID + "=" + id
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
                updateCount = db.update(MovieDatabaseHandler.Tables.FAVORITE_MOVIES, values, selectionCriteria, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI : "+ uri);
        }
        return updateCount;
    }

    @Override
    public Cursor query(Uri uri, String[] projection,
                        String selection,String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = movieDatabase.getReadableDatabase();
        final int match = sUriMatcher.match(uri);
        String id;

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();



        switch(match){
            case P_MOVIE:
            case T_MOVIE:
            case F_MOVIE:
                break;
            case P_MOVIE_ID:
                queryBuilder.setTables(MovieDatabaseHandler.Tables.POPULAR_MOVIES);
                id = MovieContract.Movie.getMovieId(uri);
                queryBuilder.appendWhere(MovieContract.Movie.ID + "="+ id);
                break;
            case F_MOVIE_ID:
                queryBuilder.setTables(MovieDatabaseHandler.Tables.FAVORITE_MOVIES);
                id  = MovieContract.Movie.getMovieId(uri);
                queryBuilder.appendWhere(MovieContract.Movie.ID + "="+ id);
                break;
            case T_MOVIE_ID:
                queryBuilder.setTables(MovieDatabaseHandler.Tables.TOPRATED_MOVIES);
                id  = MovieContract.Movie.getMovieId(uri);
                queryBuilder.appendWhere(MovieContract.Movie.ID + "="+ id);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI : "+ uri);
        }

        // Projection : Columns to return
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        return cursor;
    }
}
