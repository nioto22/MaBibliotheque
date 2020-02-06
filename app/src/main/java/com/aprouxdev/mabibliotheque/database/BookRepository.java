package com.aprouxdev.mabibliotheque.database;


import android.app.Application;
import android.os.AsyncTask;

import com.aprouxdev.mabibliotheque.models.Book;

import java.util.List;

import androidx.lifecycle.LiveData;

public class BookRepository {

    private BookDao bookDao;
    private LiveData<List<Book>> allBooks;

    public BookRepository(Application application){
        BookDatabase database = BookDatabase.getInstance(application);
        bookDao = database.bookDao();
        allBooks = bookDao.getAllBooks();
    }

    // AsyncMethods
    public void insert(Book book){
        new InsertBookAsyncTask(bookDao).execute(book);
    }

    public void update(Book book){
        new UpdateBookAsyncTask(bookDao).execute(book);
    }

    public void delete(Book book){
        new DeleteBookAsyncTask(bookDao).execute(book);
    }

    public void deleteAllBooks(){
        new DeleteAllBooksAsyncTask(bookDao).execute();
    }

    public LiveData<List<Book>> searchWithTitle(String query) {
        return bookDao.searchWithTitle(query);
    }

    public LiveData<List<Book>> searchId(String id) {
        return bookDao.searchId(id);
    }

    public LiveData<List<Book>> searchHasBeenRead(Boolean boolQuery){
        return bookDao.searchHasBeenRead(boolQuery);
    }
    public LiveData<List<Book>> searchMark(int query){
        return bookDao.searchMark(query);
    }

    public LiveData<List<Book>> getAllBooks() {
        return allBooks;
    }

    // -----------
    //  ASYNCTASK
    // -----------
    private static class InsertBookAsyncTask extends AsyncTask<Book, Void, Void>{
        private BookDao bookDao;
        public InsertBookAsyncTask(BookDao bookDao) {
            this.bookDao = bookDao;
        }
        @Override
        protected Void doInBackground(Book... books) {
            bookDao.insert(books[0]);
            return null;
        }
    }

    private static class UpdateBookAsyncTask extends AsyncTask<Book, Void, Void>{
        private BookDao bookDao;
        public UpdateBookAsyncTask(BookDao bookDao) {
            this.bookDao = bookDao;
        }
        @Override
        protected Void doInBackground(Book... books) {
            bookDao.update(books[0]);
            return null;
        }
    }

    private static class DeleteBookAsyncTask extends AsyncTask<Book, Void, Void>{
        private BookDao bookDao;
        public DeleteBookAsyncTask(BookDao bookDao) {
            this.bookDao = bookDao;
        }
        @Override
        protected Void doInBackground(Book... books) {
            bookDao.delete(books[0]);
            return null;
        }
    }

    private static class DeleteAllBooksAsyncTask extends AsyncTask<Void, Void, Void> {
        private BookDao bookDao;
        public DeleteAllBooksAsyncTask(BookDao bookDao) {
            this.bookDao = bookDao;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            bookDao.deleteAllBooks();
            return null;
        }
    }
}
