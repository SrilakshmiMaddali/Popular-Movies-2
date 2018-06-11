package com.sm.popularmovies_stage1.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Movies implements Parcelable{
    @SerializedName("vote_count")
    private int mVoteCount;
    @SerializedName("id")
    private String mId;
    @SerializedName("video")
    private String mVideo;
    @SerializedName("vote_average")
    private String mVoteAverage;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("popularity")
    private String mPopularity;
    @SerializedName("poster_path")
    private String mPosterPath;
    @SerializedName("original_language")
    private String mOriginalLanguage;
    @SerializedName("original_title")
    private String mOriginalTitle;
    @SerializedName("backdrop_path")
    private String mBackdropPath;
    @SerializedName("adult")
    private boolean mAdult;
    @SerializedName("overview")
    private String mOverview;
    @SerializedName("release_date")
    private String mReleaseDate;

    public Movies(int votecount,String id,String video,String voteaverage,String title,String popularity,String posterpath,String originallanguage,String originaltitle,
                  String backdroppath,boolean adult,String overview,String releasedate){
        this.mVoteCount=votecount;
        this.mId=id;
        this.mVideo=video;
        this.mVoteAverage=voteaverage;
        this.mTitle=title;
        this.mPopularity=popularity;
        this.mPosterPath=posterpath;
        this.mOriginalLanguage=originallanguage;
        this.mOriginalTitle=originaltitle;
        this.mBackdropPath=backdroppath;
        this.mAdult=adult;
        this.mOverview=overview;
        this.mReleaseDate=releasedate;
    }


    public Movies(Parcel in) {
        this.mVoteCount = in.readInt();
        this.mId = in.readString();
        this.mVideo = in.readString();
        this.mVoteAverage = in.readString();
        this.mTitle = in.readString();
        this.mPopularity = in.readString();
        this.mPosterPath = in.readString();
        this.mOriginalLanguage = in.readString();
        this.mOriginalTitle = in.readString();
        this.mBackdropPath = in.readString();
        this.mAdult = in.readByte() != 0;
        this.mOverview = in.readString();
        this.mReleaseDate = in.readString();

    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mVoteCount);
        dest.writeString(mId);
        dest.writeString(mVideo);
        dest.writeString(mVoteAverage);
        dest.writeString(mTitle);
        dest.writeString(mPopularity);
        dest.writeString(mPosterPath);
        dest.writeString(mOriginalLanguage);
        dest.writeString(mOriginalTitle);
        dest.writeString(mBackdropPath);
        dest.writeByte((byte) (mAdult ? 1 : 0));
        dest.writeString(mOverview);
        dest.writeString(mReleaseDate);

    }
    public int getmVoteCount() {
        return mVoteCount;
    }

    public void setmVoteCount(int mVoteCount) {
        this.mVoteCount = mVoteCount;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmVideo() {
        return mVideo;
    }

    public void setmVideo(String mVideo) {
        this.mVideo = mVideo;
    }

    public String getmVoteAverage() {
        return mVoteAverage;
    }

    public void setmVoteAverage(String mVoteAverage) {
        this.mVoteAverage = mVoteAverage;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmPopularity() {
        return mPopularity;
    }

    public void setmPopularity(String mPopularity) {
        this.mPopularity = mPopularity;
    }

    public String getmPosterPath() {
        return mPosterPath;
    }

    public void setmPosterPath(String mPosterPath) {
        this.mPosterPath = mPosterPath;
    }

    public String getmOriginalLanguage() {
        return mOriginalLanguage;
    }

    public void setmOriginalLanguage(String mOriginalLanguage) {
        this.mOriginalLanguage = mOriginalLanguage;
    }

    public String getmOriginalTitle() {
        return mOriginalTitle;
    }

    public void setmOriginalTitle(String mOriginalTitle) {
        this.mOriginalTitle = mOriginalTitle;
    }

    public String getmBackdropPath() {
        return mBackdropPath;
    }

    public void setmBackdropPath(String mBackdropPath) {
        this.mBackdropPath = mBackdropPath;
    }

    public Boolean getmAdult() {
        return mAdult;
    }

    public void setmAdult(Boolean mAdult) {
        this.mAdult = mAdult;
    }

    public String getmOverview() {
        return mOverview;
    }

    public void setmOverview(String mOverview) {
        this.mOverview = mOverview;
    }

    public String getmReleaseDate() {
        return mReleaseDate;
    }

    public void setmReleaseDate(String mReleaseDate) {
        this.mReleaseDate = mReleaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Movies> CREATOR = new Parcelable.Creator<Movies>() {

        @Override
        public Movies createFromParcel(Parcel source) {
            return new Movies(source);
        }

        @Override
        public Movies[] newArray(int size) {
            return new Movies[size];
        }
    };
}