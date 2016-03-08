package com.learn.lonejourneyman.project1s1v00;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private String mPoster;
    private String mReleaseDate;
    private String mTitle;
    private String mPlot;
    private String mRate;

    public DetailActivityFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null) {
            mPoster = intent.getStringExtra("POSTER");
            mReleaseDate = intent.getStringExtra("RELEASEDATE");
            mTitle = intent.getStringExtra("TITLE");
            mPlot = intent.getStringExtra("PLOT");
            mRate = intent.getStringExtra("RATING");

            //poplate the page
            ((TextView) rootView.findViewById(R.id.detail_title)).setText(mTitle);
            ((TextView) rootView.findViewById(R.id.detail_rating)).setText(mRate + " / 10");
            ((TextView) rootView.findViewById(R.id.detail_release_date)).setText(mReleaseDate);
            ((TextView) rootView.findViewById(R.id.detail_summary)).setText(mPlot);

            ImageView imageView = (ImageView)rootView.findViewById(R.id.detail_poster);
            Picasso.with(getActivity()).load(mPoster).into(imageView);
        }

        return rootView;
    }
}
