package com.aprouxdev.mabibliotheque.ui.main.addBook;

import android.util.Log;

import com.aprouxdev.mabibliotheque.models.Book;
import com.aprouxdev.mabibliotheque.network.BookSearch.repository.BookRepository;
import com.aprouxdev.mabibliotheque.util.Constants;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class AddBookViewModel extends ViewModel {
    private static final String TAG = "AddBookViewModel";

    private BookRepository bookRepository;
    private final MutableLiveData<List<Book>> books = new MutableLiveData<>();
    private Disposable pendingBookSearch;

    public void init(){
        bookRepository = BookRepository.getInstance();
    }

    @Override
    protected void onCleared() {
        if(pendingBookSearch != null && !pendingBookSearch.isDisposed()){
            pendingBookSearch.dispose();
        }

    }

    public void searchBooks(CharSequence textToSearch){
        if(pendingBookSearch != null && !pendingBookSearch.isDisposed()) pendingBookSearch.dispose();

        pendingBookSearch = bookRepository.getBooks(textToSearch)
                .subscribeOn(Schedulers.io())
                .subscribe(
                        books::postValue,
                        e -> Log.e(TAG, "accept: Unable to get books info from rest api.", e)
                );

    }

    public LiveData<List<Book>> getBooks() {
        return books;
    }
}
