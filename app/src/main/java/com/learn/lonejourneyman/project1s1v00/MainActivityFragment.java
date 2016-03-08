package com.learn.lonejourneyman.project1s1v00;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    // to setup ArrayList, Custome Adapter and Grid
    final String POSTERS_BASE_URL = "http://image.tmdb.org/t/p/w342";
    List<String> posters = new ArrayList<>();
    List<String> releasedates = new ArrayList<>();
    List<String> titles = new ArrayList<>();
    List<String> plots = new ArrayList<>();
    List<String> rates = new ArrayList<>();
    ImageArrayAdapter adapter;
    GridView gridView;

    public MainActivityFragment() {
    }

    public void updateMovie() {
        FetchMovieDBTask movieDBTask = new FetchMovieDBTask();

        //getting the sortby from setting
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortBy = prefs.getString(getString(R.string.pref_sort_by_key),getString(R.string.pref_sort_by_default));

        movieDBTask.execute(sortBy);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovie();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //setup rootView
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        //setup adapter
        adapter = new ImageArrayAdapter(getActivity(), posters);
        //setup grid
        gridView = (GridView) rootView.findViewById(R.id.posters_grid);

        //setup onClick detail page
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra("POSTER", posters.get(position))
                        .putExtra("RELEASEDATE", releasedates.get(position))
                        .putExtra("TITLE", titles.get(position))
                        .putExtra("PLOT", plots.get(position))
                        .putExtra("RATING", rates.get(position));

                startActivity(intent);
            }
        });

        return rootView;
    }

    //take the JSON string and extract the needed data
    private String[] getMovieDataFromJSON(String movieDBJsonStr) throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_RESULTS = "results";
        final String OWM_POSTER_PATH = "poster_path";
        final String OWM_PLOT_SYNOPSIS = "overview";
        final String OWM_RELEASE_DATE = "release_date";
        final String OWM_TITLE = "original_title";
        final String OWM_USER_RATINGS = "vote_average";

        JSONObject movieJson = new JSONObject(movieDBJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(OWM_RESULTS);

        String[] resultStrs = new String[movieArray.length()];

        for (int i = 0; i < movieArray.length(); i++ ) {

            //get individual movie data
            JSONObject movieData = movieArray.getJSONObject(i);

            String posterPath = movieData.getString(OWM_POSTER_PATH);
            String releaseDate = movieData.getString(OWM_RELEASE_DATE);
            String title = movieData.getString(OWM_TITLE);
            String synopsis = movieData.getString(OWM_PLOT_SYNOPSIS);
            String ratings = movieData.getString(OWM_USER_RATINGS);

            resultStrs[i] = posterPath + "~"
                + releaseDate + "~"
                + title + "~"
                + synopsis + "~"
                + ratings;
        }

        return resultStrs;
    }


    //setting up fetching moviedb data async task
    public class FetchMovieDBTask extends AsyncTask<String, Void, String[]> {

        //setup LOGTAG
        private final String LOG_TAG = FetchMovieDBTask.class.getSimpleName();

        //call movieDB to get JSON
        protected String[] doInBackground(String... params) {
            //out of try/catch block
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            //contain raw movieDB JSON as string
            String movieDBJsonStr = null;

            String api = getString(R.string.moviedb_api_key);

            try {
                //constuct URL
                //http://api.themoviedb.org/3/discover/movie?sort_by=vote_average.desc/popularity.desc&api_key
                final String MOVIEBD_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_PARAM = "sort_by";
                final String API_KEY = "api_key";

                Uri builtUri = Uri.parse(MOVIEBD_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, params[0])
                        .appendQueryParameter(API_KEY, api)
                        .build();

                URL url = new URL(builtUri.toString());

                //create request to MovieDB
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                //read uboyt stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if( inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                movieDBJsonStr = buffer.toString();

                Log.v(LOG_TAG,"Movie DB JSON String: " + movieDBJsonStr);

            } catch(IOException e) {
                Log.e(LOG_TAG, "Error", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch(final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieDataFromJSON(movieDBJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {

            if (result != null) {

                String[] movieDataSplit;

                for (int i = 0; i < result.length; i++) {
                    movieDataSplit = result[i].split("~");
                    posters.add(POSTERS_BASE_URL + movieDataSplit[0]);
                    releasedates.add(movieDataSplit[1]);
                    titles.add(movieDataSplit[2]);
                    plots.add(movieDataSplit[3]);
                    rates.add(movieDataSplit[4]);
                }

                adapter = new ImageArrayAdapter(getActivity(), posters);
                gridView.setAdapter(adapter);

            }
        }

    }

}