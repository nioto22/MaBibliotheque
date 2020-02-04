package com.aprouxdev.mabibliotheque.network.BookSearch.dto;

/**
 * Contains info about dto-json mapping.
 */
public interface DtoSchema {
    interface Books {
        String COUNT = "totalItems";
        String EBOOKS_LIST = "items";
    }

    interface EBook {
        String ID = "id";
        String BOOK_INFO = "volumeInfo";
    }
}
