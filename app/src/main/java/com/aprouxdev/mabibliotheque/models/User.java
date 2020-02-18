package com.aprouxdev.mabibliotheque.models;

import androidx.annotation.Nullable;

public class User {

    private String uid;
    @Nullable
    private String username;
    private Boolean isSignedInUser;
    @Nullable
    private String urlPicture;

    public User() { }

    public User(String uid, Boolean isSignedInUser) {
        this.uid = uid;
        this.username = null;
        this.urlPicture = null;
        this.isSignedInUser = isSignedInUser;
    }

    // --- GETTERS ---
    public String getUid() { return uid; }
    @Nullable
    public String getUsername() { return username; }
    @Nullable
    public String getUrlPicture() { return urlPicture; }
    public Boolean getIsSignedInUser() { return isSignedInUser; }

    // --- SETTERS ---
    public void setUsername(String username) { this.username = username; }
    public void setUid(String uid) { this.uid = uid; }
    public void setUrlPicture(String urlPicture) { this.urlPicture = urlPicture; }
    public void setIsSignedInUser(Boolean isSignedInUser) { this.isSignedInUser = isSignedInUser; }
}

