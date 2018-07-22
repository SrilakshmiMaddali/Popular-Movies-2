package com.sm.popularmovies_stage1.ui;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

import com.sm.popularmovies_stage1.BuildConfig;
import com.sm.popularmovies_stage1.R;
import com.sm.popularmovies_stage1.database.MovieContract;
import com.sm.popularmovies_stage1.model.MoviedbService;
import com.sm.popularmovies_stage1.model.Movies;
import com.sm.popularmovies_stage1.model.RetrofitClientInstance;
import com.sm.popularmovies_stage1.model.Review;
import com.sm.popularmovies_stage1.model.ReviewAdapter;
import com.sm.popularmovies_stage1.model.ReviewDataDto;
import com.sm.popularmovies_stage1.model.TrailerAdapter;
import com.sm.popularmovies_stage1.model.TrailerVideo;
import com.sm.popularmovies_stage1.model.VideoDataDto;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

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
    RecyclerView.Adapter mReviewAdapter;
    Button mAddToFavorite;
    ListView mTrailerList;
    RecyclerView mReviewsRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
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
        mReviewsRecyclerView = (RecyclerView) findViewById(R.id.review_list);
        mReviewsRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mReviewsRecyclerView.setLayoutManager(layoutManager);
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
                        if (moviePosterImage != null) {
                            moviePosterImage.setVisibility(View.INVISIBLE);
                        }
                        //mProgressBar.setVisibility(View.GONE);
                        if (movieTitle != null) {
                            movieTitle.setText(getString(R.string.detail_image_error_message));
                            movieTitle.setVisibility(View.VISIBLE);
                        }
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
        movieTitle.setText(movie.getmTitle());
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
        mReviewsRecyclerView.setAdapter(mReviewAdapter);
        mReviewsRecyclerView.invalidate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // relase all resources.
        moviePosterImage = null;
        movieTitle = null;
    }

    View.OnClickListener mAddToFavoriteListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ContentValues values = new ContentValues();
            values.put(MovieContract.Movie.ISFAV, "1");
            mContentResolver.update(MovieContract.Movie.buildMovieUri(mUserElectedMovie.getmId()), values, null, null);
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK,returnIntent);
            updateFavoriteUI();
        }
    };

    View.OnClickListener mRemoveFavoriteListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            ContentValues values = new ContentValues();
            values.putNull(MovieContract.Movie.ISFAV);
            mContentResolver.update(MovieContract.Movie.buildMovieUri(mUserElectedMovie.getmId()), values, null, null);
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK,returnIntent);
            updateFavoriteUI();
        }
    };

    private boolean isFavoriteCheck() {
        // Retrieve movies records
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
        Uri moviesuri = MovieContract.URI_TABLE;
        Cursor favCursor = mContentResolver.query(MovieContract.Movie.buildMovieUri(mUserElectedMovie.getmId()), projection, null,null,
                        null);
        if (favCursor != null && favCursor.moveToFirst()) {
            int i = favCursor.getColumnIndex(MovieContract.Movie.ISFAV);
                String isFav = favCursor.getString(i);
                if ( isFav != null && isFav.equals("1")) {
                    return true;
                }
        }
        return false;
    }


}