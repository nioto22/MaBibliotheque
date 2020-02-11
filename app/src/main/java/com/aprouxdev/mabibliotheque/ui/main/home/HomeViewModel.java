package com.aprouxdev.mabibliotheque.ui.main.home;

import com.aprouxdev.mabibliotheque.models.Book;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {
    public List<Book> setupToReadBooks(List<Book> books) {
     List<Book> toReadBooks = new ArrayList<>();
        for (Book book : books ) {
            if (book.getHasBeenRead() == null || !book.getHasBeenRead()){
                toReadBooks.add(book);
            }
        }
        return toReadBooks;
    }

    public List<Book> setupFavoriteBooks(List<Book> books) {
        List<Book> favoriteBooks = new ArrayList<>();
        for (Book book : books ) {
            if (book.getMark() > 4) favoriteBooks.add(book);
        }
        return favoriteBooks;
    }
}
