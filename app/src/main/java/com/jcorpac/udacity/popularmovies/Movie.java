package com.jcorpac.udacity.popularmovies;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jeff on 8/28/15.
 */
public class Movie {

    private String LOG_TAG = this.getClass().getSimpleName();

    private String title;
    private String summary;
    private String posterURL;
    private String releaseDate;
    private double voteAverage;

    public Movie(JSONObject movieJSON) {
        String titleTag = "original_title";
        String summaryTag = "overview";
        String posterTag = "poster_path";
        String voteTag = "vote_average";
        String releaseTag = "release_date";

        try {
            this.title = movieJSON.getString(titleTag);
            this.summary = movieJSON.getString(summaryTag);
            this.posterURL = movieJSON.getString(posterTag);
            this.releaseDate = movieJSON.getString(releaseTag);
            this.voteAverage = movieJSON.getDouble(voteTag);
        } catch (JSONException jse) {
            Log.e(LOG_TAG, "Error parsing movie JSON");
            jse.printStackTrace();
        }
    }

    public String getTitle() { return title; }

    public String getSummary() { return summary; }

    public String getPosterURL() { return posterURL; }

    public String getReleaseDate() { return releaseDate; }

    public double getVoteAverage() { return voteAverage; }
}
