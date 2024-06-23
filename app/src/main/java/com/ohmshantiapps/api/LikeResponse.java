package com.ohmshantiapps.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LikeResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("like_count")
    private int likeCount;

    @SerializedName("user_ids")
    private List<String> userIds;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public List<String> getUserIds() {
        return userIds;
    }
}
