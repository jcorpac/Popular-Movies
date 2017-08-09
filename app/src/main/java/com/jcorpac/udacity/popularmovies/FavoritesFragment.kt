package com.jcorpac.udacity.popularmovies

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridView
import android.widget.TextView
import com.jcorpac.udacity.popularmovies.data.FavoritesContract.FavoritesEntry
import com.jcorpac.udacity.popularmovies.model.Movie
import java.util.*


class FavoritesFragment : Fragment() {

    internal lateinit var favMoviesList: Array<Movie?>
    internal lateinit var thumbnailArray: Array<String?>

    internal var mCurrentPosition: Int = 0
    private val CURRENT_POSITION_TAG = "mCurrentPosition"

    private var movieAdapter: ImageAdapter? = null
    private lateinit var posterLayout: GridView
    private lateinit var txtErrorMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_favorites, container, false)
        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt(CURRENT_POSITION_TAG)
        }

        txtErrorMessage = rootView.findViewById(R.id.txt_favorites_error) as TextView

        posterLayout = rootView.findViewById(R.id.poster_layout) as GridView
        posterLayout.adapter = movieAdapter
        posterLayout.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ -> startActivity(DetailActivity.newIntent(activity, favMoviesList[position])) }
        posterLayout.smoothScrollToPosition(mCurrentPosition)
        return rootView
    }

    override fun onResume() {
        super.onResume()
        updateMovies()
    }

    private fun updateMovies() {
        val favMoviesCursor = activity.contentResolver.query(FavoritesEntry.CONTENT_URI, null, null, null,
                FavoritesEntry._ID)

        if (favMoviesCursor == null || favMoviesCursor.count == 0) {
            posterLayout.visibility = View.INVISIBLE
            txtErrorMessage.visibility = View.VISIBLE
        } else {
            favMoviesList = arrayOfNulls<Movie>(favMoviesCursor.count)
            thumbnailArray = arrayOfNulls<String>(favMoviesCursor.count)
            var movieTitle: String
            var movieSummary: String
            var posterURL: String
            var releaseDate: String
            var movieID: String
            var movieVoteAverage: Double
            var newMovie: Movie
            var movieIndex = 0

            try {
                while (favMoviesCursor.moveToNext()) {
                    movieTitle = favMoviesCursor.getString(favMoviesCursor.getColumnIndex(FavoritesEntry.COLUMN_TITLE))
                    movieSummary = favMoviesCursor.getString(favMoviesCursor.getColumnIndex(FavoritesEntry.COLUMN_SUMMARY))
                    posterURL = favMoviesCursor.getString(favMoviesCursor.getColumnIndex(FavoritesEntry.COLUMN_POSTER_URL))
                    releaseDate = favMoviesCursor.getString(favMoviesCursor.getColumnIndex(FavoritesEntry.COLUMN_RELEASE_DATE))
                    movieVoteAverage = favMoviesCursor.getDouble(favMoviesCursor.getColumnIndex(FavoritesEntry.COLUMN_VOTE_AVERAGE))
                    movieID = favMoviesCursor.getString(favMoviesCursor.getColumnIndex(FavoritesEntry.COLUMN_MOVIE_ID))

                    newMovie = Movie(movieTitle, movieSummary, posterURL, releaseDate, movieVoteAverage, movieID)
                    this.favMoviesList[movieIndex] = newMovie
                    this.thumbnailArray[movieIndex] = newMovie.posterThumbnailURL

                    movieIndex++
                }
            } finally {
                favMoviesCursor.close()
            }

            val thumbnailArrayList = ArrayList(Arrays.asList(*thumbnailArray))
            movieAdapter = ImageAdapter(activity, thumbnailArrayList)
            movieAdapter?.notifyDataSetChanged()
            posterLayout.adapter = movieAdapter
            posterLayout.smoothScrollToPosition(mCurrentPosition)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState!!.putInt(CURRENT_POSITION_TAG, posterLayout.firstVisiblePosition)
    }
}
