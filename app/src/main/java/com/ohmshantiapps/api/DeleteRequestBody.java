package com.ohmshantiapps.api;

// DeleteRequestBody.java
public class DeleteRequestBody {
    private String pId;

    public DeleteRequestBody(String pId) {
        this.pId = pId;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }
}

