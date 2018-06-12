package com.sm.popularmovies_stage1.ui;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

import com.sm.popularmovies_stage1.BuildConfig;
import com.sm.popularmovies_stage1.R;
import com.sm.popularmovies_stage1.database.MovieContract;
import com.sm.popularmovies_stage1.model.MoviedbService;
import com.sm.popularmovies_stage1.model.Movies;
import com.sm.popularmovies_stage1.model.PopularMoviesDto;
import com.sm.popularmovies_stage1.model.RetrofitClientInstance;
import com.sm.popularmovies_stage1.model.Review;
import com.sm.popularmovies_stage1.model.ReviewAdapter;
import com.sm.popularmovies_stage1.model.ReviewDataDto;
import com.sm.popularmovies_stage1.model.TrailerAdapter;
import com.sm.popularmovies_stage1.model.TrailerVideo;
import com.sm.popularmovies_stage1.model.VideoDataDto;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Details view page, to display movie details.
 */
public class MovieDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE = "extra_movie";
    protected static final String NA = "Not Available";
    TextView movieTitle;
    ImageView moviePosterImage;
    List<TrailerVideo> mTrailerVideoList;
    List<Review> mReviewsList;
    TrailerAdapter mTrailerAdapter;
    ReviewAdapter mReviewAdapter;
    Button mAddToFavorite;
    ListView mTrailerList;
    ListView mReviewsListView;
    Context mContext;
    Movies mUserElectedMovie;
    boolean isFavorite = false;
    ContentResolver mContentResolver;
    private static final String TAG = "MovieDetailsActivity";


    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detailed_activity);
        ButterKnife.bind(this);
        mContext = this;
        moviePosterImage = (ImageView) findViewById(R.id.movie_poster);
        movieTitle = (TextView) findViewById(R.id.title);
        mTrailerList = (ListView) findViewById(R.id.trailer_list);
        mTrailerList.setNestedScrollingEnabled(true);
        mReviewsListView = (ListView) findViewById(R.id.review_list);
        mReviewsListView.setNestedScrollingEnabled(true);
        mAddToFavorite = (Button) findViewById(R.id.add_to_favorite_tv);
        mContentResolver = MovieDetailsActivity.this.getContentResolver();
        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }
        // get parcelable data from intent.
        mUserElectedMovie = (Movies) intent.getParcelableExtra(EXTRA_MOVIE);
        if (mUserElectedMovie == null) {
            // EXTRA_POSITION not found in intent
            closeOnError();
            return;
        }

        // initiate resources.
        populateUI(mUserElectedMovie);
        Picasso.with(this)
                .load(getPath(mUserElectedMovie.getmPosterPath()))
                .fit()
                .into(moviePosterImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        // image loading finished, so remove progressbar.
                        //mProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        // On error , while loading sandwich image, display error message.
                        moviePosterImage.setVisibility(View.INVISIBLE);
                        //mProgressBar.setVisibility(View.GONE);
                        movieTitle.setText(getString(R.string.detail_image_error_message));
                        movieTitle.setVisibility(View.VISIBLE);
                    }
                });

        getReviewsList(getReviewsDetails());
        getMovieList(getVideoTrailerUrl());

        setTitle(mUserElectedMovie.getmOriginalTitle());
    }

    protected void onResume() {
        super.onResume();
        updateFavoriteUI();
    }
    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

    private void updateFavoriteUI() {
        isFavorite = isFavoriteCheck();
        if(isFavorite) {
            mAddToFavorite.setText(getString(R.string.removefavoritebtn));
            mAddToFavorite.setOnClickListener(mRemoveFavoriteListener);
        } else {
            mAddToFavorite.setText(getString(R.string.addfavoritebtn));
            mAddToFavorite.setOnClickListener(mAddToFavoriteListener);
        }
    }
    // Prepares poster url.
    private String getPath(String posterPath) {
        String url = "http://image.tmdb.org/t/p/" + "w500/" + posterPath;
        return url;
    }

    private void populateUI(@NonNull Movies movie) {
        // Load views, from model object
       // mProgressBar = (ProgressBar) findViewById(R.id.loadingprogress);
        //mProgressBar.setVisibility(View.VISIBLE);

        TextView originalTitleTextView = (TextView) findViewById(R.id.original_title_tv);
        String originalTitle = movie.getmOriginalTitle();
        if (!originalTitle.isEmpty()) {
            originalTitleTextView.setText(originalTitle);
        } else {
            originalTitleTextView.setText(NA);
        }

        TextView releaseDateTextView = (TextView) findViewById(R.id.release_date_tv);
        String releaseDate = movie.getmReleaseDate();
        if (!releaseDate.isEmpty()) {
            releaseDateTextView.setText(releaseDate);
        } else {
            releaseDateTextView.setText(NA);
        }

        RatingBar userRatingTextView = (RatingBar) findViewById(R.id.user_rating_tv);
        String userRating = movie.getmPopularity();
        if (!userRating.isEmpty()) {
            userRatingTextView.setRating(Float.parseFloat(userRating));
        } else {
            userRatingTextView.setRating(0);
        }

        TextView movieOverview = (TextView)findViewById(R.id.movie_synopsis_tv);
        String overview = movie.getmOverview();
        if (!overview.isEmpty()) {
            movieOverview.setText(overview);
        } else {
            movieOverview.setText(NA);
        }
    }

    private void getMovieList(Call<VideoDataDto> call) {

        call.enqueue(new retrofit2.Callback<VideoDataDto>() {
            @Override
            public void onResponse(@NonNull Call<VideoDataDto> call, @NonNull Response<VideoDataDto> response) {
                //progressDoalog.dismiss();
                if (response != null && response.body() != null) {
                    mTrailerVideoList = response.body().getResults();
                    generateDataList(mTrailerVideoList);
                } else {
                    Log.d(TAG, "null Popular movies response.");
                    Toast.makeText(MovieDetailsActivity.this, R.string.something_wrong, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<VideoDataDto> call, @NonNull Throwable t) {
                //progressDialog.dismiss();
                Toast.makeText(MovieDetailsActivity.this, R.string.something_wrong, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getReviewsList(Call<ReviewDataDto> call) {
        call.enqueue(new retrofit2.Callback<ReviewDataDto>() {
            @Override
            public void onResponse(Call<ReviewDataDto> call, Response<ReviewDataDto> response) {
                if (response != null && response.body() != null) {
                    mReviewsList = response.body().getResults();
                    createReviewsList(mReviewsList);
                }
            }

            @Override
            public void onFailure(Call<ReviewDataDto> call, Throwable t) {
                Toast.makeText(MovieDetailsActivity.this, R.string.something_wrong, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Call<VideoDataDto> getVideoTrailerUrl() {
        MoviedbService service = RetrofitClientInstance.getRetrofitInstance().create(MoviedbService.class);
        return service.getVideosList(mUserElectedMovie.getmId(),BuildConfig.API_KEY);
    }

    private Call<ReviewDataDto> getReviewsDetails() {
        MoviedbService service = RetrofitClientInstance.getRetrofitInstance().create(MoviedbService.class);
        return service.getReviewsList(mUserElectedMovie.getmId(),BuildConfig.API_KEY);
    }

    private void generateDataList(List<TrailerVideo> moviesList) {
        mTrailerAdapter = new TrailerAdapter(mContext, mTrailerVideoList);
        mTrailerList.setAdapter(mTrailerAdapter);
        mTrailerList.invalidateViews();
    }

    private void createReviewsList(List<Review> reviewsList) {
        mReviewAdapter = new ReviewAdapter(mContext, reviewsList);
        mReviewsListView.setAdapter(mReviewAdapter);
        mReviewsListView.invalidate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // relase all resources.
        moviePosterImage = null;
        movieTitle = null;
        //mProgressBar = null;
    }

    View.OnClickListener mAddToFavoriteListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ContentValues values = new ContentValues();
            values.put(MovieContract.Movie.ID, mUserElectedMovie.getmId());
            values.put(MovieContract.Movie.ORIGINAL_TITLE, mUserElectedMovie.getmOriginalTitle());
            values.put(MovieContract.Movie.POPULARITY, mUserElectedMovie.getmPopularity());
            values.put(MovieContract.Movie.OVERVIEW, mUserElectedMovie.getmOverview());
            values.put(MovieContract.Movie.VIDEO, mUserElectedMovie.getmVideo());
            values.put(MovieContract.Movie.VOTE_AVERAGE, mUserElectedMovie.getmVoteAverage());
            values.put(MovieContract.Movie.VOTE_COUNT, mUserElectedMovie.getmVoteCount());
            values.put(MovieContract.Movie.TITLE, mUserElectedMovie.getmTitle());
            values.put(MovieContract.Movie.POSTER_PATH, mUserElectedMovie.getmPosterPath());
            values.put(MovieContract.Movie.ORIGINAL_LANGUAGE, mUserElectedMovie.getmOriginalLanguage());
            values.put(MovieContract.Movie.BACKDROP_PATH, mUserElectedMovie.getmBackdropPath());
            values.put(MovieContract.Movie.RELEASE_DATE, mUserElectedMovie.getmReleaseDate());
            Uri returned = mContentResolver.insert(MovieContract.URI_TABLE, values);
            updateFavoriteUI();
            Log.d(TAG, "record id returned is " + returned.toString());
        }
    };

    View.OnClickListener mRemoveFavoriteListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Uri uri = MovieContract.Movie.buildMovieUri(mUserElectedMovie.getmId());
            mContentResolver.delete(uri, null, null);
            updateFavoriteUI();
        }
    };

    private boolean isFavoriteCheck() {
        // Retrieve movies records

        Uri moviesuri = MovieContract.URI_TABLE;
        Cursor cursor = managedQuery(moviesuri, null, null, null, MovieContract.Movie.ID);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndex(MovieContract.Movie.ID));
                String title = cursor.getString(cursor.getColumnIndex(MovieContract.Movie.TITLE));
                if ( title.equals(mUserElectedMovie.getmTitle())) {
                    return true;
                }
            }
        }
        return false;
    }


}