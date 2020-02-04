package com.aprouxdev.mabibliotheque.network.BookSearch.repository;

import com.aprouxdev.mabibliotheque.models.Book;
import com.aprouxdev.mabibliotheque.models.DetailedBook;
import com.aprouxdev.mabibliotheque.network.BookSearch.dto.BooksInfo;
import com.aprouxdev.mabibliotheque.network.BookSearch.mapper.BookMapper;
import com.aprouxdev.mabibliotheque.network.BookSearch.services.BookApi;
import com.aprouxdev.mabibliotheque.network.BookSearch.services.RetrofitService;
import com.aprouxdev.mabibliotheque.util.Constants;

import java.util.List;

import io.reactivex.Single;

public class BookRepository {

    private static BookRepository bookRepository;

    public static BookRepository getInstance(){
        if(bookRepository == null){
            bookRepository = new BookRepository();
        }
        return bookRepository;
    }

    private BookApi bookApi;
    private BookMapper mapper;

    public BookRepository() {
        this.bookApi = RetrofitService.createBookSearchService();
        this.mapper = new BookMapper();
    }

//    public MutableLiveData<BookResponse> getBooks(CharSequence source, String key){
//        final MutableLiveData<BookResponse> bookData = new MutableLiveData<>();
//        bookApi.getBookList(source, key).enqueue(new Callback<BookResponse>() {
//            @Override
//            public void onResponse(Call<BookResponse> call, Response<BookResponse> response) {
//                if(response.isSuccessful()){
//                    bookData.setValue(response.body());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<BookResponse> call, Throwable t) {
//                bookData.setValue(null);
//            }
//        });
//        return bookData;
//    }

    public Single<List<Book>> getBooks(CharSequence bookName) {
        return bookApi.getBooks(bookName, Constants.KEY_BOOKS_API, "12").map(BooksInfo::getEBooks).map(mapper::transform);
    }

    public Single<DetailedBook> getBookDetails(CharSequence bookName) {
        return bookApi.getBookDetails(bookName).map(mapper::transform);
    }


}
