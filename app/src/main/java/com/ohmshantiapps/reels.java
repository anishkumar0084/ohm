package com.ohmshantiapps;
public class reels {
    private String videoUrl;
    private int userProfilePhoto;
    private int usedAudio;
    private String userName;
    private String description;

    // Constructor, getters, and setters
    public reels(String videoUrl, int userProfilePhoto, int usedAudio, String userName, String description) {
        this.videoUrl = videoUrl;
        this.userProfilePhoto = userProfilePhoto;
        this.usedAudio = usedAudio;
        this.userName = userName;
        this.description = description;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public int getUserProfilePhoto() {
        return userProfilePhoto;
    }

    public void setUserProfilePhoto(int userProfilePhoto) {
        this.userProfilePhoto = userProfilePhoto;
    }

    public int getUsedAudio() {
        return usedAudio;
    }

    public void setUsedAudio(int usedAudio) {
        this.usedAudio = usedAudio;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
