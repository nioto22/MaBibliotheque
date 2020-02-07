package com.aprouxdev.mabibliotheque.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;

import com.aprouxdev.mabibliotheque.database.MetaData;

import java.io.Serializable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = MetaData.bookTableName)
public class Book implements Serializable {
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




    // GETTER
    public String getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getAuthor() { return author; }
    public String getThumbnailLink() {
        return thumbnailLink;
    }
    /**
     * @return null as base info do not have description data.
     */
    public String getDescription() {
        return description;
    }
    public String getCategory() { return category;   }
    public int getPageCount() {  return pageCount;   }
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

}
