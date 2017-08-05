package com.jcorpac.udacity.popularmovies.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Trailer {
    private final String LOG_TAG = this.getClass().getSimpleName();

    private String trailerName;
    private String videoId;

    public Trailer(JSONObject trailerJSON) {
        final String YOUTUBE_SITE_NAME = "YouTube";

        String nameTag = "name";
        String videoIdTag = "key";
        String siteNameTag = "site";

        try {
            this.trailerName = trailerJSON.getString(nameTag);
            if(trailerJSON.getString(siteNameTag).equals(YOUTUBE_SITE_NAME)){
                this.videoId = trailerJSON.getString(videoIdTag);
            } else {
                return;
            }
        } catch (JSONException jse) {
            Log.e(LOG_TAG, "Error parsing movie JSON");
            jse.printStackTrace();
        }
    }
    public String getTrailerName() { return trailerName; }

    public String getVideoId() { return videoId; }
}
