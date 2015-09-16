package com.example.android.movieapp1;

import android.content.Intent;
import android.graphics.Color;
import android.media.Rating;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment {
    final static String APP_ID = "Movie Detail FRAG_APP";

    public MovieDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String overview;
        String title;
        String vote_average;
        String popularity;
        String poster_path;



        Bundle bundle = getActivity().getIntent().getExtras();
        MainActivityFragment.MovieInfo mvInfo = bundle.getParcelable("mvInfoObj");


        overview = mvInfo.getMovieOverview();
        vote_average = mvInfo.getVoteAverage();
        popularity = mvInfo.getPopularity();
        title = mvInfo.getMovieNames();
        poster_path = mvInfo.getMoviePosterLocation();


        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        TextView movieName = (TextView) rootView.findViewById(R.id.movie_name);
        ImageView movieImage = (ImageView) rootView.findViewById(R.id.movie_poster);
        RatingBar movieRating = (RatingBar) rootView.findViewById(R.id.rating_bar);

       // movieRating.setRating(Float.parseFloat(rating));

        Log.v(APP_ID, "The vote average is " +  vote_average);
        movieRating.setRating(Float.parseFloat(vote_average) / 2);
        //movieRating.setNumStars((Integer.parseInt(rating))/2);

        Picasso.with(getActivity().getApplicationContext()).load(poster_path).into(movieImage);

        TextView overView = (TextView) rootView.findViewById(R.id.movie_overview);
        //TextView popularityView = (TextView) rootView.findViewById(R.id.popularity);

        movieName.setText(mvInfo.getMovieNames());
        overView.setText(overview);
       // popularityView.setText(popularity);



        //movieName.setText(mvInfo.getMovieNames());

        return rootView;


    }
}
