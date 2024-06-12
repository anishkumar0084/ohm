package com.ohmshantiapps.api;

public class DeletePostRequest {
    private String pId;

    public DeletePostRequest(String pId) {
        this.pId = pId;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }
}
