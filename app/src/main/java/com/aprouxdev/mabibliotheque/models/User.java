package com.aprouxdev.mabibliotheque.models;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class User {

    private String uid;
    @Nullable
    private String username;
    private Boolean isSignedInUser;
    private String email;
    @Nullable
    private String urlPicture;
    private List<String> friends;
    private List<String> discussions;

    public User() { }

    public User(String uid, Boolean isSignedInUser, String email) {
        this.uid = uid;
        this.email = email;
        this.username = null;
        this.urlPicture = null;
        this.isSignedInUser = isSignedInUser;
        this.friends = new ArrayList<>();
        this.discussions = new ArrayList<>();
    }

    // --- GETTERS ---
    public String getUid() { return uid; }
    public String getEmail() { return email; }
    @Nullable
    public String getUsername() { return username; }
    @Nullable
    public String getUrlPicture() { return urlPicture; }
    public Boolean getIsSignedInUser() { return isSignedInUser; }
    public List<String> getFriends() {
        return friends;
    }
    public List<String> getDiscussions() {
        return discussions;
    }

    // --- SETTERS ---
    public void setUsername(String username) { this.username = username; }
    public void setUid(String uid) { this.uid = uid; }
    public void setEmail(String email) {this.email = email; }
    public void setUrlPicture(String urlPicture) { this.urlPicture = urlPicture; }
    public void setIsSignedInUser(Boolean isSignedInUser) { this.isSignedInUser = isSignedInUser; }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }
    public void setDiscussions(List<String> discussions) { this.discussions = discussions; }
}

