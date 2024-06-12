package com.ohmshantiapps.api;

import com.google.gson.annotations.SerializedName;

public class ModelPostRequest {

    @SerializedName("action")
    private String action;

    @SerializedName("pId")
    private String pId;

    public ModelPostRequest(String action, String pId) {
        this.action = action;
        this.pId = pId;
    }
}
