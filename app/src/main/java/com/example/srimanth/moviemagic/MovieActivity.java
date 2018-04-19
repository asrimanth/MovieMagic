package com.example.srimanth.moviemagic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MovieActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    MovieAdapter mMovieAdapter;
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        mProgressBar = findViewById(R.id.movieProgressBar);

        enablePorgressBar();
        mRecyclerView = findViewById(R.id.recyclerView);

        mRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        mMovieAdapter = new MovieAdapter(this);
        List<Movie> movies = new ArrayList<>();

        for (int i=0; i<25;i++)
        {
            movies.add(new Movie());
        }
        mMovieAdapter.setMovieList(movies);

        mRecyclerView.setAdapter(mMovieAdapter);

        restApiCall();

        mProgressBar.setVisibility(View.GONE);

    }

    private void restApiCall()
    {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://api.themoviedb.org/3")
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addEncodedQueryParam("api_key", "cd0b2d73bba4b146808b38697efc18b2");
                    }
                })
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        MoviesApiService service = restAdapter.create(MoviesApiService.class);
        service.getPopularMovies(new Callback<Movie.MovieResult>() {
            @Override
            public void success(Movie.MovieResult movieResult, Response response) {
                mMovieAdapter.setMovieList(movieResult.getResults());
                Log.i("API","api call success");
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    private void enablePorgressBar()
    {
        mProgressBar.setIndeterminate(true);
        mProgressBar.setVisibility(View.VISIBLE);
    }
}
