package com.sm.popularmovies_stage1.database;

import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {

    public interface MovieColumns {
        String ID="movies_id";
        String VOTE_COUNT ="movies_vote_count";
        String VIDEO="movies_video";
        String VOTE_AVERAGE="movies_vote_average";
        String TITLE="movies_title";
        String POPULARITY="movies_popularity";
        String POSTER_PATH="movies_poster_path";
        String ORIGINAL_LANGUAGE="movies_original_language";
        String ORIGINAL_TITLE="movies_original_title";
        String BACKDROP_PATH="movies_backdrop_path";
        String OVERVIEW="movies_overview";
        String RELEASE_DATE="movies_release_date";
    }

    // Used to access the content
    public static final String CONTENT_AUTHORITY = "com.sm.popularmovies_stage1.database.provider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);

    private static final String PATH_MOVIES = "movies";

    public static final Uri URI_TABLE = Uri.parse(BASE_CONTENT_URI.toString() +"/"+PATH_MOVIES );

    public static final String[] TOP_LEVEL_PATHS = {PATH_MOVIES};

    public static class Movie implements MovieColumns,BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendEncodedPath(PATH_MOVIES).build();
        // Accessing content directory and item
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd."+CONTENT_AUTHORITY+".movies";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."+CONTENT_AUTHORITY+".movies";

        public static Uri buildMovieUri(String movieId){
            return CONTENT_URI.buildUpon().appendEncodedPath(movieId).build();
        }

        public static String getMovieId(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }

}
