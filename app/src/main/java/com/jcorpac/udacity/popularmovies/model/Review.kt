package com.jcorpac.udacity.popularmovies.model

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import android.util.Log

import org.json.JSONException
import org.json.JSONObject

class Review : Parcelable {
    private val LOG_TAG = this.javaClass.simpleName

    var author: String? = null
        private set
    var content: String? = null
        private set
    var reviewUrl: Uri? = null
        private set

    constructor(reviewJSON: JSONObject) {
        val authorTag = "author"
        val contentTag = "content"
        val urlTag = "url"

        try {
            this.author = reviewJSON.getString(authorTag)
            content = reviewJSON.getString(contentTag)
            reviewUrl = Uri.parse(reviewJSON.getString(urlTag))
        } catch (jse: JSONException) {
            Log.e(LOG_TAG, "Error parsing movie JSON")
            jse.printStackTrace()
        }

    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(this.author)
        dest.writeString(this.content)
        dest.writeParcelable(this.reviewUrl, flags)
    }

    private constructor(`in`: Parcel) {
        this.author = `in`.readString()
        this.content = `in`.readString()
        this.reviewUrl = `in`.readParcelable<Uri>(Uri::class.java.classLoader)
    }

    companion object {

        val CREATOR: Parcelable.Creator<Review> = object : Parcelable.Creator<Review> {
            override fun createFromParcel(source: Parcel): Review {
                return Review(source)
            }

            override fun newArray(size: Int): Array<Review?> {
                return arrayOfNulls(size)
            }
        }
    }
}
