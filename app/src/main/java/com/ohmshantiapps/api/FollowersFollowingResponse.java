package com.ohmshantiapps.api;

import java.util.List;

public class FollowersFollowingResponse {
    private int num_followers;
    private List<Integer> followers;
    private int num_following;
    private List<Integer> following;

    // Getters and setters
    public int getNumFollowers() {
        return num_followers;
    }

    public void setNumFollowers(int num_followers) {
        this.num_followers = num_followers;
    }

    public List<Integer> getFollowers() {
        return followers;
    }

    public void setFollowers(List<Integer> followers) {
        this.followers = followers;
    }

    public int getNumFollowing() {
        return num_following;
    }

    public void setNumFollowing(int num_following) {
        this.num_following = num_following;
    }

    public List<Integer> getFollowing() {
        return following;
    }

    public void setFollowing(List<Integer> following) {
        this.following = following;
    }
}
