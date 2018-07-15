package com.sm.popularmovies_stage1.model;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sm.popularmovies_stage1.R;
import com.sm.popularmovies_stage1.database.MovieContract;
import com.sm.popularmovies_stage1.ui.MainActivity;
import com.sm.popularmovies_stage1.ui.MovieDetailsActivity;

public class MoviesListCursorAdapter extends CursorAdapter {
    LayoutInflater mInflater;
    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w500/";
    private static final String TAG = "MoviesListCursorAdapter";
    public MoviesListCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
         return  mInflater.inflate(R.layout.grid_view_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        String path = cursor.getString(cursor.getColumnIndex(MovieContract.Movie.POSTER_PATH));
        String posterPath = getImagePath(path);
        ViewHolder holder = new ViewHolder();
        ImageView posterImage = (ImageView) view.findViewById(R.id.poster);
        posterImage.setTag(cursor.getPosition());
        if (posterPath != null) {
            PicassoClient.downloadImage(mContext, posterPath, posterImage);
        }
    }

    private String getImagePath(String posterPath) {
        if (posterPath != null && !posterPath.isEmpty()) {
            return IMAGE_BASE_URL + posterPath;
        } else {
            return null;
        }
    }

    @Override
    public int getCount() {
        if (getCursor() == null) {
            return 0;
        }
        return super.getCount();
    }

    static class ViewHolder {
        ImageView imageView;
    }
}
