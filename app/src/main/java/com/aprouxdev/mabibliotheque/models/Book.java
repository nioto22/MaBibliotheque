package com.aprouxdev.mabibliotheque.models;

import java.io.Serializable;

public class Book implements Serializable {
    private String id;
    private String title;
    private String thumbnailLink;
    private Boolean hasBeenRead;
    private int mark;
    private String saveTimestamp;
    private String readTimestamp;


    // GETTER
    public String getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getThumbnailLink() {
        return thumbnailLink;
    }
    /**
     * @return null as base info do not have description data.
     */
    public String getDescription() {
        return null;
    }
    public Boolean getHasBeenRead() { return hasBeenRead; }
    public int getMark() { return mark; }
    public String getSaveTimestamp() { return saveTimestamp; }
    public String getReadTimestamp() { return readTimestamp; }

    // SETTER

    public void setId(String id) {
        this.id = id;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setThumbnailLink(String thumbnailLink) {
        this.thumbnailLink = thumbnailLink;
    }
    public void setHasBeenRead(Boolean hasBeenRead) { this.hasBeenRead = hasBeenRead; }
    public void setMark(int mark) { this.mark = mark; }
    public void setSaveTimestamp(String saveTimestamp) { this.saveTimestamp = saveTimestamp; }
    public void setReadTimestamp(String readTimestamp) { this.readTimestamp = readTimestamp; }
}
