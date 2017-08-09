package com.jcorpac.udacity.popularmovies.model

import android.os.Parcel
import android.os.Parcelable
import android.util.Log

import org.json.JSONException
import org.json.JSONObject

class Trailer : Parcelable {
    private val LOG_TAG = this.javaClass.simpleName

    var trailerName: String? = null
        private set
    var videoId: String? = null
        private set

    constructor(trailerJSON: JSONObject) {
        val YOUTUBE_SITE_NAME = "YouTube"

        val nameTag = "name"
        val videoIdTag = "key"
        val siteNameTag = "site"

        try {
            this.trailerName = trailerJSON.getString(nameTag)
            if (trailerJSON.getString(siteNameTag) == YOUTUBE_SITE_NAME) {
                this.videoId = trailerJSON.getString(videoIdTag)
            } else {
                return
            }
        } catch (jse: JSONException) {
            Log.e(LOG_TAG, "Error parsing movie JSON")
            jse.printStackTrace()
        }

    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(this.trailerName)
        dest.writeString(this.videoId)
    }

    protected constructor(`in`: Parcel) {
        this.trailerName = `in`.readString()
        this.videoId = `in`.readString()
    }

    companion object {

        val CREATOR: Parcelable.Creator<Trailer> = object : Parcelable.Creator<Trailer> {
            override fun createFromParcel(source: Parcel): Trailer {
                return Trailer(source)
            }

            override fun newArray(size: Int): Array<Trailer?> {
                return arrayOfNulls(size)
            }
        }
    }
}
