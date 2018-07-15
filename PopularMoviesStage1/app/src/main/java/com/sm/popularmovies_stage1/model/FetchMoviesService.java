package com.sm.popularmovies_stage1.model;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.sm.popularmovies_stage1.BuildConfig;
import com.sm.popularmovies_stage1.R;
import com.sm.popularmovies_stage1.database.MovieContract;
import com.sm.popularmovies_stage1.ui.MainActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FetchMoviesService extends IntentService {
    public final static String FETCH_POPULAR_MOVIES = "getPopularMovies";
    public final static String FETCH_TOPRATED_MOVIES = "getTopratedMovies";
    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;
    public final static String FETCH_FAV_MOVIES = "getFavMovies";
    private static final String API_KEY = BuildConfig.API_KEY;
    private static final String TAG = "FetchMoviesService";
    ContentResolver mContentResolver;
    ResultReceiver mReceiver;
    public enum enum_Movie_Type {
        POPULAR_MOVIE_TYPE,
        TOPRATED_MOVIE_TYPE,
        FAVORITE_MOVIE_TYPE;
    }

    public FetchMoviesService() {
        super("FetchMoviesService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String action = intent.getAction();
        mReceiver = intent.getParcelableExtra("receiver");
        Bundle bundle = new Bundle();
        if (mReceiver != null) {
            mReceiver.send(STATUS_RUNNING, Bundle.EMPTY);
        }
        if(action.equals(FETCH_POPULAR_MOVIES)) {
            getMovieList(getPopularMoviesCall(), enum_Movie_Type.POPULAR_MOVIE_TYPE);
        } else if (action.equals(FETCH_TOPRATED_MOVIES)) {
            getMovieList(getTopRatedMovieCall(), enum_Movie_Type.TOPRATED_MOVIE_TYPE);
        } else if (action.equals(FETCH_FAV_MOVIES)) {
            createFavoriteList();
        }
        mContentResolver = getContentResolver();
    }

    public static Call<PopularMoviesDto> getPopularMoviesCall() {
        MoviedbService service = RetrofitClientInstance.getRetrofitInstance().create(MoviedbService.class);
        return service.getPopularMovies(API_KEY);
    }

    public static Call<PopularMoviesDto> getTopRatedMovieCall() {
        MoviedbService service = RetrofitClientInstance.getRetrofitInstance().create(MoviedbService.class);
        return service.getTopRatedMovies(API_KEY);
    }

    private void getMovieList(Call<PopularMoviesDto> call, final enum_Movie_Type type) {

        call.enqueue(new Callback<PopularMoviesDto>() {
            @Override
            public void onResponse(@NonNull Call<PopularMoviesDto> call, @NonNull Response<PopularMoviesDto> response) {
                //progressDoalog.dismiss();
                List<Movies> moviesList;
                if (response != null && response.body() != null) {
                    moviesList = response.body().getmResults();

                    insertToDatabase(moviesList,type);
                    mReceiver.send(STATUS_FINISHED, Bundle.EMPTY);
                } else {
                    Log.d(TAG, "null Popular movies response.");
                    Toast.makeText(FetchMoviesService.this, R.string.something_wrong, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PopularMoviesDto> call, @NonNull Throwable t) {
                //progressDialog.dismiss();
                mReceiver.send(STATUS_ERROR, Bundle.EMPTY);
                Toast.makeText(FetchMoviesService.this, R.string.something_wrong, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void insertToDatabase(List<Movies> list, enum_Movie_Type type) {
        int length = list.size();
        for (int i = 0; i < length; i++) {
            Movies movie = list.get(i);
            ContentValues values = new ContentValues();
            values.put(MovieContract.Movie.ID, movie.getmId());
            values.put(MovieContract.Movie.ORIGINAL_TITLE, movie.getmOriginalTitle());
            values.put(MovieContract.Movie.POPULARITY, movie.getmPopularity());
            values.put(MovieContract.Movie.OVERVIEW, movie.getmOverview());
            values.put(MovieContract.Movie.VIDEO, movie.getmVideo());
            values.put(MovieContract.Movie.VOTE_AVERAGE, movie.getmVoteAverage());
            values.put(MovieContract.Movie.VOTE_COUNT, movie.getmVoteCount());
            values.put(MovieContract.Movie.TITLE, movie.getmTitle());
            values.put(MovieContract.Movie.POSTER_PATH, movie.getmPosterPath());
            values.put(MovieContract.Movie.ORIGINAL_LANGUAGE, movie.getmOriginalLanguage());
            values.put(MovieContract.Movie.BACKDROP_PATH, movie.getmBackdropPath());
            values.put(MovieContract.Movie.RELEASE_DATE, movie.getmReleaseDate());
            /*switch (type) {
                case POPULAR_MOVIE_TYPE:
                    values.put(MovieContract.Movie.ISPOP, "1");
                    break;
                case TOPRATED_MOVIE_TYPE:
                    values.put(MovieContract.Movie.ISTOP, "1");
                    break;
                case FAVORITE_MOVIE_TYPE:
                    values.put(MovieContract.Movie.ISFAV, "1");
                    break;
            }*/

            //CursorLoader cursorLoader = new CursorLoader(MainActivity.this, MovieContract.URI_TABLE,
            // new String[]{MovieContract.Movie.POSTER_PATH},MovieContract.Movie.ISPOP+"=?", new String[] {"1"},MovieContract.Movie.ID);

            Cursor cursor = mContentResolver.query(MovieContract.URI_TABLE,new String[]{MovieContract.Movie.ID},MovieContract.Movie.ID+"=?", new String[]{movie.getmId()},MovieContract.Movie.ID );
            if (cursor == null) {
                Uri returned = mContentResolver.insert(MovieContract.URI_TABLE, values);
            }
        }
    }

    public void createFavoriteList() {
        String[] projection = {MovieContract.Movie.ID,
                MovieContract.MovieColumns.VIDEO,
                MovieContract.MovieColumns.VOTE_AVERAGE,
                MovieContract.MovieColumns.TITLE,
                MovieContract.MovieColumns.POPULARITY,
                MovieContract.MovieColumns.POSTER_PATH,
                MovieContract.MovieColumns.ORIGINAL_LANGUAGE,
                MovieContract.MovieColumns.ORIGINAL_TITLE,
                MovieContract.MovieColumns.BACKDROP_PATH,
                MovieContract.MovieColumns.OVERVIEW,
                MovieContract.MovieColumns.VOTE_COUNT,
                MovieContract.MovieColumns.RELEASE_DATE
        };

        List<Movies> entries = new ArrayList<Movies>();

        Cursor mCursor = mContentResolver.query(MovieContract.URI_TABLE, projection, null, null, null);

        if(mCursor != null){
            if(mCursor.moveToFirst()){
                do{
                    int id = mCursor.getInt(mCursor.getColumnIndex(MovieContract.Movie.ID));
                    String video = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieColumns.VIDEO));
                    String vote_average = mCursor.getString(mCursor.getColumnIndex( MovieContract.MovieColumns.VOTE_AVERAGE));
                    String title = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieColumns.TITLE));
                    String popularity = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieColumns.POPULARITY));
                    String poster_path = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieColumns.POSTER_PATH));
                    String origin_language = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieColumns.ORIGINAL_LANGUAGE));
                    String origin_title = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieColumns.ORIGINAL_TITLE));
                    String backdrop_path = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieColumns.BACKDROP_PATH));
                    String overview = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieColumns.OVERVIEW));
                    String vote_count = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieColumns.VOTE_COUNT));
                    String release_date = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieColumns.RELEASE_DATE));
                    Movies movieEntry = new Movies(Integer.parseInt(vote_count),Integer.toString(id),video,vote_average,title,popularity, poster_path, origin_language, origin_title, backdrop_path, false, overview, release_date);
                    entries.add(movieEntry);
                }while(mCursor.moveToNext());
            }
        }
        //mMoviesList = entries;
        //generateDataList(mMoviesList);
    }
}
