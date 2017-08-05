package com.jcorpac.udacity.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.jcorpac.udacity.popularmovies.data.FavoritesContract.FavoritesEntry;
import com.jcorpac.udacity.popularmovies.model.Movie;

import java.util.ArrayList;
import java.util.Arrays;


public class FavoritesFragment extends Fragment {

    private final String LOG_TAG = this.getClass().getSimpleName();

    Movie[] favMoviesList;
    String[] thumbnailArray;

    private ImageAdapter movieAdapter;
    private GridView posterLayout;

    public FavoritesFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorites, container, false);
        posterLayout = (GridView)rootView.findViewById(R.id.poster_layout);

        posterLayout.setAdapter(movieAdapter);

        posterLayout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent toDetailView = new Intent(getActivity(), DetailActivity.class);
                toDetailView.putExtra(Constants.DETAIL_INTENT_LABEL, favMoviesList[position]);
                startActivity(toDetailView);
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateMovies();
    }

    private void updateMovies() {
        Cursor favMoviesCursor = getActivity().getContentResolver().query(FavoritesEntry.CONTENT_URI,
                null,
                null,
                null,
                FavoritesEntry._ID);

        favMoviesList = new Movie[favMoviesCursor.getCount()];
        thumbnailArray = new String[favMoviesCursor.getCount()];
        String movieTitle;
        String movieSummary;
        String posterURL;
        String releaseDate;
        double movieVoteAverage;
        String movieID;
        Movie newMovie;
        int movieIndex = 0;

        try {
            while (favMoviesCursor.moveToNext()) {
                movieTitle = favMoviesCursor.getString(favMoviesCursor.getColumnIndex(FavoritesEntry.COLUMN_TITLE));
                movieSummary = favMoviesCursor.getString(favMoviesCursor.getColumnIndex(FavoritesEntry.COLUMN_SUMMARY));
                posterURL = favMoviesCursor.getString(favMoviesCursor.getColumnIndex(FavoritesEntry.COLUMN_POSTER_URL));
                releaseDate = favMoviesCursor.getString(favMoviesCursor.getColumnIndex(FavoritesEntry.COLUMN_RELEASE_DATE));
                movieVoteAverage = favMoviesCursor.getDouble(favMoviesCursor.getColumnIndex(FavoritesEntry.COLUMN_VOTE_AVERAGE));
                movieID = favMoviesCursor.getString(favMoviesCursor.getColumnIndex(FavoritesEntry.COLUMN_MOVIE_ID));

                newMovie = new Movie(movieTitle, movieSummary, posterURL, releaseDate, movieVoteAverage, movieID);
                this.favMoviesList[movieIndex] = newMovie;
                this.thumbnailArray[movieIndex] = newMovie.getPosterThumbnailURL();

                movieIndex++;
            }
        } finally {
            favMoviesCursor.close();
        }

        ArrayList<String> thumbnailArrayList = new ArrayList<>(Arrays.asList(thumbnailArray));
        movieAdapter = new ImageAdapter(getActivity(), thumbnailArrayList);
        movieAdapter.notifyDataSetChanged();
        posterLayout.setAdapter(movieAdapter);
    }
}
