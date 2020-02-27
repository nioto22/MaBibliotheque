package com.aprouxdev.mabibliotheque.models;

import java.util.List;

public class Discussion {

    private String uid;
    private String name;
    private List<String> usersUid;

    public Discussion() {}

    public Discussion(String uid,List<String> usersUid) {
        this.uid = uid;
        this.usersUid = usersUid;
    }

    public Discussion(String uid, String name, List<String> usersUid) {
        this.uid = uid;
        this.name = name;
        this.usersUid = usersUid;
    }

    // GETTER
    public String getUid() { return uid; }
    public String getName() { return name; }
    public List<String> getUsersUid() { return usersUid; }

    // SETTER

    public void setUsersUid(List<String> usersUid) {
        this.usersUid = usersUid;
    }

    public void setName(String name) {
        this.name = name;
    }
}
