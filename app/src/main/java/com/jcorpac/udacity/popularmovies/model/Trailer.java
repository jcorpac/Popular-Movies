package com.jcorpac.udacity.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Trailer implements Parcelable {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.trailerName);
        dest.writeString(this.videoId);
    }

    protected Trailer(Parcel in) {
        this.trailerName = in.readString();
        this.videoId = in.readString();
    }

    public static final Parcelable.Creator<Trailer> CREATOR = new Parcelable.Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel source) {
            return new Trailer(source);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };
}
