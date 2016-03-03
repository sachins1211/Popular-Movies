package com.sachin.popularmovies.network;

import com.sachin.popularmovies.modal.movieGeneral;
import com.sachin.popularmovies.modal.review.movieReview;
import com.sachin.popularmovies.modal.trailer.movieYoutubeModal;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;


public interface MovieAPI {


    @GET("/3/discover/movie")
    void fetchMovies(
            @Query("sort_by") String mSort,
            @Query("api_key") String mApiKey,
            @Query("language") String lang,
            @Query("page") Integer page,
            Callback<movieGeneral> cb
    );




    @GET("/3/movie/{id}/reviews")
    void fetchReview(
            @Query("api_key") String mApiKey,
            @Path("id") String id,
            Callback<movieReview> cb
    );

    @GET("/3/movie/{id}/videos")
    void fetchVideos(
            @Query("api_key") String mApiKey,
            @Path("id") String id,
            Callback<movieYoutubeModal> cb
    );

    @GET("/3/search/movie")
    void searchMovie(
            @Query("api_key") String mApiKey,
            @Query("query") String mquery,

            Callback<movieGeneral> callback);

}
