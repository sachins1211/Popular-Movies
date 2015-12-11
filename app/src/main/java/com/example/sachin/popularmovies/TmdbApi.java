package com.example.sachin.popularmovies;


public class TmdbApi {


    public static final String API_KEY = "bc7bb987b24df9d7a6ee6dd0274e7abb";


    public static String SORT_BY = "popularity.desc";
    public static final int MIN_VOTES = 250;
    public static final String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
    public static final String IMG_BASE_URL = "http://image.tmdb.org/t/p/";
    public static final String SMALL_IMG_SIZE = "w342/";
    public static final String JSON_RESULTS_ARRAY = "results";
    public static final String JSON_POSTER_PATH = "poster_path";
}
