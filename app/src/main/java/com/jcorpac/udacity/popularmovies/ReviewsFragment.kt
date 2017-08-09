package com.jcorpac.udacity.popularmovies


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.jcorpac.udacity.popularmovies.model.Review
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class ReviewsFragment : Fragment() {

    internal lateinit var lstReviews: ListView
    internal lateinit var prgReviewsProgress: ProgressBar
    internal lateinit var txtReviewsError: TextView

    private val LIST_VIEW_STATE_TAG = "listViewState"
    private val REVIEWS_ARRAY_LIST = "reviewsArray"
    internal var reviewsArray: ArrayList<Review>? = null


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_reviews, container, false)

        lstReviews = rootView.findViewById(R.id.lst_reviews) as ListView
        if (savedInstanceState != null) {
            reviewsArray = savedInstanceState.getParcelableArrayList<Review>(REVIEWS_ARRAY_LIST)
            lstReviews.adapter = ReviewsAdapter(activity, R.layout.review_row_item, reviewsArray!!)
            lstReviews.onRestoreInstanceState(savedInstanceState.getParcelable<Parcelable>(LIST_VIEW_STATE_TAG))
        }

        val incomingIntent = activity.intent
        val reviewsUri: Uri
        val movieTitle: String
        prgReviewsProgress = rootView.findViewById(R.id.prg_reviews_progress) as ProgressBar
        txtReviewsError = rootView.findViewById(R.id.txt_reviews_error) as TextView
        if (incomingIntent != null && reviewsArray == null) {
            reviewsUri = incomingIntent.getParcelableExtra<Uri>(ReviewsActivity.REVIEWS_URI_TAG)
            movieTitle = incomingIntent.getStringExtra(ReviewsActivity.REVIEWS_MOVIE_TAG)
            activity.title = movieTitle + " reviews"
            GetReviewsTask().execute(reviewsUri)
        }

        return rootView
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState!!.putParcelable(LIST_VIEW_STATE_TAG, lstReviews.onSaveInstanceState())
        outState.putParcelableArrayList(REVIEWS_ARRAY_LIST, reviewsArray)
    }

    private inner class GetReviewsTask : AsyncTask<Uri, Void, ArrayList<Review>>() {

        internal val LOG_TAG = this.javaClass.simpleName

        internal lateinit var serviceJsonStr: String
        internal var reviewsList = ArrayList<Review>()

        override fun onPreExecute() {
            super.onPreExecute()
            prgReviewsProgress.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg params: Uri): ArrayList<Review>? {

            var urlConnection: HttpURLConnection? = null
            var reader: BufferedReader? = null

            val serviceEndpoint = params[0]

            try {
                val serviceURL = URL(serviceEndpoint.toString())
                urlConnection = serviceURL.openConnection() as HttpURLConnection
                urlConnection.requestMethod = "GET"
                urlConnection.connectTimeout = 5000
                urlConnection.readTimeout = 10000
                urlConnection.connect()

                val inputStream = urlConnection.inputStream
                val buffer = StringBuilder()
                if (inputStream == null) {
                    return null
                }
                reader = BufferedReader(InputStreamReader(inputStream))

                var line = reader.readLine()
                while (line != null) {
                    buffer.append("$line\n")
                    line = reader.readLine()
                }

                if (buffer.isEmpty()) {
                    // Stream was empty.  No point in parsing.
                    return null
                }
                serviceJsonStr = buffer.toString()

            } catch (e: IOException) {
                Log.e(LOG_TAG, "Error ", e)

                return null
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect()
                }
                if (reader != null) {
                    try {
                        reader.close()
                    } catch (e: IOException) {
                        Log.e(LOG_TAG, "Error closing stream", e)
                    }

                }
            }
            getReviewsList()
            return reviewsList
        }

        private fun getReviewsList() {
            var newReviewsJSON: JSONObject
            var newReview: Review
            try {
                val reviewsArray = JSONObject(serviceJsonStr).getJSONArray("results")

                for (i in 0..reviewsArray.length() - 1) {
                    newReviewsJSON = reviewsArray.getJSONObject(i)
                    newReview = Review(newReviewsJSON)
                    if (newReview.author != null && newReview.content != null)
                        reviewsList.add(newReview)
                }
            } catch (jse: JSONException) {
                Log.e(LOG_TAG, "Error retrieving review list", jse)
                jse.printStackTrace()
            }

        }

        override fun onPostExecute(reviews: ArrayList<Review>?) {
            super.onPostExecute(reviews)
            prgReviewsProgress.visibility = View.INVISIBLE
            if (reviews != null && reviews.size > 0) {
                reviewsArray = reviews
                lstReviews.adapter = ReviewsAdapter(activity, R.layout.review_row_item, reviews)
            } else {
                lstReviews.visibility = View.INVISIBLE
                txtReviewsError.visibility = View.VISIBLE
            }
        }
    }

    private inner class ReviewsAdapter internal constructor(internal var context: Context, internal var layoutResourceId: Int, data: ArrayList<Review>) : ArrayAdapter<Review>(context, layoutResourceId, data) {
        internal var data: ArrayList<Review>
        internal lateinit var thisReview: Review

        init {
            this.data = data
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var row = convertView
            val holder: ReviewHolder

            if (row == null) {
                val inflater = activity.layoutInflater
                row = inflater.inflate(layoutResourceId, null, false)

                holder = ReviewHolder()
                holder.txtAuthorName = row!!.findViewById(R.id.txt_reviewer_name) as TextView
                holder.txtReviewContent = row.findViewById(R.id.txt_review_content) as TextView
                holder.btnToWeb = row.findViewById(R.id.btn_read_on_web) as Button

                row.tag = holder
            } else {
                holder = row.tag as ReviewHolder
            }

            thisReview = getItem(position)
            holder.txtAuthorName!!.text = thisReview.author
            val reviewContent = thisReview.content
            holder.txtReviewContent!!.text = reviewContent
            holder.btnToWeb!!.setOnClickListener { startActivity(Intent(Intent.ACTION_VIEW, thisReview.reviewUrl)) }

            return row
        }

        internal inner class ReviewHolder {
            var txtAuthorName: TextView? = null
            var txtReviewContent: TextView? = null
            var btnToWeb: Button? = null
        }
    }

}
