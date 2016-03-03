package com.sachin.popularmovies;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import android.widget.ProgressBar;
import android.widget.Toast;
import android.support.v7.widget.SearchView;
import com.sachin.popularmovies.adapter.movieGeneralAdapter;
import com.sachin.popularmovies.API_KEY.ApiKey;
import com.sachin.popularmovies.database.MoviesDBHelper;
import com.sachin.popularmovies.modal.Results;
import com.sachin.popularmovies.modal.movieGeneral;
import com.sachin.popularmovies.modal.movieGeneralModal;
import com.sachin.popularmovies.network.MovieAPI;
import com.sachin.popularmovies.network.NetworkAPI;


import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends AppCompatActivity {
    final CharSequence[] items = {" Most Popular ", " Highest Rated ", " My Favourites "};
    private final String MOST_POPULAR = "popularity.desc";
    private final String HIGHLY_RATED = "vote_count.desc";
    // private final String HIGHLY_RATED = "vote_average.desc";//&vote_count.gte=100
    View recyclerView;
    private AlertDialog choice;
    public String FLAG_CURRENT = MOST_POPULAR;
    private String FLAG_FAV = "FAVOURITE";
    private Integer page;
    private List<movieGeneralModal> movieGeneralModals = new ArrayList<movieGeneralModal>();
    private List<movieGeneralModal> movieGeneralModals2 = new ArrayList<movieGeneralModal>();
    private boolean mTwoPane;
    private movieGeneral mMoviegeneralData;
    private int checkItem=0;
    private ProgressBar layout;
    private List<movieGeneralModal> movieGeneralModals3 = new ArrayList<movieGeneralModal>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        setContentView(R.layout.activity_main);
        movieGeneralModals.clear();
        movieGeneralModals2.clear();
        movieGeneralModals3.clear();
        page = 1;


        recyclerView = findViewById(R.id.movie_list);



        assert recyclerView != null;

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
        }

        if (savedInstanceState == null)
            FetchMovie((RecyclerView) recyclerView, FLAG_CURRENT);
        else {
            if (savedInstanceState.getString("CURRENT") == FLAG_FAV) {
                FetchMovie((RecyclerView) recyclerView, FLAG_FAV);
            } else if (savedInstanceState.getSerializable("adapter") != null) {
                mMoviegeneralData = (movieGeneral) savedInstanceState.getSerializable("adapter");
                drawLayout((RecyclerView) recyclerView, mMoviegeneralData);
            } else {
                FetchMovie((RecyclerView) recyclerView, FLAG_CURRENT);
            }

        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
       final MenuItem myActionMenuItem = menu.findItem( R.id.action_search);
       final SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setQueryHint("Search Movies...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!searchView.isIconified()) {
                    searchView.setIconified(true);
                    queryTMDb(query);
                }
                myActionMenuItem.collapseActionView();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu:
                showChoices();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    private void showChoices() {

        choice = new AlertDialog.Builder(this)
                .setSingleChoiceItems(items, checkItem , new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:
                                FetchMovie((RecyclerView) recyclerView, MOST_POPULAR);
                                checkItem=0;
                                break;
                            case 1:
                                FetchMovie((RecyclerView) recyclerView, HIGHLY_RATED);
                                checkItem=1;
                                break;
                            case 2:
                                FetchMovie((RecyclerView) recyclerView, FLAG_FAV);
                                checkItem=2;
                                break;
                        }
                        choice.dismiss();
                    }
                }).setInverseBackgroundForced(true).setTitle("  Choose  ").setIcon(R.drawable.sort2)
                .show();
    }



    protected void FetchFavourites(@NonNull final RecyclerView recyclerView) {
        MoviesDBHelper db = new MoviesDBHelper(getApplicationContext());
        List<movieGeneralModal> movieGeneralModals = db.getAllMovies();
        if (movieGeneralModals.size() > 0)
            attachAdapter(recyclerView, movieGeneralModals);

        else {
            Toast.makeText(getApplicationContext(), "No Favourites Added!", Toast.LENGTH_LONG).show();
            FetchMovie((RecyclerView) recyclerView, MOST_POPULAR);
            checkItem=0;
        }
        layout.setVisibility(View.GONE);

    }


    protected void getPaneChanges() {
        mTwoPane = findViewById(R.id.movie_detail_container) != null;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
        getPaneChanges();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("adapter", mMoviegeneralData);
        outState.putString("CURRENT", FLAG_CURRENT);

    }

    private void attachAdapter(@NonNull final RecyclerView recyclerView, List<movieGeneralModal> movieGeneralModals) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float density  = getResources().getDisplayMetrics().density;
        float width = displaymetrics.widthPixels/density;

        int number;


        if (!mTwoPane) {
            number = Math.round(width/190);
        } else {
            number = Math.round(width/(2*200));
        }
        GridLayoutManager lLayout = new GridLayoutManager(getApplicationContext(), number);
        RecyclerView rView = recyclerView;
        rView.setHasFixedSize(true);
        rView.setLayoutManager(lLayout);
        FragmentManager fm = getSupportFragmentManager();
        movieGeneralAdapter mMovieGeneralAdapter = new movieGeneralAdapter(getApplicationContext(), movieGeneralModals, mTwoPane, fm);
        rView.setAdapter(mMovieGeneralAdapter);


    }

    private void drawLayout(@NonNull final RecyclerView recyclerView, movieGeneral mMoviegeneral) {
        Results[] mResult = mMoviegeneral.getResults();
        if (FLAG_CURRENT == MOST_POPULAR) {
            for (Results result : mResult) {
                movieGeneralModal obj = new movieGeneralModal(result.getTitle(), result.getPoster_path(), result.getBackdrop_path(), result.getVote_average()
                        , result.getId(), result.getVote_count(), result.getRelease_date(), result.getOverview());
                movieGeneralModals.add(obj);
            }
        } else {
            for (Results result : mResult) {
                movieGeneralModal obj = new movieGeneralModal(result.getTitle(), result.getPoster_path(), result.getBackdrop_path(), result.getVote_average()
                        , result.getId(), result.getVote_count(), result.getRelease_date(), result.getOverview());
                movieGeneralModals2.add(obj);
            }

        }


        if (mResult.length > 0) {
            if (FLAG_CURRENT == MOST_POPULAR) {
                attachAdapter(recyclerView, movieGeneralModals);
            } else {
                attachAdapter(recyclerView, movieGeneralModals2);

            }

        }

    }


    private void FetchMovie(@NonNull final RecyclerView recyclerView, String temp) {

        FLAG_CURRENT = temp;
       layout= (ProgressBar) findViewById(R.id.progressBar);
        layout.setVisibility(View.VISIBLE);

        if (FLAG_CURRENT != FLAG_FAV) {

            MovieAPI mMovieAPI = NetworkAPI.createService(MovieAPI.class);
            for (int i = 0; i < 5; i++) {

                mMovieAPI.fetchMovies(FLAG_CURRENT, ApiKey.TMDB_API_KEY, "en", page, new Callback<movieGeneral>() {
                    @Override
                    public void success(movieGeneral mMoviegeneral, Response response) {
                        mMoviegeneralData = mMoviegeneral;
                        drawLayout(recyclerView, mMoviegeneralData);
                        layout.setVisibility(View.GONE);

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        isOnline();
                        //do nothing
                    }
                });
                page += 1;
                isOnline();
            }
        } else {
            FetchFavourites(recyclerView);
        }
    }

    public void isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
       if(activeNetwork==null){
            Snackbar.make(recyclerView, "Please Connect to the Internet", Snackbar.LENGTH_LONG)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (FLAG_CURRENT == MOST_POPULAR) {
                                FetchMovie((RecyclerView) recyclerView, MOST_POPULAR);
                            } else {
                                FetchMovie((RecyclerView) recyclerView, HIGHLY_RATED);
                            }
                        }
                    })
                    .setDuration(Snackbar.LENGTH_INDEFINITE)
                    .show();
        }


    }

    @Override
    public void startActivityFromFragment(Fragment fragment, Intent intent, int requestCode) {
        //super.startActivityFromFragment(fragment, intent, requestCode);
        FetchMovie((RecyclerView) recyclerView, FLAG_FAV);
    }


    public void queryTMDb(String query) {
        query = query.replaceAll(" ", "%20");
        layout= (ProgressBar) findViewById(R.id.progressBar);
        layout.setVisibility(View.VISIBLE);

        MovieAPI mMovieAPI = NetworkAPI.createService(MovieAPI.class);
        mMovieAPI.searchMovie(ApiKey.TMDB_API_KEY,query,new Callback<movieGeneral>() {
            @Override
            public void success(movieGeneral mMoviegeneral, Response response) {
                mMoviegeneralData = mMoviegeneral;
                Results[] mResult1 = mMoviegeneral.getResults();
                for (Results result : mResult1) {
                    movieGeneralModal obj = new movieGeneralModal(result.getTitle(), result.getPoster_path(), result.getBackdrop_path(), result.getVote_average()
                            , result.getId(), result.getVote_count(), result.getRelease_date(), result.getOverview());
                    movieGeneralModals3.add(obj);
                }


                if (mResult1.length > 0) {
                    attachAdapter((RecyclerView) recyclerView, movieGeneralModals3);

                }
                else{
                    Toast.makeText(getBaseContext(),"No Movies Found!",Toast.LENGTH_LONG).show();
                }


                layout.setVisibility(View.GONE);

            }

            @Override
            public void failure(RetrofitError error) {
                isOnline();
                //do nothing
            }
        });

        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }


    }
}






