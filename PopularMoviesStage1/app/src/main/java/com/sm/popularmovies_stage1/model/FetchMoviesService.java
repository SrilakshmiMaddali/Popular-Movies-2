package com.sm.popularmovies_stage1.model;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
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
    public final static String FETCH_FAV_MOVIES = "getFavMovies";
    private static final String API_KEY = BuildConfig.API_KEY;
    private static final String TAG = "FetchMoviesService";
    ContentResolver mContentResolver;
    public FetchMoviesService() {
        super("FetchMoviesService");
        mContentResolver = FetchMoviesService.this.getContentResolver();
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String action = intent.getAction();
        if(action.equals(FETCH_POPULAR_MOVIES)) {
            getMovieList(getPopularMoviesCall());
        }
        else if (action.equals(FETCH_TOPRATED_MOVIES)) {
            getMovieList(getTopRatedMovieCall());
        }
        else if (action.equals(FETCH_FAV_MOVIES)) {
            createFavoriteList();
        }

    }

    private Call<PopularMoviesDto> getPopularMoviesCall() {
        MoviedbService service = RetrofitClientInstance.getRetrofitInstance().create(MoviedbService.class);
        return service.getPopularMovies(API_KEY);
    }

    private Call<PopularMoviesDto> getTopRatedMovieCall() {
        MoviedbService service = RetrofitClientInstance.getRetrofitInstance().create(MoviedbService.class);
        return service.getTopRatedMovies(API_KEY);
    }

    private void getMovieList(Call<PopularMoviesDto> call) {

        call.enqueue(new Callback<PopularMoviesDto>() {
            @Override
            public void onResponse(@NonNull Call<PopularMoviesDto> call, @NonNull Response<PopularMoviesDto> response) {
                //progressDoalog.dismiss();
                if (response != null && response.body() != null) {
                    mMoviesList = response.body().getmResults();
                    generateDataList(mMoviesList);
                } else {
                    Log.d(TAG, "null Popular movies response.");
                    Toast.makeText(FetchMoviesService.this, R.string.something_wrong, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PopularMoviesDto> call, @NonNull Throwable t) {
                //progressDialog.dismiss();
                Toast.makeText(FetchMoviesService.this, R.string.something_wrong, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createFavoriteList() {
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
