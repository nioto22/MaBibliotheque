package com.aprouxdev.mabibliotheque.database.localDatabase;

public interface MetaData {

        String bookTableName = "book_table";
        String bookDatabase = "book_database";

    interface Query {
        String getAllBooksQuery = "SELECT * FROM book_table";
        String deleteAllBooksQuery = "DELETE FROM book_table";
        String searchWithTitleQuery = "SELECT * FROM book_table WHERE title LIKE :searchQuery";
        String searchIdQuery = "SELECT * FROM book_table WHERE id LIKE :searchQuery";
        String searchHasBeenReadQuery = "SELECT * FROM book_table WHERE hasBeenRead LIKE :searchQuery";
        String searchMarkQuery = "SELECT * FROM book_table WHERE mark LIKE :searchQuery";
    }
}
