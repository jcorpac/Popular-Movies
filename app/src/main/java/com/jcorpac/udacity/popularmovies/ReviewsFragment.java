package com.jcorpac.udacity.popularmovies;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jcorpac.udacity.popularmovies.model.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReviewsFragment extends Fragment {

    ListView lstReviews;

    ProgressBar prgReviewsProgress;
    TextView txtReviewsError;

    public ReviewsFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reviews, container, false);

        Intent incomingIntent = getActivity().getIntent();
        Uri reviewsUri;
        String movieTitle;
        prgReviewsProgress = (ProgressBar)rootView.findViewById(R.id.prg_reviews_progress);
        txtReviewsError = (TextView)rootView.findViewById(R.id.txt_reviews_error);
        if(incomingIntent != null) {
            reviewsUri = incomingIntent.getParcelableExtra(ReviewsActivity.REVIEWS_URI_TAG);
            Log.d("REVIEWS", "onCreateView: " + reviewsUri.toString());
            movieTitle = incomingIntent.getStringExtra(ReviewsActivity.REVIEWS_MOVIE_TAG);
            getActivity().setTitle(movieTitle + " reviews");
            new GetReviewsTask().execute(reviewsUri);
        }
        lstReviews = (ListView)rootView.findViewById(R.id.lst_reviews);

        return rootView;
    }

    private class GetReviewsTask extends AsyncTask<Uri, Void, ArrayList<Review>> {

        final String LOG_TAG = this.getClass().getSimpleName();

        String serviceJsonStr = null;
        ArrayList<Review> reviewsList = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prgReviewsProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Review> doInBackground(Uri... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            Uri serviceEndpoint = params[0];

            try {
                URL serviceURL = new URL(serviceEndpoint.toString());
                urlConnection = (HttpURLConnection) serviceURL.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                serviceJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);

                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            getReviewsList();
            return reviewsList;
        }

        private void getReviewsList() {
            JSONObject newReviewsJSON;
            Review newReview;
            try {
                JSONArray reviewsArray = new JSONObject(serviceJsonStr).getJSONArray("results");

                for(int i = 0; i< reviewsArray.length(); i++){
                    newReviewsJSON = reviewsArray.getJSONObject(i);
                    newReview = new Review(newReviewsJSON);
                    if(newReview.getAuthor() != null && newReview.getContent() != null)
                        reviewsList.add(newReview);
                }
            } catch (JSONException jse) {
                Log.e(LOG_TAG, "Error retrieving review list", jse);
                jse.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Review> reviews) {
            super.onPostExecute(reviews);
            prgReviewsProgress.setVisibility(View.INVISIBLE);
            if (reviews != null && reviews.size() > 0) {
                ReviewsAdapter adapter = new ReviewsAdapter(getActivity(), R.layout.review_row_item, reviews);
                lstReviews.setAdapter(adapter);
            } else {
                lstReviews.setVisibility(View.INVISIBLE);
                txtReviewsError.setVisibility(View.VISIBLE);
            }
        }
    }

    private class ReviewsAdapter extends ArrayAdapter<Review> {

        Context context;
        int layoutResourceId;
        ArrayList<Review> data = null;
        Review thisReview;

        ReviewsAdapter(Context context, int layoutResourceId, ArrayList<Review> data) {
            super(context, layoutResourceId, data);
            this.context = context;
            this.data = data;
            this.layoutResourceId = layoutResourceId;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View row = convertView;
            ReviewHolder holder;

            if(row == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                row = inflater.inflate(layoutResourceId, null, false);

                holder = new ReviewHolder();
                holder.txtAuthorName = (TextView)row.findViewById(R.id.txt_reviewer_name);
                holder.txtReviewContent = (TextView) row.findViewById(R.id.txt_review_content);
                holder.btnToWeb = (Button) row.findViewById(R.id.btn_read_on_web);

                row.setTag(holder);
            } else {
                holder = (ReviewHolder) row.getTag();
            }

            thisReview = getItem(position);
            holder.txtAuthorName.setText(thisReview.getAuthor());
            String reviewContent = thisReview.getContent();
            if (reviewContent.length() > 1000){
                reviewContent = reviewContent.substring(0, 1000) + "...";
            }
            holder.txtReviewContent.setText(reviewContent);
            holder.btnToWeb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_VIEW, thisReview.getReviewUrl()));
                }
            });

            return row;
        }

        class ReviewHolder {
            TextView txtAuthorName, txtReviewContent;
            Button btnToWeb;
        }
    }

}
