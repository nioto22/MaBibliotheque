package com.aprouxdev.mabibliotheque.database.localDatabase;

import com.aprouxdev.mabibliotheque.models.Book;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import static com.aprouxdev.mabibliotheque.database.localDatabase.MetaData.Query.*;

@Dao
public interface BookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Book book);

    @Update
    void update(Book book);

    @Delete
    void delete(Book book);

    @Query(deleteAllBooksQuery)
    void deleteAllBooks();

    @Query(getAllBooksQuery)
    LiveData<List<Book>> getAllBooks();

    // --------------------
    // SEARCH METHOD
    // --------------------
    @Query(searchWithTitleQuery)
    LiveData<List<Book>> searchWithTitle(String searchQuery);

    @Query(searchIdQuery)
    LiveData<List<Book>> searchId(String searchQuery);

    @Query(searchHasBeenReadQuery)
    LiveData<List<Book>> searchHasBeenRead(Boolean searchQuery);

    @Query(searchMarkQuery)
    LiveData<List<Book>> searchMark(Integer searchQuery);
}
