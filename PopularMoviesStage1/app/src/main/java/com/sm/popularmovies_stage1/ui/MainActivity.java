package com.sm.popularmovies_stage1.ui;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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

public class MainActivity extends AppCompatActivity {

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridview = findViewById(R.id.gridview);
        mContext = this;
        mContentResolver = MainActivity.this.getContentResolver();
        getMovieList(getPopularMoviesCall());
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class);
                intent.putExtra(MovieDetailsActivity.EXTRA_MOVIE, mMoviesList.get(position));
                startActivity(intent);
            }
        });
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
        mMoviesList = entries;
        generateDataList(mMoviesList);
    }
    private void generateDataList(List<Movies> moviesList) {
        mCustomAdapter = new CustomAdapter(mContext, moviesList);
        gridview.setAdapter(mCustomAdapter);
        gridview.invalidateViews();
    }
}