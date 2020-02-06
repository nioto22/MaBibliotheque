package com.aprouxdev.mabibliotheque.network.BookSearch.dto;



import java.io.Serializable;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * Short list of data about the book to display in "books list".
 */
public class BookInfo implements Serializable {
    private static final long serialVersionUID = 8320625464585649346L;

    String title;
    List<String> authors;
    String author;
    ImageLinks imageLinks;
    String description;

    public String getTitle() {
        return title;
    }
    public String getAuthor() { return authors.get(0); }

    @Nullable
    public String getThumbnailLink() {
        if (imageLinks == null) return null;

        return imageLinks.thumbnail;
    }

    /**
     * @return null as base info do not have description data.
     */
    public String getDescription() {
        return description;
    }

    public static class ImageLinks implements Serializable {
        private static final long serialVersionUID = -5555371262366112331L;

        String thumbnail;

        @Override
        public String toString() {
            return "ImageLinks{" +
                    "thumbnail='" + thumbnail + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "BookInfo{" +
                "title='" + title + '\'' +
                ", imageLinks=" + imageLinks +
                '}';
    }
}
