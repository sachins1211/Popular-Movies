package com.example.sachin.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class gridAdapter extends BaseAdapter {

    Context mContext;
    List<String> list = new ArrayList<String>();

    public gridAdapter(Context context) {
        mContext=context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if(convertView == null){

            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, 450));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);


        }else {

            imageView = (ImageView) convertView;
        }

        Picasso.with(mContext)
                .load(TmdbApi.IMG_BASE_URL + TmdbApi.SMALL_IMG_SIZE + list.get(position))
                .into(imageView);

        return imageView;
    }

    public void clean() {
        list.clear();
    }

    public void add(String s) {
        list.add(s);
    }

}
