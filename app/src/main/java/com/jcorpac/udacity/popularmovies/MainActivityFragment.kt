package com.jcorpac.udacity.popularmovies

import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.GridView
import android.widget.ProgressBar
import android.widget.TextView
import com.jcorpac.udacity.popularmovies.model.Movie
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class MainActivityFragment : Fragment() {
    internal lateinit var moviesList: Array<Movie?>
    internal lateinit var thumbnailArray: Array<String?>

    internal var mCurrentPosition: Int = 0
    private val CURRENT_POSITION_TAG = "mCurrentPosition"

    private var movieAdapter: ImageAdapter? = null
    private lateinit var posterLayout: GridView
    private lateinit var prgMoviesProgress: ProgressBar
    private lateinit var txtMoviesError: TextView

    init {
        setHasOptionsMenu(true)
    }

    override fun onStart() {
        super.onStart()
        updateMovies()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_main, container, false)

        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt(CURRENT_POSITION_TAG)
        }

        posterLayout = rootView.findViewById(R.id.poster_layout) as GridView
        posterLayout.adapter = movieAdapter
        posterLayout.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ -> startActivity(DetailActivity.newIntent(activity, moviesList[position])) }

        prgMoviesProgress = rootView.findViewById(R.id.prg_movies_progress) as ProgressBar
        txtMoviesError = rootView.findViewById(R.id.txt_movies_error) as TextView

        posterLayout.smoothScrollToPosition(mCurrentPosition)
        return rootView
    }

    private fun updateMovies() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        val sortMoviesBy = prefs.getString(getString(R.string.sort_by_key), getString(R.string.default_sort_value))

        GetMoviesTask().execute(sortMoviesBy)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater!!.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val itemId = item?.itemId
        when (itemId) {
            R.id.action_settings -> {
                startActivity(Intent(context, SettingsActivity::class.java))
                return true
            }
            R.id.action_favorites -> {
                startActivity(Intent(context, FavoritesActivity::class.java))
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt(CURRENT_POSITION_TAG, posterLayout.firstVisiblePosition)
    }

    private inner class GetMoviesTask : AsyncTask<String, Void, Array<Movie?>?>() {

        internal val LOG_TAG = this.javaClass.simpleName

        internal var serviceJsonStr: String? = null
        internal var movies: Array<Movie?>? = null

        override fun onPreExecute() {
            super.onPreExecute()
            prgMoviesProgress.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg params: String): Array<Movie?>? {

            var urlConnection: HttpURLConnection? = null
            var reader: BufferedReader? = null

            val API_PARAM = "api_key"
            val SORT_BY_PARAM = "sort_by"
            val serviceEndpoint: Uri

            val sortByParam = params[0]
            when (sortByParam) {
                "popularity" -> serviceEndpoint = Uri.parse(Constants.POPULAR_MOVIES_URL).buildUpon()
                        .appendQueryParameter(API_PARAM, Constants.API_KEY)
                        .build()
                "vote_average" -> serviceEndpoint = Uri.parse(Constants.TOP_RATED_MOVIES_URL).buildUpon()
                        .appendQueryParameter(API_PARAM, Constants.API_KEY)
                        .build()
                else -> serviceEndpoint = Uri.parse(Constants.MOVIES_SERVICE_URL).buildUpon()
                        .appendQueryParameter(SORT_BY_PARAM, sortByParam + ".desc")
                        .appendQueryParameter(API_PARAM, Constants.API_KEY)
                        .build()
            }

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

            getMovieList()

            return movies
        }

        private fun getMovieList() {
            var currentMovie: JSONObject
            try {
                val moviesArray = JSONObject(serviceJsonStr).getJSONArray("results")
                movies = arrayOfNulls<Movie>(moviesArray.length())
                thumbnailArray = arrayOfNulls<String>(moviesArray.length())

                for (i in 0..moviesArray.length() - 1) {
                    currentMovie = moviesArray.getJSONObject(i)
                    movies!![i] = Movie(currentMovie)
                    thumbnailArray[i] = movies!![i]?.posterThumbnailURL
                }
            } catch (jse: JSONException) {
                Log.e(LOG_TAG, "Error retrieving movie list", jse)
                jse.printStackTrace()
            }

        }

        override fun onPostExecute(movies: Array<Movie?>?) {
            super.onPostExecute(movies)
            prgMoviesProgress.visibility = View.INVISIBLE
            if (movies != null) {
                moviesList = movies

                val thumbnailArrayList = ArrayList(Arrays.asList(*thumbnailArray))
                movieAdapter = ImageAdapter(activity, thumbnailArrayList)
                movieAdapter?.notifyDataSetChanged()
                posterLayout.adapter = movieAdapter
                posterLayout.smoothScrollToPosition(mCurrentPosition)
            } else {
                posterLayout.visibility = View.INVISIBLE
                txtMoviesError.visibility = View.VISIBLE
            }
        }
    }

}
