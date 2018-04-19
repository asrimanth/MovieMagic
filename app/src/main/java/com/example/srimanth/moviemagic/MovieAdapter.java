package com.example.srimanth.moviemagic;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> mMovieList;
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public MovieAdapter(Context context)
    {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mMovieList = new ArrayList<>();
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = mLayoutInflater.inflate(R.layout.row_movie,parent,false);

        return new MovieViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {

        Movie movie = mMovieList.get(position);

        Picasso.with(mContext)
                .load(movie.getPoster())
                .placeholder(R.color.colorAccent)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {

        return (mMovieList == null) ? 0 : mMovieList.size();

    }

    public void setMovieList(List<Movie> movieList) {
        this.mMovieList.clear();
        this.mMovieList.addAll(movieList);
        // The adapter needs to know that the data has changed. If we don't call this, app will crash.
        notifyDataSetChanged();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        MovieViewHolder(View itemView)
        {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }

    }
}
