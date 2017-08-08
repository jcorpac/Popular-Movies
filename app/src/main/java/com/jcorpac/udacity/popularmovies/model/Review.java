package com.jcorpac.udacity.popularmovies.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Review implements Parcelable {
    private final String LOG_TAG = this.getClass().getSimpleName();

    private String author;
    private String content;
    private Uri reviewUrl;

    public Review(JSONObject reviewJSON) {
        String authorTag = "author";
        String contentTag = "content";
        String urlTag = "url";

        try {
            this.author = reviewJSON.getString(authorTag);
            content = reviewJSON.getString(contentTag);
            reviewUrl = Uri.parse(reviewJSON.getString(urlTag));
        } catch (JSONException jse) {
            Log.e(LOG_TAG, "Error parsing movie JSON");
            jse.printStackTrace();
        }
    }

    public String getAuthor() { return author; }

    public String getContent() { return content; }

    public Uri getReviewUrl() { return reviewUrl; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.author);
        dest.writeString(this.content);
        dest.writeParcelable(this.reviewUrl, flags);
    }

    protected Review(Parcel in) {
        this.author = in.readString();
        this.content = in.readString();
        this.reviewUrl = in.readParcelable(Uri.class.getClassLoader());
    }

    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel source) {
            return new Review(source);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
}
