package com.jcorpac.udacity.popularmovies.model;

import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Trailer {
    private final String LOG_TAG = this.getClass().getSimpleName();

    private String trailerName;
    private Uri trailerWebUrl;
    private Uri trailerAppUri;

    public Trailer(JSONObject trailerJSON) {
        final String YOUTUBE_SITE_NAME = "YouTube";
        final String YOUTUBE_WEB_URL = "https://m.youtube.com/watch?v=";
        final String YOUTUBE_APP_URI = "vnd.youtube:";

        String nameTag = "name";
        String trailerKeyTag = "key";
        String siteNameTag = "site";
        String typeTag = "type";
        final String TRAILER_TYPE = "Trailer";

        String siteName;
        String trailerKey;
        try {
            if (trailerJSON.getString(typeTag).equals(TRAILER_TYPE)) {
                this.trailerName = trailerJSON.getString(nameTag);
                trailerKey = trailerJSON.getString(trailerKeyTag);
                siteName = trailerJSON.getString(siteNameTag);
                if(siteName.equals(YOUTUBE_SITE_NAME)){
                    trailerWebUrl = Uri.parse(YOUTUBE_WEB_URL + trailerKey);
                    trailerAppUri = Uri.parse(YOUTUBE_APP_URI + trailerKey);
                }
            }
        } catch (JSONException jse) {
            Log.e(LOG_TAG, "Error parsing movie JSON");
            jse.printStackTrace();
        }
    }
    public String getTrailerName() { return trailerName; }

    public Uri getTrailerWebUrl() { return trailerWebUrl; }

    public Uri getTrailerAppUri() { return trailerAppUri; }

    @Override
    public String toString() {
        return trailerName + " - " + trailerWebUrl;
    }
}
