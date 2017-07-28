package com.jcorpac.udacity.popularmovies.model;

import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import com.jcorpac.udacity.popularmovies.Constants;

public class Movie {

    private final String LOG_TAG = this.getClass().getSimpleName();

    private String title;
    private String summary;
    private String posterURL;
    private String releaseDate;
    private double voteAverage;
    private String trailersUri;
    private String reviewsUri;
    private String movieID;

    public Movie(String title, String summary, String posterURL, String releaseDate, double voteAverage, String trailersUri, String reviewsUri, String movieID) {
        this.title = title;
        this.summary = summary;
        this.posterURL = posterURL;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.trailersUri = trailersUri;
        this.reviewsUri = reviewsUri;
        this.movieID = movieID;
    }

    public Movie(JSONObject movieJSON) {
        String titleTag = "original_title";
        String summaryTag = "overview";
        String posterTag = "poster_path";

        String voteTag = "vote_average";
        String releaseTag = "release_date";
        String idTag = "id";

        try {
            this.title = movieJSON.getString(titleTag);
            this.summary = movieJSON.getString(summaryTag);
            this.posterURL = movieJSON.getString(posterTag);
            this.releaseDate = movieJSON.getString(releaseTag);
            this.voteAverage = movieJSON.getDouble(voteTag);
            this.movieID = movieJSON.getString(idTag);
            trailersUri = Constants.BASE_URL + "movie/" + movieID + "/videos";
            reviewsUri = Constants.BASE_URL + "movie/" + movieID + "/reviews";
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

    public Uri getTrailersUri() {
        return Uri.parse(trailersUri).buildUpon()
                .appendQueryParameter("api_key", Constants.API_KEY)
                .build();
    }

    public Uri getReviewsUri() {
        return Uri.parse(reviewsUri).buildUpon()
                .appendQueryParameter("api_key", Constants.API_KEY)
                .build();
    }

    @Override
    public String toString() {
        return "Title = " + title + " Release Date = " + releaseDate + " id = " + movieID;
    }
}
