package com.ohmshantiapps.model;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("username")
    private String username;

    @SerializedName("bio")
    private String bio;

    @SerializedName("location")
    private String location;

    @SerializedName("link")
    private String link;

    @SerializedName("photo")
    private String photo;

    @SerializedName("phone")
    private String phone;

    @SerializedName("status")
    private String status;

    @SerializedName("typingTo")
    private String typingTo;

    @SerializedName("isBlocked")
    private boolean isBlocked;

    @SerializedName("password")
    private String password;



    @SerializedName("userid")
    private String userid;

    // Constructor
    public User(int id, String name, String email, String username, String bio, String location, String link, String photo, String phone, String status, String typingTo, boolean isBlocked, String password,String userid) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.username = username;
        this.bio = bio;
        this.location = location;
        this.link = link;
        this.photo = photo;
        this.phone = phone;
        this.status = status;
        this.typingTo = typingTo;
        this.isBlocked = isBlocked;
        this.password = password;
        this.userid=userid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }




    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTypingTo() {
        return typingTo;
    }

    public void setTypingTo(String typingTo) {
        this.typingTo = typingTo;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // toString method for debugging
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", bio='" + bio + '\'' +
                ", location='" + location + '\'' +
                ", link='" + link + '\'' +
                ", photo='" + photo + '\'' +
                ", phone='" + phone + '\'' +
                ", status='" + status + '\'' +
                ", typingTo='" + typingTo + '\'' +
                ", isBlocked=" + isBlocked +
                ", password='" + password + '\'' +
                ",userid='" + userid + '\''+
                '}';
    }
}
