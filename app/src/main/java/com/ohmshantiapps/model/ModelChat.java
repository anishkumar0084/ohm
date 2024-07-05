package com.ohmshantiapps.model;

import com.google.gson.annotations.SerializedName;

public class ModelChat {
    @SerializedName("sender")
    private String sender;

    @SerializedName("receiver")
    private String receiver;

    @SerializedName("msg")
    private String message;

    @SerializedName("isSeen")
    private int isSeen;

    @SerializedName("type")
    private String type;

    // Constructors, getters, and setters
    public ModelChat(String sender, String receiver, String message, String type, int isSeen) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isSeen = isSeen;
        this.type = type;
    }

    // Getters and setters
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int isSeen() {
        return isSeen;
    }

    public void setSeen(int seen) {
        isSeen = seen;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
