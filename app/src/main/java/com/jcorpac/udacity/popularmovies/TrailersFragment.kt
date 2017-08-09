package com.jcorpac.udacity.popularmovies


import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v4.app.ShareCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.jcorpac.udacity.popularmovies.model.Trailer
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class TrailersFragment : Fragment() {

    private lateinit var lstTrailers: ListView
    private lateinit var prgTrailersProgress: ProgressBar
    private lateinit var txtTrailersError: TextView

    private val LIST_VIEW_STATE_TAG = "listViewState"
    private val TRAILER_ARRAY_LIST = "trailersArray"
    internal var trailerArray: ArrayList<Trailer>? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_trailers, container, false)

        lstTrailers = rootView.findViewById(R.id.lst_trailers) as ListView
        if (savedInstanceState != null) {
            trailerArray = savedInstanceState.getParcelableArrayList<Trailer>(TRAILER_ARRAY_LIST)
            lstTrailers.adapter = TrailerAdapter(activity, R.layout.trailer_row_item, trailerArray!!)
            lstTrailers.onRestoreInstanceState(savedInstanceState.getParcelable<Parcelable>(LIST_VIEW_STATE_TAG))
        }

        val incomingIntent = activity.intent
        val trailersUri: Uri
        val movieTitle: String
        txtTrailersError = rootView.findViewById(R.id.txt_trailers_error) as TextView
        prgTrailersProgress = rootView.findViewById(R.id.prg_trailers_progress) as ProgressBar
        if (incomingIntent != null && trailerArray == null) {
            trailersUri = incomingIntent.getParcelableExtra<Uri>(TrailersActivity.TRAILERS_URI_TAG)
            movieTitle = incomingIntent.getStringExtra(TrailersActivity.TRAILERS_MOVIE_TAG)
            activity.title = movieTitle + " trailers"
            GetTrailersTask().execute(trailersUri)
        }
        return rootView
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState!!.putParcelable(LIST_VIEW_STATE_TAG, lstTrailers.onSaveInstanceState())
        outState.putParcelableArrayList(TRAILER_ARRAY_LIST, trailerArray)
    }

    private inner class GetTrailersTask : AsyncTask<Uri, Void, ArrayList<Trailer>>() {

        internal val LOG_TAG = this.javaClass.simpleName

        internal var serviceJsonStr: String? = null
        internal var trailers = ArrayList<Trailer>()

        override fun onPreExecute() {
            super.onPreExecute()
            prgTrailersProgress.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg params: Uri): ArrayList<Trailer>? {

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
            getTrailerList()
            return trailers
        }

        private fun getTrailerList() {
            var newTrailerJSON: JSONObject
            var newTrailer: Trailer
            try {
                val trailersArray = JSONObject(serviceJsonStr).getJSONArray("results")

                for (i in 0..trailersArray.length() - 1) {
                    newTrailerJSON = trailersArray.getJSONObject(i)
                    newTrailer = Trailer(newTrailerJSON)
                    if (newTrailer.videoId != null && newTrailer.trailerName != null)
                        trailers.add(newTrailer)
                }
            } catch (jse: JSONException) {
                Log.e(LOG_TAG, "Error retrieving movie list", jse)
                jse.printStackTrace()
            }

        }

        override fun onPostExecute(trailers: ArrayList<Trailer>?) {
            super.onPostExecute(trailers)
            prgTrailersProgress.visibility = View.INVISIBLE
            if (trailers != null && trailers.size > 0) {
                trailerArray = trailers
                lstTrailers.adapter = TrailerAdapter(activity, R.layout.trailer_row_item, trailers)
            } else {
                lstTrailers.visibility = View.INVISIBLE
                txtTrailersError.visibility = View.VISIBLE
            }
        }
    }

    private inner class TrailerAdapter internal constructor(internal var context: Context, internal var layoutResourceId: Int, data: ArrayList<Trailer>) : ArrayAdapter<Trailer>(context, layoutResourceId, data) {
        internal var data: ArrayList<Trailer>? = null
        internal lateinit var trailer: Trailer

        init {
            this.data = data
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var row = convertView
            val holder: TrailerHolder

            if (row == null) {
                val inflater = activity.layoutInflater
                row = inflater.inflate(layoutResourceId, null, false)

                holder = TrailerHolder()
                holder.titleText = row!!.findViewById(R.id.txt_trailer_name) as TextView
                holder.playButton = row.findViewById(R.id.btn_play_trailer) as ImageButton
                holder.shareButton = row.findViewById(R.id.btn_share_trailer) as ImageButton

                row.tag = holder
            } else {
                holder = row.tag as TrailerHolder
            }

            trailer = getItem(position)
            holder.titleText!!.text = trailer.trailerName
            holder.playButton!!.setOnClickListener { playYoutubeVideo(trailer.videoId) }
            holder.shareButton!!.setOnClickListener { shareYoutubeVideo(trailer.videoId) }

            return row
        }

        private fun playYoutubeVideo(videoId: String?) {
            val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId))
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + videoId))
            try {
                startActivity(appIntent)
            } catch (ex: ActivityNotFoundException) {
                startActivity(webIntent)
            }

        }

        private fun shareYoutubeVideo(videoId: String?) {
            val message = "Hey! Here's a video that you might like! http://www.youtube.com/watch?v=" + videoId
            val shareIntent = ShareCompat.IntentBuilder.from(activity)
                    .setType("text/plain")
                    .setText(message)
                    .intent
            shareIntent.action = Intent.ACTION_SEND
            startActivity(shareIntent)
        }

        internal inner class TrailerHolder {
            var titleText: TextView? = null
            var playButton: ImageButton? = null
            var shareButton: ImageButton? = null
        }
    }
}
