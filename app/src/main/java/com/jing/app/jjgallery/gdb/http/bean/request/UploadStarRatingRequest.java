package com.jing.app.jjgallery.gdb.http.bean.request;

import com.king.app.gdb.data.entity.StarRating;

import java.util.List;

public class UploadStarRatingRequest {

    private List<StarRating> ratingList;

    public List<StarRating> getRatingList() {
        return ratingList;
    }

    public void setRatingList(List<StarRating> ratingList) {
        this.ratingList = ratingList;
    }
}
