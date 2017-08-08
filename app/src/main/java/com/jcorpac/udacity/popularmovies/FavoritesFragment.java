package com.jcorpac.udacity.popularmovies;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.jcorpac.udacity.popularmovies.data.FavoritesContract.FavoritesEntry;
import com.jcorpac.udacity.popularmovies.model.Movie;

import java.util.ArrayList;
import java.util.Arrays;


public class FavoritesFragment extends Fragment {

    Movie[] favMoviesList;
    String[] thumbnailArray;

    int mCurrentPosition;
    private final String CURRENT_POSITION_TAG = "mCurrentPosition";

    private ImageAdapter movieAdapter;
    private GridView posterLayout;
    private TextView txtErrorMessage;

    public FavoritesFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorites, container, false);
        if(savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt(CURRENT_POSITION_TAG);
        }

        txtErrorMessage = (TextView)rootView.findViewById(R.id.txt_favorites_error);

        posterLayout = (GridView)rootView.findViewById(R.id.poster_layout);
        posterLayout.setAdapter(movieAdapter);
        posterLayout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(DetailActivity.newIntent(getActivity(), favMoviesList[position]));
            }
        });
        posterLayout.smoothScrollToPosition(mCurrentPosition);
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

        if(favMoviesCursor == null || favMoviesCursor.getCount() == 0){
            posterLayout.setVisibility(View.INVISIBLE);
            txtErrorMessage.setVisibility(View.VISIBLE);
        } else {
            favMoviesList = new Movie[favMoviesCursor.getCount()];
            thumbnailArray = new String[favMoviesCursor.getCount()];
            String movieTitle, movieSummary, posterURL, releaseDate, movieID;
            double movieVoteAverage;
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
            posterLayout.smoothScrollToPosition(mCurrentPosition);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_POSITION_TAG, posterLayout.getFirstVisiblePosition());
    }
}
