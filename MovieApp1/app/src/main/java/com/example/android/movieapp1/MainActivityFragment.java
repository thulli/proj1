package com.example.android.movieapp1;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.internal.view.menu.MenuView;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static String APP_TAG = "MOVIE_APP1_MainActivityFragment";
    ArrayAdapter mPosterAdapter;
    ArrayList<MovieInfo> mvArrayList = new ArrayList<MovieInfo>();


    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if ( id == R.id.action_refresh) {
            Toast.makeText(getActivity(), "Refresh button is pressed", Toast.LENGTH_SHORT).show();
            Log.v(APP_TAG, "Refresh button executed");
           // new fetchMoviesTask().execute();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        Context context = getActivity();


        ArrayList<MovieInfo> movieInfoList = new ArrayList<MovieInfo>();


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String api_key = sharedPreferences.getString(getString(R.string.api_key), "API_KEY");
        String sort_pref = sharedPreferences.getString(getString(R.string.sort_pref), "Sorting Preference");

        if ((api_key == null) || (api_key == "")) {
            Toast.makeText(getActivity(), "Please Provide API Key from settings option",  Toast.LENGTH_LONG).show();
            return rootView;
        }

        new fetchMoviesTask().execute(api_key, sort_pref);

        mPosterAdapter = new PosterAdapter(getActivity(), R.layout.movie_posters, movieInfoList );
        GridView gridview = (GridView) rootView.findViewById(R.id.gridview_poster);
        gridview.setAdapter(mPosterAdapter);


        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getActivity(), "" + position,
                        Toast.LENGTH_SHORT).show();

                Bundle bundle = new Bundle();
                Intent detailMovieActivity = new Intent(getActivity(), MovieDetailActivity.class);
                //Log.v(APP_TAG, "The object to send is" + mvArrayList.get(position));

                bundle.putParcelable("mvInfoObj", mvArrayList.get(position));
                detailMovieActivity.putExtras(bundle);
                startActivity(detailMovieActivity);
            }
        });

        return rootView;
    }




    /* Movie class to store  the movie information */

    public static class MovieInfo implements Parcelable {

        private String movie_id;
        private String orig_title;
        private String title;
        private String overview;
        private String release_date;
        private String poster_path;
        private String vote_average;
        private String popularity;
        private String rating;

        public MovieInfo( Parcel in) {
            readFromParcel(in);
        }

        public MovieInfo(String... movieInfos) {
            this.movie_id = movieInfos[0];
            this.orig_title = movieInfos[1];
            this.title = movieInfos[2];
            this.overview = movieInfos[3];
            this.release_date = movieInfos[4];
            this.poster_path = movieInfos[5];
            this.vote_average = movieInfos[6];
            this.popularity = movieInfos[7];
            this.rating = movieInfos[8];
        }

        public String getMoviePosterLocation() {
            return this.poster_path;
        }

        public String getMovieNames() {
            return this.title;
        }

        public String getMovieOverview() {
            return this.overview;
        }

        public String getVoteAverage() {
            return this.vote_average;
        }

        public String getPopularity() {
            return this.popularity;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {

            dest.writeString(poster_path);
            dest.writeString(title);
            dest.writeString(popularity);
            dest.writeString(vote_average);
            dest.writeString(overview);

        }

        private void readFromParcel(Parcel in ) {
            poster_path = in.readString();
            title = in.readString();
            popularity = in.readString();
            vote_average = in.readString();
            overview = in.readString();
        }


        public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

            public MovieInfo createFromParcel(Parcel in ) {
                return new MovieInfo(in);
            }

            public MovieInfo[] newArray(int size) {
                return new MovieInfo[size];
            }

        };


        @Override
        public int describeContents() {
            return 0;
        }



    }

    /* Custom adapter to display image and text view */
    public class PosterAdapter extends ArrayAdapter<MovieInfo> {

        private final Context mContext;
        private final ArrayList<MovieInfo> mData;
        private final int mlayoutResourceId;

        public PosterAdapter(Context context, int layoutResourceID, ArrayList<MovieInfo> moviePosterArrayList){

            super(context, layoutResourceID, moviePosterArrayList);
            this.mContext = context;
            this.mData = moviePosterArrayList;
            this.mlayoutResourceId = layoutResourceID;

        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public MovieInfo getItem(int position) {
            return mData.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            ViewHolder viewHolder = null;

            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                LayoutInflater inflater = getActivity().getLayoutInflater();
                convertView = inflater.inflate(mlayoutResourceId, parent, false);

                viewHolder = new ViewHolder();

                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.poster_icons);
                viewHolder.textView = (TextView) convertView.findViewById(R.id.poster_name);

                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();

            }

            MovieInfo moviePoster = mData.get(position);
            viewHolder.textView.setText(moviePoster.getMovieNames());
            String poster = moviePoster.getMoviePosterLocation();
           // poster = "http://image.tmdb.org/t/p/w342//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg";
            if (poster != null) {
                Log.v(APP_TAG, poster);
            } else {
                Log.v(APP_TAG, "Image location is null");
            }
            Picasso.with(mContext).load(poster).into(viewHolder.imageView);
            // viewHolder.imageView.setImageResource(moviePoster.getMoviePoster());
            return convertView;

        }

         public class ViewHolder {
             ImageView imageView;
             TextView textView;

        }
    }


    public class fetchMoviesTask extends AsyncTask<String, Void, ArrayList<MovieInfo>> {
        ProgressDialog mdialog;

        @Override
        protected void onPreExecute() {
            mdialog = new ProgressDialog(getActivity());
            mdialog.setMessage("Downloading...");
            mdialog.setCancelable(false);
            mdialog.show();

           // super.onPreExecute();
        }

        @Override
        protected ArrayList<MovieInfo> doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader;
            URL url = null;

            try {
                final String API_KEY_PARAM="api_key";
                final String API_KEY = params[0];
                Log.v(APP_TAG, "the api key is " + API_KEY);
                Log.v(APP_TAG, "the sorting order is " + params[1]);
                final String POPULAR_PARAM;

                final String SORT_KEY = "sort_by";
                if (params[1].equalsIgnoreCase("most_popular")) {
                    POPULAR_PARAM = "popularity.desc";
                } else {
                    POPULAR_PARAM = "vote_average.desc";
                }

                final String CERTIFICATION_KEY = "certification_country";
                final String CERTIFICATION_VAL = "US";
                final String MOVIEDB_BASE_URL =
                        "https://api.themoviedb.org/3/discover/movie";

                Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                        .appendQueryParameter(CERTIFICATION_KEY, CERTIFICATION_VAL)
                        .appendQueryParameter(SORT_KEY, POPULAR_PARAM)
                        .appendQueryParameter(API_KEY_PARAM, API_KEY)
                        .build();

                try {
                    url = new URL(builtUri.toString());
                    Log.v(APP_TAG, "Built URI " + builtUri.toString());
                } catch (MalformedURLException e) {
                    Log.e(APP_TAG, "Unable to build url" , e);
                    return null;
                }


                try {
                    // Create the request to OpenWeatherMap, and open the connection
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();
                } catch (ProtocolException e) {
                    Log.e(APP_TAG, "Unable to connect to the move db" , e);
                    return null;
                } catch (FileNotFoundException e) {
                    Log.e(APP_TAG, "Check the API Key");
                    return null;
                }

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

               // Log.d("APP_TAG", buffer.toString());
                try {
                    return getMovieObjFromJson(buffer.toString());
                } catch (JSONException err) {
                    Log.e(APP_TAG, "Json exception");
                }

            } catch (IOException e) {
                Log.e(APP_TAG, "Error in getting movies", e);
                return null;
            }


            return null;
        }


        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */


        private ArrayList<MovieInfo> getMovieObjFromJson(String movieJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String MOVIE_LIST = "results";
            final String ID = "id";
            final String ORG_TITLE = "original_title";
            final String TITLE = "title";
            final String OVERVIEW = "overview";
            final String RELEASE_DATE = "release_date";
            final String POSTER_PATH = "poster_path";
            final String VOTE = "vote_average";
            final String POPULARITY = "popularity";
            final String Rating = "adult";

            String movie_id, rating, org_title, title, overview, rel_date, poster_path, vote, popularity;


            JSONObject forecastJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = forecastJson.getJSONArray(MOVIE_LIST);
            final String MOVIE_POSTER_URL = "http://image.tmdb.org/t/p/w342//";
            Uri posterUri = Uri.parse(MOVIE_POSTER_URL);

            for (int i = 0; i < movieArray.length(); i++) {

                Log.v(APP_TAG, movieArray.get(i).toString());

                // Get the JSON object representing a movide
                JSONObject movie = movieArray.getJSONObject(i);
                movie_id = movie.getString(ID);
                org_title = movie.getString(ORG_TITLE);
                title = movie.getString(TITLE);
                overview = movie.getString(OVERVIEW);
                rel_date = movie.getString(RELEASE_DATE);
                poster_path = movie.getString(POSTER_PATH);
                poster_path = MOVIE_POSTER_URL + poster_path;
                vote = movie.getString(VOTE);
                popularity = movie.getString(POPULARITY);
                rating = movie.getString(Rating);

                mvArrayList.add(new MovieInfo(movie_id, org_title, title, overview,
                                                rel_date, poster_path, vote, popularity, rating));
                mdialog.dismiss();

            }
            return mvArrayList;

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(ArrayList<MovieInfo> mvArrayList) {
            if (mvArrayList == null) {
                Log.v(APP_TAG, "Nothing to add");
                mdialog.dismiss();
                Toast.makeText(getActivity(), "API KEY IS INVALID, add correct API KEY in settings menu", Toast.LENGTH_LONG).show();
            } else {
                mPosterAdapter.clear();
                mPosterAdapter.addAll(mvArrayList);
            }
        }
    }

}
