package com.sm.popularmovies_stage1.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PopularMoviesDto {
    @SerializedName("page")
    private int mpage;
    @SerializedName("results")
    private List<Movies> mResults;
    @SerializedName("total_results")
    private int mTotalResults;
    @SerializedName("total_pages")
    private int mTotalPages;

    public PopularMoviesDto(int mpage,List<Movies> results,int mTotalResults,int totalPages ){
        this.mpage=mpage;
        this.mTotalResults=mTotalResults;
        this.mTotalPages=totalPages;
        this.mResults =results;
    }

    public int getMpage() {
        return mpage;
    }

    public void setMpage(int mpage) {
        this.mpage = mpage;
    }

    public List<Movies> getmResults() {
        return mResults;
    }

    public void setmResults(List<Movies> mResults) {
        this.mResults = mResults;
    }

    public int getmTotalResults() {
        return mTotalResults;
    }

    public void setmTotalResults(int mTotalResults) {
        this.mTotalResults = mTotalResults;
    }

    public int getmTotalPages() {
        return mTotalPages;
    }

    public void setmTotalPages(int mTotalPages) {
        this.mTotalPages = mTotalPages;
    }
}