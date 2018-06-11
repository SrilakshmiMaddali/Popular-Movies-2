package com.sm.popularmovies_stage1.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VideoDataDto {
    @SerializedName("id")
    private String id;
    @SerializedName("results")
    private List<TrailerVideo> results;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<TrailerVideo> getResults() {
        return results;
    }

    public void setResults(List<TrailerVideo> results) {
        this.results = results;
    }
}
