package com.example.sachin.popularmovies;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sachin.popularmovies.adapter.movieGeneralAdapter;
import com.example.sachin.popularmovies.API_KEY.ApiKey;
import com.example.sachin.popularmovies.database.favouritesSqliteHelper;
import com.example.sachin.popularmovies.modal.Results;
import com.example.sachin.popularmovies.modal.movieGeneral;
import com.example.sachin.popularmovies.modal.movieGeneralModal;
import com.example.sachin.popularmovies.network.MovieAPI;
import com.example.sachin.popularmovies.network.NetworkAPI;



import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class movieListActivity extends AppCompatActivity {
    final CharSequence[] items = {" Most Popular ", " Highest Rated ", " My Favourites "};
    private final String MOST_POPULAR = "popularity.desc";
    private final String HIGHLY_RATED = "vote_count.desc";
    View recyclerView;
    private AlertDialog choice;
    private String FLAG_CURRENT = MOST_POPULAR;
    private String FLAG_FAV = "FAVOURITE";
    private TextView errorTextView;
    private ImageView errorImageview;
    private Integer page=1;


    private boolean mTwoPane;
    private movieGeneral mMoviegeneralData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);


        recyclerView = findViewById(R.id.movie_list);
        errorImageview = (ImageView) findViewById(R.id.errimg);
        errorTextView = (TextView) findViewById(R.id.errtext);

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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort_popularity) {
            FetchMovie((RecyclerView) recyclerView, MOST_POPULAR);
            return true;
        }
        else if (id == R.id.action_sort_rating){
            FetchMovie((RecyclerView) recyclerView, HIGHLY_RATED);
            return true;
        }
        else if (id == R.id.action_favorite){
            FetchMovie((RecyclerView) recyclerView, FLAG_FAV);
        }

        return super.onOptionsItemSelected(item);

    }

    protected void FetchFavourites(@NonNull final RecyclerView recyclerView) {
        favouritesSqliteHelper db = new favouritesSqliteHelper(getApplicationContext());
        List<movieGeneralModal> movieGeneralModals = db.getAllMovies();
        if (movieGeneralModals.size() > 0)
            attachAdapter(recyclerView, movieGeneralModals);
        else {
            Toast.makeText(getApplicationContext(), "No Favourites Added!", Toast.LENGTH_SHORT).show();
        }
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
        int width = displaymetrics.widthPixels;
        int number;
        if (!mTwoPane) {
            number = width / 250;
        } else {
            number = (width / 2) / 250;
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
        List<movieGeneralModal> movieGeneralModals = new ArrayList<movieGeneralModal>();
        Results[] mResult = mMoviegeneral.getResults();
        for (Results result : mResult) {
            movieGeneralModal obj = new movieGeneralModal(result.getTitle(), result.getPoster_path(), result.getVote_average()
                    , result.getId(), result.getVote_count(), result.getRelease_date(), result.getOverview());
            movieGeneralModals.add(obj);
        }
        if (mResult.length > 0) {
            attachAdapter(recyclerView, movieGeneralModals);
        } else {
            errorImageview.setVisibility(View.VISIBLE);
            errorTextView.setVisibility(View.VISIBLE);
        }
    }

    private void FetchMovie(@NonNull final RecyclerView recyclerView, String temp) {

        errorImageview.setVisibility(View.INVISIBLE);
        errorTextView.setVisibility(View.INVISIBLE);
        errorTextView.setText("\t\tSorry!\nNetwork Error!");

        FLAG_CURRENT = temp;
        if (FLAG_CURRENT != FLAG_FAV) {
            MovieAPI mMovieAPI = NetworkAPI.createService(MovieAPI.class);
            mMovieAPI.fetchMovies(FLAG_CURRENT, ApiKey.TMDB_API_KEY, "en",page, new Callback<movieGeneral>() {
                @Override
                public void success(movieGeneral mMoviegeneral, Response response) {
                    mMoviegeneralData = mMoviegeneral;
                    drawLayout(recyclerView, mMoviegeneral);
                }

                @Override
                public void failure(RetrofitError error) {
                    errorImageview.setVisibility(View.VISIBLE);
                    errorTextView.setVisibility(View.VISIBLE);
                }
            });
        } else {
            FetchFavourites(recyclerView);
        }
    }


}
