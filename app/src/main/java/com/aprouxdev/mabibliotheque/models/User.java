package com.aprouxdev.mabibliotheque.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class User implements Serializable {

    private String uid;
    //@Nullable
    private String username;
    private Boolean isSignedInUser;
    private String email;
    @Nullable
    private String urlPicture;
    private Boolean isAPublicUser;


    // --- CONSTRUCTORS ---

    public User() { }

    public User(String uid, String username, Boolean isSignedInUser, String email, @Nullable String urlPicture) {
        this.uid = uid;
        this.username = username;
        this.isSignedInUser = isSignedInUser;
        this.email = email;
        this.urlPicture = urlPicture;
        this.isAPublicUser = true;
    }

    // --- GETTERS ---
    public String getUid() { return uid; }
    public String getEmail() { return email; }
    @Nullable
    public String getUsername() { return username; }
    @Nullable
    public String getUrlPicture() { return urlPicture; }
    public Boolean getIsSignedInUser() { return isSignedInUser; }
    public Boolean getAPublicUser() { return isAPublicUser;}

    // --- SETTERS ---
    public void setUsername(String username) { this.username = username; }
    public void setUid(String uid) { this.uid = uid; }
    public void setEmail(String email) {this.email = email; }
    public void setUrlPicture(String urlPicture) { this.urlPicture = urlPicture; }
    public void setIsSignedInUser(Boolean isSignedInUser) { this.isSignedInUser = isSignedInUser; }
    public void setAPublicUser(Boolean isAPublicUser) {  this.isAPublicUser = isAPublicUser; }
}

