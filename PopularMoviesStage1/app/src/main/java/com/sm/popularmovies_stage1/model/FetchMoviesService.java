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

import static com.sm.popularmovies_stage1.model.FetchMoviesService.enum_Movie_Type.DEFAULT;

public class FetchMoviesService extends IntentService {
    public final static String FETCH_POPULAR_MOVIES = "getPopularMovies";
    public final static String FETCH_TOPRATED_MOVIES = "getTopratedMovies";
    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;
    public final static String FETCH_FAV_MOVIES = "getFavMovies";
    public final static String DEFAULT_MODE ="getAll";
    private static final String API_KEY = BuildConfig.API_KEY;
    private static final String TAG = "FetchMoviesService";
    ContentResolver mContentResolver;
    ResultReceiver mReceiver;
    public enum enum_Movie_Type {
        POPULAR_MOVIE_TYPE,
        TOPRATED_MOVIE_TYPE,
        FAVORITE_MOVIE_TYPE,
        DEFAULT;
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
            mReceiver.send(STATUS_FINISHED, Bundle.EMPTY);
        } else if (action.equals(DEFAULT_MODE)) {
            getMovieList(getPopularMoviesCall(), enum_Movie_Type.POPULAR_MOVIE_TYPE);
            getMovieList(getTopRatedMovieCall(), enum_Movie_Type.TOPRATED_MOVIE_TYPE);
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
                    if (moviesList.size() > 0) {
                        insertToDatabase(moviesList, type);
                        mReceiver.send(STATUS_FINISHED, Bundle.EMPTY);
                    }
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
            switch (type) {
                case POPULAR_MOVIE_TYPE:
                    values.put(MovieContract.Movie.ISPOP, "1");
                    Cursor popCursor = mContentResolver.query(MovieContract.Movie.buildMovieUri(movie.getmId()), null, null, null,
                            null);
                    if (popCursor != null && popCursor.moveToFirst()) {
                        if (!((popCursor.getString(popCursor.getColumnIndex(MovieContract.Movie.ID)).equalsIgnoreCase(movie.getmId())))) {
                            Uri returned = mContentResolver.insert(MovieContract.URI_TABLE, values);
                        } else {
                            mContentResolver.update(MovieContract.URI_TABLE, values, null, null);
                        }
                    }
                    break;
                case TOPRATED_MOVIE_TYPE:
                    values.put(MovieContract.Movie.ISTOP, "1");
                    Cursor topCursor = mContentResolver.query(MovieContract.Movie.buildMovieUri(movie.getmId()), null, null,null,
                            null);
                    if (topCursor != null  && topCursor.moveToFirst()) {
                        if (!((topCursor.getString(topCursor.getColumnIndex(MovieContract.Movie.ID)).equalsIgnoreCase(movie.getmId())))) {
                            Uri returned = mContentResolver.insert(MovieContract.URI_TABLE, values);
                        } else {
                            mContentResolver.update(MovieContract.URI_TABLE, values, null, null);
                        }
                    }

                    break;
                case FAVORITE_MOVIE_TYPE:
                    values.put(MovieContract.Movie.ISFAV, "1");
                    Cursor favCursor = mContentResolver.query(MovieContract.Movie.buildMovieUri(movie.getmId()), null, null,null,
                            null);
                    if(favCursor != null) {
                        if (favCursor.moveToFirst()) {
                            do {
                                String isFav = favCursor.getString(favCursor.getColumnIndex(MovieContract.Movie.ISFAV));
                                if (!isFav.equals("1")) {
                                    mContentResolver.insert(MovieContract.URI_TABLE, values);
                                }
                            }while (favCursor.moveToNext());
                        } else {
                            mContentResolver.insert(MovieContract.URI_TABLE, values);
                        }
                    } else {
                        mContentResolver.insert(MovieContract.URI_TABLE, values);
                    }
                    break;
            }
        }
    }
}
