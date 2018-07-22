package com.sm.popularmovies_stage1.ui;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.Toast;

import com.sm.popularmovies_stage1.BuildConfig;
import com.sm.popularmovies_stage1.R;
import com.sm.popularmovies_stage1.database.MovieContract;
import com.sm.popularmovies_stage1.model.CustomAdapter;
import com.sm.popularmovies_stage1.model.DataDownloadResultReceiver;
import com.sm.popularmovies_stage1.model.FetchMoviesService;
import com.sm.popularmovies_stage1.model.Movies;
import com.sm.popularmovies_stage1.model.MoviesListCursorAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, DataDownloadResultReceiver.Receiver {
    GridView gridview;
    Context mContext;
    MoviesListCursorAdapter mMovieListAdapter;
    MenuItem top;
    MenuItem pop;
    MenuItem fav;
    private static final String TAG = "MainActivity";
    ContentResolver mContentResolver;
    DataDownloadResultReceiver mReceiver;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String POPULAR_PREF_VALUE = "popular";
    public static final String TOPRATED_PREF_VALUE= "toprated";
    public static final String FAV_PREF_VALUE = "favorite";
    public static final String USER_PREFERENCE_KEY = "userpreference";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mContentResolver = MainActivity.this.getContentResolver();
        mReceiver = new DataDownloadResultReceiver(new Handler());
        gridview = findViewById(R.id.gridview);
        mReceiver.setReceiver(this);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(USER_PREFERENCE_KEY, POPULAR_PREF_VALUE);
        editor.commit();
        startFetchMoviesService(FetchMoviesService.enum_Movie_Type.DEFAULT);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor clickedCursor = (Cursor) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class);
                String voteCount = clickedCursor.getString(clickedCursor.getColumnIndex(MovieContract.Movie.VOTE_COUNT));
                String id = clickedCursor.getString(clickedCursor.getColumnIndex(MovieContract.Movie.ID));
                String video = clickedCursor.getString(clickedCursor.getColumnIndex(MovieContract.Movie.VIDEO));
                String voteAverage = clickedCursor.getString(clickedCursor.getColumnIndex(MovieContract.Movie.VOTE_AVERAGE));
                String title = clickedCursor.getString(clickedCursor.getColumnIndex(MovieContract.Movie.TITLE));
                String popularity = clickedCursor.getString(clickedCursor.getColumnIndex(MovieContract.Movie.POPULARITY));
                String posterPath = clickedCursor.getString(clickedCursor.getColumnIndex(MovieContract.Movie.POSTER_PATH));
                String originalLanguage = clickedCursor.getString(clickedCursor.getColumnIndex(MovieContract.Movie.ORIGINAL_LANGUAGE));
                String originalTitle = clickedCursor.getString(clickedCursor.getColumnIndex(MovieContract.Movie.ORIGINAL_TITLE));
                String backdropPath = clickedCursor.getString(clickedCursor.getColumnIndex(MovieContract.Movie.BACKDROP_PATH));
                String overView = clickedCursor.getString(clickedCursor.getColumnIndex(MovieContract.Movie.OVERVIEW));
                String releaseDate = clickedCursor.getString(clickedCursor.getColumnIndex(MovieContract.Movie.RELEASE_DATE));
                Movies userElected = new Movies(Integer.parseInt(voteCount),id, video, voteAverage, title, popularity,
                                posterPath, originalLanguage, originalTitle,
                                backdropPath,true,overView, releaseDate);
                intent.putExtra(MovieDetailsActivity.EXTRA_MOVIE, userElected);
                startActivityForResult(intent,100);
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        getSupportLoaderManager().initLoader(1, null, MainActivity.this);
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        getSupportLoaderManager().restartLoader(1, null, MainActivity.this);
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movietypefilter, menu);
        top = menu.findItem(R.id.top_movies);
        pop = menu.findItem(R.id.popular_movies);
        fav = menu.findItem(R.id.fv_movies);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        switch (item.getItemId()) {
            case R.id.popular_movies:
                editor.putString(USER_PREFERENCE_KEY, POPULAR_PREF_VALUE);
                editor.commit();
                top.setVisible(true);
                pop.setVisible(false);
                invalidateOptionsMenu();
                setTitle(R.string.popular_movies);
                getSupportLoaderManager().restartLoader(1, null, MainActivity.this);
                break;
            case R.id.top_movies:
                editor.putString(USER_PREFERENCE_KEY, TOPRATED_PREF_VALUE);
                editor.commit();
                top.setVisible(false);
                pop.setVisible(true);
                setTitle(R.string.top_movies);
                getSupportLoaderManager().restartLoader(1, null, MainActivity.this);
                break;
            case R.id.fv_movies:
                setTitle(R.string.favorite_movies);
                editor.putString(USER_PREFERENCE_KEY, FAV_PREF_VALUE);
                editor.commit();
                getSupportLoaderManager().restartLoader(1, null, MainActivity.this);
                break;
            default:
                break;
        }
        return true;
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable Bundle args) {
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
                MovieContract.MovieColumns.RELEASE_DATE,
                MovieContract.MovieColumns.ISPOP,
                MovieContract.MovieColumns.ISTOP,
                MovieContract.MovieColumns.ISFAV
        };
        CursorLoader cursorLoader = null;
        String  pref = sharedpreferences.getString(USER_PREFERENCE_KEY, null);
        if (pref.equalsIgnoreCase(POPULAR_PREF_VALUE)) {
            cursorLoader = new CursorLoader(MainActivity.this, MovieContract.URI_TABLE, projection, MovieContract.Movie.ISPOP + "=?",
                            new String[] { "1" }, MovieContract.Movie.ID);
        } else if (pref.equalsIgnoreCase(TOPRATED_PREF_VALUE)) {
            cursorLoader = new CursorLoader(MainActivity.this, MovieContract.URI_TABLE, projection, MovieContract.Movie.ISTOP + "=?",
                            new String[] { "1" }, MovieContract.Movie.ID);
        } else if (pref.equalsIgnoreCase(FAV_PREF_VALUE)) {
            cursorLoader = new CursorLoader(MainActivity.this, MovieContract.URI_TABLE, projection, MovieContract.Movie.ISFAV + "=?",
                            new String[] { "1" }, MovieContract.Movie.ID);
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Cursor cursor) {
        if(mMovieListAdapter == null) {
            mMovieListAdapter = new MoviesListCursorAdapter(this, cursor);
        } else {
            mMovieListAdapter.swapCursor(cursor);
        }
        gridview.setAdapter(mMovieListAdapter);
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {
        if (mMovieListAdapter != null) {
            mMovieListAdapter.swapCursor(null);
        }
    }

    private void startFetchMoviesService(FetchMoviesService.enum_Movie_Type type) {
        Intent intent = new Intent(this,FetchMoviesService.class);
        intent.putExtra("receiver", mReceiver);
        intent.putExtra("requestId", 101);
        switch (type){
            case POPULAR_MOVIE_TYPE:
                intent.setAction(FetchMoviesService.FETCH_POPULAR_MOVIES);
                startService(intent);
                break;
            case TOPRATED_MOVIE_TYPE:
                intent.setAction(FetchMoviesService.FETCH_TOPRATED_MOVIES);
                startService(intent);
                break;
            case FAVORITE_MOVIE_TYPE:
                intent.setAction(FetchMoviesService.FETCH_FAV_MOVIES);
                startService(intent);
                break;
            case DEFAULT:
                intent.setAction(FetchMoviesService.DEFAULT_MODE);
                startService(intent);
                break;
        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case FetchMoviesService.STATUS_FINISHED:
                getSupportLoaderManager().initLoader(1, null, MainActivity.this);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if(resultCode == Activity.RESULT_OK){
                getSupportLoaderManager().restartLoader(1, null, MainActivity.this);
            }
        }
    }
}