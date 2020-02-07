package com.aprouxdev.mabibliotheque.network.BookSearch.mapper;


import com.aprouxdev.mabibliotheque.models.Book;
import com.aprouxdev.mabibliotheque.models.DetailedBook;
import com.aprouxdev.mabibliotheque.network.BookSearch.dto.BookInfo;
import com.aprouxdev.mabibliotheque.network.BookSearch.dto.DetailedEBookInfo;
import com.aprouxdev.mabibliotheque.network.BookSearch.dto.EBookInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BookMapper {


    public BookMapper() {
    }

    public @Nullable
    Book transform(@Nullable EBookInfo eBookInfo) {
        if (eBookInfo == null) return null;

        final Book book = new Book();

        transformBookData(eBookInfo, book);

        return book;
    }

    private void transformBookData(@NonNull EBookInfo eBookInfo, Book book) {
        book.setId(eBookInfo.getId());

        final BookInfo bookInfo = eBookInfo.getBookInfo();
        book.setTitle(bookInfo.getTitle());
        book.setAuthor(bookInfo.getAuthor());
        book.setDescription(bookInfo.getDescription());
        book.setPageCount(bookInfo.getPageCount());
        book.setCategory(bookInfo.getCategory());
        book.setThumbnailLink(bookInfo.getThumbnailLink());
    }

    public @NonNull
    List<Book> transform(@Nullable Collection<? extends EBookInfo> eBookInfos) {
        if (eBookInfos == null) return Collections.emptyList();

        List<Book> books = new ArrayList<>();
        for (EBookInfo eBookInfo : eBookInfos) {
            final Book book = transform(eBookInfo);
            if (book != null) books.add(book);
        }

        return books;
    }

    public DetailedBook transform(DetailedEBookInfo detailedBookInfo) {
        final DetailedBook detailedBook = new DetailedBook();

        transformBookData(detailedBookInfo, detailedBook);

        detailedBook.setDescription(detailedBookInfo.getBookInfo().getDescription());

        return detailedBook;
    }
}
