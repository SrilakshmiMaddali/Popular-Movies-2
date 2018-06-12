package com.sm.popularmovies_stage1.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sm.popularmovies_stage1.R;

import java.util.List;

public class ReviewAdapter extends ArrayAdapter<Review> {
    private Context mContext;
    private List<Review> mReviews;
    public ReviewAdapter(@NonNull Context context, @NonNull List<Review> objects) {
        super(context, R.layout.review_item_layout, objects);
        mContext = context;
        mReviews = objects;
    }

    public static class ViewHolder {
        TextView author;
        TextView content;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.review_item_layout, null);
            holder = new ViewHolder();

            holder.author = (TextView) convertView
                    .findViewById(R.id.author);
            holder.content = (TextView) convertView
                    .findViewById(R.id.content);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }
}
