package com.aprouxdev.mabibliotheque.viewmodels;

import android.app.Activity;
import android.app.Application;

import com.aprouxdev.mabibliotheque.database.BookRepository;
import com.aprouxdev.mabibliotheque.models.Book;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

public class BookViewModel extends AndroidViewModel {

    private BookRepository bookRepository;
    private LiveData<List<Book>> books;

    public BookViewModel(@NonNull Application application){
        super(application);
        bookRepository = new BookRepository(application);
        books = bookRepository.getAllBooks();
    }

    public LiveData<List<Book>> getBooks() {
        if(books == null){
            books = new MutableLiveData<>();
        }
        return books;
    }

    public void insert(Book book){
        bookRepository.insert(book);
    }

    public void update(Book book){
        bookRepository.update(book);
    }

    public void delete(Book book){
        bookRepository.delete(book);
    }

    public void deleteAllActivities(){
        bookRepository.deleteAllBooks();
    }

    public LiveData<List<Book>> searchWithTitle(String query){
        return bookRepository.searchWithTitle(query);
    }
    public LiveData<List<Book>> searchId(String query){
        return bookRepository.searchId(query);
    }
    public LiveData<List<Book>> searchHasBeenRead(Boolean boolQuery){
        return bookRepository.searchHasBeenRead(boolQuery);
    }
    public LiveData<List<Book>> searchMark(int query){
        return bookRepository.searchMark(query);
    }
}
