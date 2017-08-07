package com.jcorpac.udacity.popularmovies;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jcorpac.udacity.popularmovies.model.Trailer;

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

public class TrailersFragment extends Fragment{

    private ListView lstTrailers;
    private ProgressBar prgTrailersProgress;
    private TextView txtTrailersError;

    public TrailersFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_trailers, container, false);

        Intent incomingIntent = getActivity().getIntent();
        Uri trailersUri;
        String movieTitle;
        txtTrailersError = (TextView)rootView.findViewById(R.id.txt_trailers_error);
        prgTrailersProgress = (ProgressBar)rootView.findViewById(R.id.prg_trailers_progress);
        if(incomingIntent != null) {
            trailersUri = incomingIntent.getParcelableExtra(TrailersActivity.TRAILERS_URI_TAG);
            movieTitle = incomingIntent.getStringExtra(TrailersActivity.TRAILERS_MOVIE_TAG);
            getActivity().setTitle(movieTitle + " trailers");
            new GetTrailersTask().execute(trailersUri);
        }
        lstTrailers = (ListView)rootView.findViewById(R.id.lst_trailers);
        return rootView;
    }

    private class GetTrailersTask extends AsyncTask<Uri, Void, ArrayList<Trailer>> {

        final String LOG_TAG = this.getClass().getSimpleName();

        String serviceJsonStr = null;
        ArrayList<Trailer> trailers = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prgTrailersProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Trailer> doInBackground(Uri... params) {

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
            getTrailerList();
            return trailers;
        }

        private void getTrailerList() {
            JSONObject newTrailerJSON;
            Trailer newTrailer;
            try {
                JSONArray trailersArray =  new JSONObject(serviceJsonStr).getJSONArray("results");

                for(int i = 0; i<trailersArray.length(); i++){
                    newTrailerJSON = trailersArray.getJSONObject(i);
                    newTrailer = new Trailer(newTrailerJSON);
                    if(newTrailer.getVideoId() != null && newTrailer.getTrailerName() != null)
                        trailers.add(newTrailer);
                }
            } catch (JSONException jse) {
                Log.e(LOG_TAG, "Error retrieving movie list", jse);
                jse.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Trailer> trailers) {
            super.onPostExecute(trailers);
            prgTrailersProgress.setVisibility(View.INVISIBLE);
            if (trailers != null && trailers.size() > 0) {
                lstTrailers.setMinimumHeight(trailers.size()*50);
                TrailerAdapter adapter = new TrailerAdapter(getActivity(), R.layout.trailer_row_item, trailers);
                lstTrailers.setAdapter(adapter);
            }
            else {
                lstTrailers.setVisibility(View.INVISIBLE);
                txtTrailersError.setVisibility(View.VISIBLE);
            }
        }
    }

    private class TrailerAdapter extends ArrayAdapter<Trailer> {

        Context context;
        int layoutResourceId;
        ArrayList<Trailer> data = null;
        Trailer trailer;

        TrailerAdapter(Context context, int layoutResourceId, ArrayList<Trailer> data) {
            super(context, layoutResourceId, data);
            this.context = context;
            this.data = data;
            this.layoutResourceId = layoutResourceId;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View row = convertView;
            TrailerHolder holder;

            if(row == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                row = inflater.inflate(layoutResourceId, null, false);

                holder = new TrailerHolder();
                holder.titleText = (TextView)row.findViewById(R.id.txt_trailer_name);
                holder.playButton = (ImageButton)row.findViewById(R.id.btn_play_trailer);
                holder.shareButton = (ImageButton)row.findViewById(R.id.btn_share_trailer);

                row.setTag(holder);
            } else {
                holder = (TrailerHolder)row.getTag();
            }

            trailer = getItem(position);
            holder.titleText.setText(trailer.getTrailerName());
            holder.playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playYoutubeVideo(trailer.getVideoId());
                }
            });
            holder.shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareYoutubeVideo(trailer.getVideoId());
                }
            });

            return row;
        }

        private void playYoutubeVideo(String videoId) {
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId));
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + videoId));
            try {
                startActivity(appIntent);
            } catch (ActivityNotFoundException ex) {
                startActivity(webIntent);
            }
        }

        private void shareYoutubeVideo(String videoId) {
            String message = "Hey! Here's a video that you might like! http://www.youtube.com/watch?v=" + videoId;
            Intent shareIntent = ShareCompat.IntentBuilder.from(getActivity())
                    .setType("text/plain")
                    .setText(message)
                    .getIntent();
            shareIntent.setAction(Intent.ACTION_SEND);
            startActivity(shareIntent);
        }

        class TrailerHolder {
            TextView titleText;
            ImageButton playButton, shareButton;
        }
    }
}
