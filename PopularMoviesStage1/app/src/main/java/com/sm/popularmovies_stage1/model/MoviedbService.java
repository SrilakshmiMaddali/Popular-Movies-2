package com.sm.popularmovies_stage1.model;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MoviedbService {

    @GET("movie/popular")
    Call<PopularMoviesDto> getPopularMovies(@Query("api_key") String apiKey);

    @GET("movie/top_rated")
    Call<PopularMoviesDto> getTopRatedMovies(@Query("api_key") String apiKey);

    @GET("movie/{taskId}/videos")
    Call<VideoDataDto> getVideosList(@Path("taskId") String taskId, @Query("api_key") String apiKey);

    @GET("movie/{taskId}/reviews")
    Call<PopularMoviesDto> getReviewsList(@Path("taskId") String taskId, @Query("api_key") String apiKey);
}