package com.jcorpac.udacity.popularmovies.model;

import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Review {
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
}
