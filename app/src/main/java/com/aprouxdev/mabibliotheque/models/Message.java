package com.aprouxdev.mabibliotheque.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Message {

    private String uid;
    private String message;
    private Date dateCreated;
    private User userSender;
    private String bookId;
    private String bookImageLink;

    public Message() { }

    public Message(String uid, String message, User userSender) {
        this.uid = uid;
        this.message = message;
        this.userSender = userSender;
    }

    public Message(String uid, String message, String bookId, String bookImageLink, User userSender) {
        this.uid = uid;
        this.message = message;
        this.bookId = bookId;
        this.bookImageLink = bookImageLink;
        this.userSender = userSender;
    }

    // --- GETTERS ---
    public String getUid() { return  uid; }
    public String getMessage() { return message; }
    @ServerTimestamp
    public Date getDateCreated() { return dateCreated; }
    public User getUserSender() { return userSender; }
    public String getBook() { return bookId; }
    public String getBookImageLink() { return bookImageLink; }

    // --- SETTERS ---
    public void setMessage(String message) { this.message = message; }
    public void setDateCreated(Date dateCreated) { this.dateCreated = dateCreated; }
    public void setUserSender(User userSender) { this.userSender = userSender; }
    public void setBookId(String bookId) { this.bookId = bookId; }
    public void setBookImageLink(String bookImageLink) { this.bookImageLink = bookImageLink; }
}

