package com.topurayhan.chatter;

import java.util.ArrayList;

public class User {
    private String userId, name, username, email, profileImage;
    private ArrayList<String> friendList;

    public  User(){

    }
    public User(String userId, String name, String username, String email, String profileImage, ArrayList<String> friendList) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.email = email;
        this.profileImage = profileImage;
        this.friendList = friendList;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public ArrayList<String> getFriendList() {
        return friendList;
    }

    public void setFriendList(ArrayList<String> friendList) {
        this.friendList = friendList;
    }
}
