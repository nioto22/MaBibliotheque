package com.aprouxdev.mabibliotheque.models;

import java.util.List;

import androidx.annotation.Nullable;

public class SocialUser {

    private String uid;
    private String username;
    private Boolean isAPublicUser;
    @Nullable
    private String urlPicture;
    private int adviserLevel;
    private List<String> friends;
    private List<String> posts;
    private List<String> comments;
    private List<String> requestsTo;
    private List<String> requestsFrom;

    public SocialUser() {}


    public SocialUser(String uid, String username, @Nullable String urlPicture) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.isAPublicUser = true;
        this.adviserLevel = 0;
    }

    // --- GETTERS ---
    public String getUid() {  return uid;}
    public String getUsername() {   return username; }
    public Boolean getAPublicUser() {return isAPublicUser; }
    @Nullable
    public String getUrlPicture() {return urlPicture;  }
    public int getAdviserLevel() {return adviserLevel; }
    public List<String> getFriends() { return friends; }
    public List<String> getPosts() {return posts; }
    public List<String> getComments() { return comments;  }
    public List<String> getRequestsTo() { return requestsTo; }
    public List<String> getRequestsFrom() { return requestsFrom;}

    // --- SETTERS ---
    public void setUsername(String username) {  this.username = username; }
    public void setAPublicUser(Boolean APublicUser) { isAPublicUser = APublicUser;  }
    public void setUrlPicture(@Nullable String urlPicture) {this.urlPicture = urlPicture;}
    public void setAdviserLevel(int adviserLevel) { this.adviserLevel = adviserLevel;}
    public void setFriends(List<String> friends) { this.friends = friends;  }
    public void setPosts(List<String> posts) { this.posts = posts; }
    public void setComments(List<String> comments) { this.comments = comments; }
    public void setRequestsTo(List<String> requestsTo) {  this.requestsTo = requestsTo; }
    public void setRequestsFrom(List<String> requestsFrom) { this.requestsFrom = requestsFrom; }
}
