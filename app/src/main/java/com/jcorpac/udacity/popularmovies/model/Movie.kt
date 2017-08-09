package com.jcorpac.udacity.popularmovies.model

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import android.util.Log

import com.jcorpac.udacity.popularmovies.Constants

import org.json.JSONException
import org.json.JSONObject

class Movie : Parcelable{
private val LOG_TAG = this.javaClass.simpleName

var title: String? = null
        private set

var summary: String? = null
        private set

var posterURL: String? = null
        private set

var posterThumbnailURL: String? = null
        private set

var releaseDate: String? = null
        private set

var voteAverage: Double = 0.toDouble()
        private set

private var trailersUri: String? = null

private var reviewsUri: String? = null

var movieID: String? = null
        private set

constructor(title: String, summary: String, posterURL: String, releaseDate: String, voteAverage: Double, movieID: String) {
        this.title = title
        this.summary = summary
        this.posterURL = posterURL
        this.posterThumbnailURL = Constants.POSTER_BASE_URL + Constants.THUMBNAIL_RES + posterURL
        this.releaseDate = releaseDate
        this.voteAverage = voteAverage
        trailersUri = Constants.BASE_URL + "movie/" + movieID + "/videos"
        reviewsUri = Constants.BASE_URL + "movie/" + movieID + "/reviews"
        this.movieID = movieID
    }

constructor(movieJSON: JSONObject) {
        val titleTag = "original_title"
        val summaryTag = "overview"
        val posterTag = "poster_path"

        val voteTag = "vote_average"
        val releaseTag = "release_date"
        val idTag = "id"

        try {
            this.title = movieJSON.getString(titleTag)
            this.summary = movieJSON.getString(summaryTag)
            this.posterURL = movieJSON.getString(posterTag)
            this.posterThumbnailURL = Constants.POSTER_BASE_URL + Constants.THUMBNAIL_RES + movieJSON.getString(posterTag)
            this.releaseDate = movieJSON.getString(releaseTag)
            this.voteAverage = movieJSON.getDouble(voteTag)
            this.movieID = movieJSON.getString(idTag)
            trailersUri = Constants.BASE_URL + "movie/" + movieID + "/videos"
            reviewsUri = Constants.BASE_URL + "movie/" + movieID + "/reviews"
        } catch (jse: JSONException) {
            Log.e(LOG_TAG, "Error parsing movie JSON")
            jse.printStackTrace()
        }

    }

fun getTrailersUri(): Uri {
        return Uri.parse(trailersUri).buildUpon()
                .appendQueryParameter("api_key", Constants.API_KEY)
                .build()
    }

fun getReviewsUri(): Uri {
        return Uri.parse(reviewsUri).buildUpon()
                .appendQueryParameter("api_key", Constants.API_KEY)
                .build()
    }

override fun toString(): String {
        return "Title = $title Release Date = $releaseDate id = $movieID"
    }

    private constructor(`in`: Parcel) {
        this.title = `in`.readString()
        this.summary = `in`.readString()
        this.posterURL = `in`.readString()
        this.posterThumbnailURL = `in`.readString()
        this.releaseDate = `in`.readString()
        this.voteAverage = `in`.readDouble()
        this.movieID = `in`.readString()
        trailersUri = Constants.BASE_URL + "movie/" + this.movieID + "/videos"
        reviewsUri = Constants.BASE_URL + "movie/" + this.movieID + "/reviews"
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(this.title)
        dest.writeString(this.summary)
        dest.writeString(this.posterURL)
        dest.writeString(this.posterThumbnailURL)
        dest.writeString(this.releaseDate)
        dest.writeDouble(this.voteAverage)
        dest.writeString(this.movieID)
    }

companion object {
    @JvmField val CREATOR: Parcelable.Creator<Movie> = object : Parcelable.Creator<Movie> {
        override fun createFromParcel(source: Parcel): Movie = Movie(source)
        override fun newArray(size: Int): Array<Movie?> =arrayOfNulls(size)}}

        override fun describeContents() = 0
}
