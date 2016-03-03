package com.sachin.popularmovies.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sachin.popularmovies.R;


public class movieGeneralHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView movieName, movieAvg, calendar;
    public ImageView moviePhoto;
    public View mView;


    public movieGeneralHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        mView = itemView;
        movieName = (TextView) itemView.findViewById(R.id.movieName);
        movieAvg = (TextView) itemView.findViewById(R.id.vote);
        moviePhoto = (ImageView) itemView.findViewById(R.id.moviePhoto);
        calendar=(TextView) itemView.findViewById(R.id.calendar);



    }

    @Override
    public void onClick(View view) {
        //Toast.makeText(view.getContext(), "Position = " + getPosition(), Toast.LENGTH_SHORT).show();
    }
}

