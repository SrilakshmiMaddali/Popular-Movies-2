package com.sm.popularmovies_stage1.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReviewDataDto {
    @SerializedName("id")
    private String id;
    @SerializedName("page")
    private Integer page;
    @SerializedName("results")
    private List<Review> results;
    @SerializedName("total_pages")
    private Integer totalPages;

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    @SerializedName("total_results")
    private  Integer totalResults;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public List<Review> getResults() {
        return results;
    }

    public void setResults(List<Review> results) {
        this.results = results;
    }
}
