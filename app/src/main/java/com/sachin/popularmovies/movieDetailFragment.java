package com.sachin.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sachin.popularmovies.API_KEY.ApiKey;
import com.sachin.popularmovies.database.MoviesDBHelper;
import com.sachin.popularmovies.modal.movieGeneralModal;
import com.sachin.popularmovies.modal.review.Results;
import com.sachin.popularmovies.modal.review.movieReview;
import com.sachin.popularmovies.modal.trailer.movieYoutubeModal;
import com.sachin.popularmovies.network.MovieAPI;
import com.sachin.popularmovies.network.NetworkAPI;
import com.squareup.picasso.Picasso;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class movieDetailFragment extends Fragment {

    private FragmentManager fm;
    private movieGeneralModal moviegeneralModal;
    private TextView reviewText, titleText, voteText, peoplesText, calendarText, plotSynopsis;
    private ImageView titleImage;
    private ImageView backdropImage;
    private LinearLayout youtubeViewHolder;
    private FloatingActionButton shareYoutube;
    private String shareYoutubeID;
    public FloatingActionButton fab;
    private TextView reviewUser;
    Toast mToast;



    public movieDetailFragment() {

    }

    public void setArgument(FragmentManager fm) {
        this.fm = fm;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);
        if (savedInstanceState != null) {
            this.moviegeneralModal = (movieGeneralModal) savedInstanceState.getSerializable("DATA");
        }
        MoviesDBHelper db = new MoviesDBHelper(getContext());

        updateGeneralUI(rootView);
        if(db.hasObject(moviegeneralModal.getmId())){
            fab.setImageResource(R.drawable.favorite2);
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("DATA", moviegeneralModal);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void setMovieData(movieGeneralModal moviegeneralModal) {
        this.moviegeneralModal = moviegeneralModal;
    }


    private void updateGeneralUI(View v) {
        titleText = (TextView) v.findViewById(R.id.titleText);
        voteText = (TextView) v.findViewById(R.id.rating);
        calendarText = (TextView) v.findViewById(R.id.calendar);
        peoplesText = (TextView) v.findViewById(R.id.people);
        titleImage = (ImageView) v.findViewById(R.id.titleimg);
        backdropImage=(ImageView) v.findViewById(R.id.backdrop);
        plotSynopsis = (TextView) v.findViewById(R.id.plotsynopsis);
        reviewText = (TextView) v.findViewById(R.id.reviewText);
        youtubeViewHolder = (LinearLayout) v.findViewById(R.id.youtubelayout);
        shareYoutube = (FloatingActionButton) v.findViewById(R.id.youtubesharer);
        fab = (FloatingActionButton) v.findViewById(R.id.fab);
        reviewUser=(TextView)v.findViewById(R.id.reviewUser);


        titleText.setText(moviegeneralModal.getTitle());
        voteText.setText(moviegeneralModal.getmVote());
        peoplesText.setText(moviegeneralModal.getmPeople());
        calendarText.setText(moviegeneralModal.getmReleaseDate());
        plotSynopsis.setText(moviegeneralModal.getmOverview());



        getMovieReview(reviewText);

        Picasso.with(getContext())
                .load(moviegeneralModal.getThumbnail())
                .placeholder(R.drawable.loading)
                .error(R.drawable.error)
                .fit().into(titleImage);
        Picasso.with(getContext())
                .load(moviegeneralModal.getBackdrop())
                .placeholder(R.drawable.backload)
                .error(R.drawable.backerror)
                .fit().into(backdropImage);




        getTrailer(youtubeViewHolder);
        shareYoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shareYoutubeID != null) {
                    shareYoutubeIntent(shareYoutubeID);
                } else {
                    Toast.makeText(getContext(), "Sorry! No Youtube Videos Available!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoviesDBHelper db = new MoviesDBHelper(getContext());

                if(!db.hasObject(moviegeneralModal.getmId())){
                    saveToDatabase();
                } else {
                    removeDatabase();

                }
            }
        });

    }

    protected void saveToDatabase() {
        MoviesDBHelper db = new MoviesDBHelper(getContext());
        if (!reviewText.getText().toString().contains("Sorry")) {
            moviegeneralModal.setmReview(reviewText.getText().toString());
        }
        boolean b = db.insertMovie(moviegeneralModal);

        if (b) {
            if(mToast != null)
            {
                mToast.cancel();
            }
            mToast=Toast.makeText(getContext(), "Added to Favourite!", Toast.LENGTH_SHORT);
            mToast.show();
            fab.setImageResource(R.drawable.favorite2);
        }

    }
    protected void removeDatabase(){
        MoviesDBHelper db = new MoviesDBHelper(getContext());
        if(mToast != null)
        {
            mToast.cancel();
        }
        mToast=Toast.makeText(getContext(), "Removed from Favourite!", Toast.LENGTH_SHORT);
        mToast.show();
        fab.setImageResource(R.drawable.favorite);
        db.deleteMovie(moviegeneralModal.getTitle());
    }


    protected void shareYoutubeIntent(String shareYoutubeID) {
        String url = "http://www.youtube.com/watch?v=" + shareYoutubeID;
        String shareMsg = "Hey! There's a film named " +"'"+ moviegeneralModal.getTitle()+"'" + ". It looks awesome!. \nThe trailer link is- " + url + "\nHave a look at it!";
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Popular Movies - Android App");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareMsg);
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
    }

    protected String generateYoutubeThumbnailURL(String id) {
        String url = "http://img.youtube.com/vi/" + id + "/mqdefault.jpg";
        return url;
    }

    public void watchYoutubeVideo(String id) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + id));
            startActivity(intent);
        }
    }

    protected void getTrailer(final LinearLayout youtubeViewHolder) {
        MovieAPI mMovieAPI = NetworkAPI.createService(MovieAPI.class);
        mMovieAPI.fetchVideos(ApiKey.TMDB_API_KEY, this.moviegeneralModal.getmId(), new Callback<movieYoutubeModal>() {

            @Override
            public void success(movieYoutubeModal movieYoutubeModal, Response response) {
                try{
                youtubeViewHolder.setPadding(5, 5, 5, 5);
                com.sachin.popularmovies.modal.trailer.Results[] trailer = movieYoutubeModal.getResults();
                if (trailer.length > 0) {
                    shareYoutubeID = trailer[0].getKey();
                    for (final com.sachin.popularmovies.modal.trailer.Results obj : trailer) {
                        String url = generateYoutubeThumbnailURL(obj.getKey());
                        ImageView myImage = new ImageView(getContext());
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                180,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        params.leftMargin = 3;
                        params.rightMargin = 3;
                        params.topMargin = 6;
                        params.bottomMargin = 3;
                        myImage.setLayoutParams(params);
                        Picasso.with(getContext())
                                .load(url)
                                .into(myImage);
                        youtubeViewHolder.addView(myImage);
                        myImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                watchYoutubeVideo(obj.getKey());
                            }
                        });

                    }

                } else {
                    youtubeViewHolder.setPadding(10,35,10,5);
                    TextView errmsg = new TextView(getContext());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            30
                    );
                    errmsg.setLayoutParams(params);
                    errmsg.setText("Sorry, No Trailers Found!");
                    youtubeViewHolder.addView(errmsg);}
            }catch(Exception e){

            }}

            @Override
            public void failure(RetrofitError error) {
                youtubeViewHolder.setPadding(10, 35, 10, 5);
                TextView errmsg = new TextView(getContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        30
                );
                errmsg.setLayoutParams(params);
                errmsg.setText("Network Error! Connect to Network to watch Trailer!");
                youtubeViewHolder.addView(errmsg);

            }
        });
    }


    protected void getMovieReview(final View review) {
        MovieAPI mMovieAPI = NetworkAPI.createService(MovieAPI.class);
        mMovieAPI.fetchReview(ApiKey.TMDB_API_KEY, this.moviegeneralModal.getmId(), new Callback<movieReview>() {
            @Override
            public void success(movieReview movieReview, Response response) {
                Results[] movieResult = movieReview.getResults();
                if (movieResult.length > 0) {
                    reviewUser.setText(movieResult[0].getAuthor());
                    ((TextView) review).setText(movieResult[0].getContent());
                }

                else
                    ((TextView) review).setText("Sorry No Reviews are Available!");

            }


            @Override
            public void failure(RetrofitError error) {
                Log.d("error", error.toString());
                ((TextView) review).setText("Network Error! Connect to Network to see Reviews! ");
            }
        });


    }

    protected void generateThumbnail() {

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }



}