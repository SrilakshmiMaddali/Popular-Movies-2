package com.sm.popularmovies_stage1.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.sm.popularmovies_stage1.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Adapter object to render movies in grid view.
 */
public class CustomAdapter extends ArrayAdapter<Movies> {
    private List<Movies> mMoviesList;
    private LayoutInflater mInflater;
    private ViewHolder mHolder;
    private Context mContext;
    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w500/";
    private static final String TAG = "CustomAdapter";


    private class ViewHolder {
        ImageView moviePoster;

    }

    public CustomAdapter(@NonNull Context context, @NonNull List<Movies> moviesList) {
        super(context, R.layout.grid_view_item, moviesList);
        mContext = context;
        Log.d(TAG, moviesList.toString());
        mMoviesList = moviesList;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View recordView = convertView;
        if (recordView == null) {
            mHolder = new ViewHolder();
            Log.d(TAG, mMoviesList.get(position).getmPosterPath());
            recordView = mInflater.inflate(R.layout.grid_view_item, null);
            mHolder.moviePoster = (ImageView) recordView.findViewById(R.id.poster);
            recordView.setTag(mHolder);

        }
        ViewHolder holder = (ViewHolder) recordView.getTag();
        String url = loadImage(position);
        //populateImagePoster( holder.moviePoster, url, position);
        PicassoClient.downloadImage(mContext, url, holder.moviePoster);
        return recordView;
    }

    private void populateImagePoster(View imageView, String imageUrl, final int position) {
        Picasso.with(mContext)
                .load(imageUrl)
                .fit()
                .into((ImageView) imageView);
    }


    private String loadImage(int position) {
        return IMAGE_BASE_URL + mMoviesList.get(position).getmPosterPath();
    }
}