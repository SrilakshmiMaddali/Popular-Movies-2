package com.sm.popularmovies_stage1.ui;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.sm.popularmovies_stage1.BuildConfig;
import com.sm.popularmovies_stage1.R;
import com.sm.popularmovies_stage1.database.MovieContract;
import com.sm.popularmovies_stage1.model.CustomAdapter;
import com.sm.popularmovies_stage1.model.MoviedbService;
import com.sm.popularmovies_stage1.model.Movies;
import com.sm.popularmovies_stage1.model.PopularMoviesDto;
import com.sm.popularmovies_stage1.model.RetrofitClientInstance;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    GridView gridview;
    Context mContext;
    CustomAdapter mCustomAdapter;
    MenuItem top;
    MenuItem pop;
    MenuItem fav;
    private static final String API_KEY = BuildConfig.API_KEY;
    private static final String TAG = "MainActivity";
    List<Movies> mMoviesList;
    ContentResolver mContentResolver;
    Uri CONTACT_URI = MovieContract.URI_TABLE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridview = findViewById(R.id.gridview);
        mContext = this;
        getSupportLoaderManager().initLoader(1, null, this);
        getMovieList(getPopularMoviesCall());
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class);
                intent.putExtra(MovieDetailsActivity.EXTRA_MOVIE, mMoviesList.get(position));
                startActivity(intent);
            }
        });
        generateDataList();
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
                getMovieList(getPopularMoviesCall());
                top.setVisible(true);
                pop.setVisible(false);
                invalidateOptionsMenu();
                setTitle(R.string.popular_movies);
                break;
            // action with ID action_settings was selected
            case R.id.top_movies:
                getMovieList(getTopRatedMovieCall());
                top.setVisible(false);
                pop.setVisible(true);
                setTitle(R.string.top_movies);
                break;
            case R.id.fv_movies:
                createFavoriteList();
                setTitle(R.string.favorite_movies);
                break;
            default:
                break;
        }

        return true;
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
                    Toast.makeText(MainActivity.this, R.string.something_wrong, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PopularMoviesDto> call, @NonNull Throwable t) {
                //progressDialog.dismiss();
                Toast.makeText(MainActivity.this, R.string.something_wrong, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void generateDataList(List<Movies> moviesList) {
        mCustomAdapter = new CustomAdapter(mContext, moviesList);
        gridview.setAdapter(mCustomAdapter);
        gridview.invalidateViews();
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable Bundle args) {
        //(@NonNull Context context, @NonNull Uri uri, @Nullable String[] projection,
          //      @Nullable String selection, @Nullable String[] selectionArgs,
            //    @Nullable String sortOrder)
        CursorLoader cursorLoader = new CursorLoader(MainActivity.this, CONTACT_URI, null, null, null,MovieContract.Movie.ID);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Cursor cursor) {
        cursor.moveToFirst();
        adapter = new CustomContactAdapter(this, cursor);
        lstContact.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }
}