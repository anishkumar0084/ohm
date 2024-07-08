package com.ohmshantiapps.api;

import com.google.gson.annotations.SerializedName;

public class UniqueLinkResponse {

    @SerializedName("unique_link")
    private String uniqueLink;

    public String getUniqueLink() {
        return uniqueLink;
    }
}
