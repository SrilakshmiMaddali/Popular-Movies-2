package com.sm.popularmovies_stage1.ui;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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
    private static final String API_KEY = BuildConfig.API_KEY;
    private static final String TAG = "MainActivity";
    List<Movies> mMoviesList;
    ContentResolver mContentResolver;
    DataDownloadResultReceiver mReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mContentResolver = MainActivity.this.getContentResolver();
        mReceiver = new DataDownloadResultReceiver(new Handler());
        gridview = findViewById(R.id.gridview);
        mReceiver.setReceiver(this);
        startFetchMoviesService(FetchMoviesService.enum_Movie_Type.POPULAR_MOVIE_TYPE);
        //initializeContentLoader();
    }

    @Override
    protected void onResume(){
        super.onResume();
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

        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.popular_movies:
                startFetchMoviesService(FetchMoviesService.enum_Movie_Type.POPULAR_MOVIE_TYPE);
                top.setVisible(true);
                pop.setVisible(false);
                invalidateOptionsMenu();
                setTitle(R.string.popular_movies);
                break;
            // action with ID action_settings was selected
            case R.id.top_movies:
                startFetchMoviesService(FetchMoviesService.enum_Movie_Type.TOPRATED_MOVIE_TYPE);
                top.setVisible(false);
                pop.setVisible(true);
                setTitle(R.string.top_movies);
                break;
            case R.id.fv_movies:
                //FetchMoviesService.createFavoriteList();
                setTitle(R.string.favorite_movies);
                break;
            default:
                break;
        }
        return true;
    }

   private void initializeContentLoader() {
        // array of database column names
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
        // array of views to display database values
        int[] viewIds = new int[] {
                R.id.poster};
        Cursor cursor = mContentResolver.query(MovieContract.URI_TABLE, projection,null, null,null );
        // CursorAdapter to load data from the Cursor into the ListView
        mMovieListAdapter = new MoviesListCursorAdapter(this, cursor);
        gridview.setAdapter(mMovieListAdapter);
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable Bundle args) {
        //(@NonNull Context context, @NonNull Uri uri, @Nullable String[] projection,
          //      @Nullable String selection, @Nullable String[] selectionArgs,
            //    @Nullable String sortOrder)
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
        //CursorLoader cursorLoader = new CursorLoader(MainActivity.this, MovieContract.URI_TABLE, new String[]{MovieContract.Movie.POSTER_PATH},MovieContract.Movie.ISPOP+"=?", new String[] {"1"},MovieContract.Movie.ID);
        //CursorLoader cursorLoader = new CursorLoader(MainActivity.this, MovieContract.URI_TABLE, null,null, null,null);
        CursorLoader cursorLoader = new CursorLoader(MainActivity.this, MovieContract.URI_TABLE,null,null, null,MovieContract.Movie.ID );


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
        mMovieListAdapter.swapCursor(null);
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
        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case FetchMoviesService.STATUS_FINISHED:
                getSupportLoaderManager().initLoader(1, null, MainActivity.this);
                gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class);
                        intent.putExtra(MovieDetailsActivity.EXTRA_MOVIE, mMoviesList.get(position));
                        startActivity(intent);
                    }
                });
                break;
        }
    }
}