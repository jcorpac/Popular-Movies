package com.jcorpac.udacity.popularmovies.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.jcorpac.udacity.popularmovies.Constants;

import org.json.JSONException;
import org.json.JSONObject;

public class Movie implements Parcelable {

    private final String LOG_TAG = this.getClass().getSimpleName();

    private String title;
    private String summary;
    private String posterURL;
    private String posterThumbnailURL;
    private String releaseDate;
    private double voteAverage;
    private String trailersUri;
    private String reviewsUri;
    private String movieID;

    public Movie(String title, String summary, String posterURL, String releaseDate, double voteAverage, String movieID) {
        this.title = title;
        this.summary = summary;
        this.posterURL = posterURL;
        this.posterThumbnailURL = Constants.POSTER_BASE_URL + Constants.THUMBNAIL_RES + posterURL;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        trailersUri = Constants.BASE_URL + "movie/" + movieID + "/videos";
        reviewsUri = Constants.BASE_URL + "movie/" + movieID + "/reviews";
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
            this.posterThumbnailURL = Constants.POSTER_BASE_URL + Constants.THUMBNAIL_RES + movieJSON.getString(posterTag);
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

    public String getPosterThumbnailURL() { return posterThumbnailURL; }

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

    public String getMovieID() { return movieID; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.summary);
        dest.writeString(this.posterURL);
        dest.writeString(this.posterThumbnailURL);
        dest.writeString(this.releaseDate);
        dest.writeDouble(this.voteAverage);
        dest.writeString(this.movieID);
    }

    protected Movie(Parcel in) {
        this.title = in.readString();
        this.summary = in.readString();
        this.posterURL = in.readString();
        this.posterThumbnailURL = in.readString();
        this.releaseDate = in.readString();
        this.voteAverage = in.readDouble();
        this.movieID = in.readString();
        trailersUri = Constants.BASE_URL + "movie/" + this.movieID + "/videos";
        reviewsUri = Constants.BASE_URL + "movie/" + this.movieID + "/reviews";
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
