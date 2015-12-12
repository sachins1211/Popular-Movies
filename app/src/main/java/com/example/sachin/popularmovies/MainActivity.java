package com.example.sachin.popularmovies;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    GridView gridView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    gridAdapter gridAdapter;
    JSONArray main;

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (isNetworkAvailable()){
        gridView = (GridView) findViewById(R.id.gridView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        gridAdapter = new gridAdapter(this);

        gridView.setAdapter(gridAdapter);
        update();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                update();
            }
        });
        }

        else{
            new AlertDialog.Builder(this)
                    .setTitle("No Internet")
                    .setMessage("Please Connect To Internet").setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    public class fetchData extends AsyncTask<Void, Void, String> {
        String fullJson;

        @Override
        protected void onPostExecute(String s) {

            try {
                JSONObject root = new JSONObject(fullJson);

                main = root.getJSONArray(TmdbApi.JSON_RESULTS_ARRAY);

                for (int i = 0; i < main.length(); i++) {
                    JSONObject movieJSON = main.getJSONObject(i);
                    gridAdapter.add(movieJSON.getString(TmdbApi.JSON_POSTER_PATH));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            gridAdapter.notifyDataSetChanged();
            gridView.smoothScrollToPosition(1);
            mSwipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected String doInBackground(Void... voids) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(TmdbApi.BASE_URL + "sort_by=" + TmdbApi.SORT_BY
                        + "&vote_count.gte=" + TmdbApi.MIN_VOTES + "&api_key=" + TmdbApi.API_KEY);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream == null)
                    return null;

                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                if (buffer.length() == 0)
                    return null;

                fullJson = buffer.toString();


            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();

                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return fullJson;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void update() {
        gridAdapter.clean();
        mSwipeRefreshLayout.setRefreshing(true);
        new fetchData().execute();
    }
}
