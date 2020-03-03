package com.aprouxdev.mabibliotheque.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

public class Discussion {

    private String uid;
    private String name;
    private Date dateCreated;
    private int numberOfUsers;
    private List<String> usersUid;
    private List<String> usersName;

    public Discussion() {}

    public Discussion(String uid,List<String> usersUid, List<String> usersName) {
        this.uid = uid;
        this.usersUid = usersUid;
        this.usersName = usersName;
        this.numberOfUsers = usersUid.size();
    }

    public Discussion(String uid, String name, List<String> usersUid) {
        this.uid = uid;
        this.name = name;
        this.usersUid = usersUid;
    }

    // GETTER
    public String getUid() { return uid; }
    public String getName() { return name; }
    @ServerTimestamp
    public Date getDateCreated() { return dateCreated; }
    public List<String> getUsersUid() { return usersUid; }
    public List<String> getUsersName() { return usersName; }
    public int getNumberOfUsers() {  return numberOfUsers; }

    // SETTER
    public void setUsersUid(List<String> usersUid) {
        this.usersUid = usersUid;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setDateCreated(Date dateCreated) { this.dateCreated = dateCreated; }
    public void setUsersName(List<String> usersName) { this.usersName = usersName; }
    public void setNumberOfUsers(int numberOfUsers) { this.numberOfUsers = numberOfUsers; }
}
