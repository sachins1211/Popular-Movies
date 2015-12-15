package com.example.sachin.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;


public class movieDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageView backdrop = (ImageView) findViewById(R.id.backdropView);
        ImageView poster = (ImageView) findViewById(R.id.posterView);
        TextView titleView = (TextView) findViewById(R.id.titleView);
        TextView releasedView = (TextView) findViewById(R.id.releasedView);
        TextView starText = (TextView) findViewById(R.id.starText);
        TextView overviewText = (TextView) findViewById(R.id.overviewView);

        try {
            JSONObject movieJSON;

            movieJSON = new JSONObject(getIntent().getStringExtra(TmdbApi.JSON_TAG));

            final String backdropURI = movieJSON.getString(TmdbApi.JSON_BACKDROP_PATH);
            final String posterURI = movieJSON.getString(TmdbApi.JSON_POSTER_PATH);

            Picasso.with(this)
                    .load(TmdbApi.IMG_BASE_URL + TmdbApi.FULL_IMG_SIZE + backdropURI)
                    .fit()
                    .into(backdrop);

            Picasso.with(this)
                    .load(TmdbApi.IMG_BASE_URL + TmdbApi.FULL_IMG_SIZE + posterURI)
                    .fit()
                    .into(poster);



            titleView.setText(movieJSON.getString(TmdbApi.JSON_MOVIE_TITLE));
            releasedView.setText((movieJSON.getString(TmdbApi.JSON_MOVIE_RELEASE_DATE)));
            starText.setText(movieJSON.getString(TmdbApi.JSON_MOVIE_VOTE_AVERAGE)+ "/10");
            overviewText.setText(movieJSON.getString(TmdbApi.JSON_MOVIE_OVERVIEW));
            getSupportActionBar().setTitle(movieJSON.getString(TmdbApi.JSON_MOVIE_TITLE));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

}
