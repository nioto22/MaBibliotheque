package com.aprouxdev.mabibliotheque.models;

import com.aprouxdev.mabibliotheque.database.localDatabase.MetaData;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = MetaData.bookTableName)
public class Book implements Serializable, Cloneable {
    @PrimaryKey
    @NonNull
    private String id;
    private String title;
    private String author;
    private String thumbnailLink;
    private String description;
    private String category;
    private int pageCount;
    private Boolean hasBeenRead;
    private int mark;
    private String comment;
    private String loan;
    private String saveTimestamp;
    private String readTimestamp;
    @Ignore
    private Date dateCreated;

    public Book() {
    }

    public Book(@NonNull String id, String title, String author, String thumbnailLink,
                String description, String category, int pageCount, Boolean hasBeenRead, int mark,
                String comment, String loan, String readTimestamp) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.thumbnailLink = thumbnailLink;
        this.description = description;
        this.category = category;
        this.pageCount = pageCount;
        this.hasBeenRead = hasBeenRead;
        this.mark = mark;
        this.comment = comment;
        this.loan = loan;
        this.saveTimestamp = this.dateCreated.toString();
        this.readTimestamp = readTimestamp;
    }

    // GETTER
    public String getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getAuthor() { return author; }
    @ServerTimestamp
    public Date getDateCreated() { return dateCreated; }

    // API INFORMATION
    public String getThumbnailLink() {
        return thumbnailLink;
    }
    public String getDescription() {
        return description;
    }
    public String getCategory() { return category;   }
    public int getPageCount() {  return pageCount;   }

    // USER ADDED INFORMATION
    public Boolean getHasBeenRead() { return hasBeenRead; }
    public int getMark() { return mark; }
    public String getComment() {  return comment;   }
    public String getLoan() {  return loan; }
    public String getSaveTimestamp() { return saveTimestamp; }
    public String getReadTimestamp() { return readTimestamp; }
// SETTER

    public void setId(String id) {
        this.id = id;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setAuthor(String author) { this.author = author; }

    public void setDescription(String description) {  this.description = description; }
    public void setCategory(String category) {  this.category = category; }
    public void setPageCount(int pageCount) { this.pageCount = pageCount;  }

    public void setThumbnailLink(String thumbnailLink) {
        this.thumbnailLink = thumbnailLink;
    }
    public void setHasBeenRead(Boolean hasBeenRead) { this.hasBeenRead = hasBeenRead; }
    public void setMark(int mark) { this.mark = mark; }

    public void setComment(String comment) {  this.comment = comment;  }
    public void setLoan(String loan) { this.loan = loan; }
    public void setSaveTimestamp(String saveTimestamp) { this.saveTimestamp = saveTimestamp; }
    public void setReadTimestamp(String readTimestamp) { this.readTimestamp = readTimestamp; }
    public void setDateCreated(Date dateCreated) { this.dateCreated = dateCreated; }

    // Methods

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
