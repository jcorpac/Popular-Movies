package com.jcorpac.udacity.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.jcorpac.udacity.popularmovies.model.Movie;

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
import java.util.Arrays;

public class MainActivityFragment extends Fragment {

    Movie[] moviesList;
    String[] thumbnailArray;

    private ImageAdapter movieAdapter;
    private GridView posterLayout;

    public MainActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        posterLayout = (GridView)rootView.findViewById(R.id.poster_layout);

        posterLayout.setAdapter(movieAdapter);

        posterLayout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(DetailActivity.newIntent(getActivity(), moviesList[position]));
            }
        });

        return rootView;
    }

    private void updateMovies() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortMoviesBy = prefs.getString(getString(R.string.sortByKey), getString(R.string.strDefaultSortValue));

        new GetMoviesTask().execute(sortMoviesBy);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch(itemId){
            case (R.id.action_settings):
                startActivity(new Intent(getContext(), SettingsActivity.class));
                return true;
            case (R.id.action_favorites):
                startActivity(new Intent(getContext(), FavoritesActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class GetMoviesTask extends AsyncTask<String, Void, Movie[]> {

        final String LOG_TAG = this.getClass().getSimpleName();

        String serviceJsonStr = null;
        Movie[] movies = null;


        @Override
        protected Movie[] doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            final String API_PARAM = "api_key";
            final String SORT_BY_PARAM = "sort_by";

            Uri serviceEndpoint = Uri.parse(Constants.MOVIES_SERVICE).buildUpon()
                    .appendQueryParameter(SORT_BY_PARAM, params[0] + ".desc")
                    .appendQueryParameter(API_PARAM, Constants.API_KEY)
                    .build();

            try {
                URL serviceURL = new URL(serviceEndpoint.toString());
                urlConnection = (HttpURLConnection) serviceURL.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                serviceJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);

                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            getMovieList();

            return movies;
        }

        private void getMovieList() {
            JSONObject currentMovie;
            try {
                JSONArray moviesArray =  new JSONObject(serviceJsonStr).getJSONArray("results");
                movies = new Movie[moviesArray.length()];
                thumbnailArray = new String[moviesArray.length()];

                for(int i = 0; i<moviesArray.length(); i++){
                    currentMovie = moviesArray.getJSONObject(i);
                    movies[i] = new Movie(currentMovie);
                    thumbnailArray[i] = movies[i].getPosterThumbnailURL();
                }
            } catch (JSONException jse) {
                Log.e(LOG_TAG, "Error retrieving movie list", jse);
                jse.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            super.onPostExecute(movies);
            if (movies != null) {
                moviesList = movies;

                ArrayList<String> thumbnailArrayList = new ArrayList<>(Arrays.asList(thumbnailArray));
                movieAdapter = new ImageAdapter(getActivity(), thumbnailArrayList);
                movieAdapter.notifyDataSetChanged();
                posterLayout.setAdapter(movieAdapter);
            } else {
                Toast.makeText(getContext(), "Error Retrieving New Movies", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
