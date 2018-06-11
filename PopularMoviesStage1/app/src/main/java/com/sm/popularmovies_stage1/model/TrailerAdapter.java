package com.sm.popularmovies_stage1.model;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.VideoView;

import com.sm.popularmovies_stage1.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TrailerAdapter extends ArrayAdapter<TrailerVideo> {
    private Context mContext;
    private List<TrailerVideo> mVideos;
    public TrailerAdapter(@NonNull Context context, @NonNull List<TrailerVideo> objects) {
        super(context, R.layout.trailer_item_layout, objects);
        mContext = context;
        mVideos = objects;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;


        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.trailer_item_layout, null);
            holder = new ViewHolder();

            holder.movieTrailerPosterThumbnail = (ImageButton) convertView
                    .findViewById(R.id.videoView);

            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();

        }
        /***get clicked view and play video url at this position**/
        holder.movieTrailerPosterThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchVideoIntent(position);
            }
        });
        PicassoClient.downloadImage(mContext, getthumbnailUrl(position), holder.movieTrailerPosterThumbnail);
        return convertView;
    }

    public static class ViewHolder {
        ImageButton movieTrailerPosterThumbnail;
    }

    private void populateImagePoster(View imageView, String imageUrl, final int position) {
        Picasso.with(mContext)
                .load(imageUrl)
                .fit()
                .into((ImageView) imageView);
    }

    private String getthumbnailUrl(int position) {
        TrailerVideo video = mVideos.get(position);
        String thumbnailUrl = "https://i.ytimg.com/vi/"+ video.getKey() + "/0.jpg";
        return thumbnailUrl;
    }

    private String getTrailerUrl(int position) {
        TrailerVideo video = mVideos.get(position);
        String url = "http://www.youtube.com/watch?v=" + video.getKey(); // your URL here
        return url;
    }

    private void launchVideoIntent(int position) {
        TrailerVideo video = mVideos.get(position);
        watchYoutubeVideo(video.getKey());
    }

    private void watchYoutubeVideo(String id){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            mContext.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            mContext.startActivity(webIntent);
        }
    }


}
