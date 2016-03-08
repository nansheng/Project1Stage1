package com.learn.lonejourneyman.project1s1v00;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by lonejourneyman on 3/6/16.
 */
public class ImageArrayAdapter extends ArrayAdapter<String> {


    public ImageArrayAdapter(Context context, List<String> posterURLs) {
        super(context, R.layout.movie_poster, posterURLs);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.movie_poster, parent, false);

        if (customView == null) customView = inflater.inflate(R.layout.movie_poster, null);

        String onePoster = getItem(position);
        ImageView image = (ImageView) customView.findViewById(R.id.grid_movie_poster);

        Picasso.with(getContext()).load(onePoster).into(image);

        return customView;
    }
}
