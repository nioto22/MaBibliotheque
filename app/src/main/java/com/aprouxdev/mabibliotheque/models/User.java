package com.aprouxdev.mabibliotheque.models;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class User {

    private String uid;
    @Nullable
    private String username;
    private Boolean isSignedInUser;
    @Nullable
    private String urlPicture;
    private List<String> friends;

    public User() { }

    public User(String uid, Boolean isSignedInUser) {
        this.uid = uid;
        this.username = null;
        this.urlPicture = null;
        this.isSignedInUser = isSignedInUser;
        this.friends = new ArrayList<>();
    }

    // --- GETTERS ---
    public String getUid() { return uid; }
    @Nullable
    public String getUsername() { return username; }
    @Nullable
    public String getUrlPicture() { return urlPicture; }
    public Boolean getIsSignedInUser() { return isSignedInUser; }
    public List<String> getFriends() {
        return friends;
    }

    // --- SETTERS ---
    public void setUsername(String username) { this.username = username; }
    public void setUid(String uid) { this.uid = uid; }
    public void setUrlPicture(String urlPicture) { this.urlPicture = urlPicture; }
    public void setIsSignedInUser(Boolean isSignedInUser) { this.isSignedInUser = isSignedInUser; }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }
}

