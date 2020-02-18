package com.aprouxdev.mabibliotheque.database.firestoreDatabase;

import com.aprouxdev.mabibliotheque.models.Book;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class LibraryHelper {

    public static final String LIBRARIES_COLLECTION_NAME = "libraries";
    public static final String LIBRARY_COLLECTION_NAME = "library";

    // --- COLLECTION REFERENCE ---
    public static CollectionReference getLibraryCollection(String uid){
        return FirebaseFirestore.getInstance()
                .collection(LIBRARIES_COLLECTION_NAME) // All Libraries collection
                .document(uid)
                .collection(LIBRARY_COLLECTION_NAME); // MyLibrary
    }

    // --- ADD BOOK TO LIBRARY = CREATE A Library and a BOOK ---
    public static Task<Void> addBook(String uid, Book book){
        return LibraryHelper.getLibraryCollection(uid).document(book.getId()).set(book);
    }

    // --- QUERY ---
    public static Query getAllBooks(String uid){
        return LibraryHelper.getLibraryCollection(uid);
    }
    public static Query getBooksByCategory(String uid, String category){
        return LibraryHelper.getLibraryCollection(uid)
                .whereEqualTo("category", category);
    }
    public static Query getBooksByRead(String uid, boolean hasBeenRead){
        if (hasBeenRead) {
            return LibraryHelper.getLibraryCollection(uid)
                    .whereEqualTo("hasBeenRead", hasBeenRead);
        } else {
            return LibraryHelper.getLibraryCollection(uid)
                    .whereEqualTo("hasBeenRead", false);
        }
    }
    public static Query getBooksByMark(String uid, int mark){
        return LibraryHelper.getLibraryCollection(uid)
                .whereGreaterThanOrEqualTo("mark",mark);
    }

    // --- GET ---
    public static Task<DocumentSnapshot> getBook(String uid, String bookId){
        return LibraryHelper.getLibraryCollection(uid).document(bookId).get();
    }

    // --- UPDATE ---
    public static Task<Void> updateBook(String uid, Book book){
        return LibraryHelper.addBook(uid, book);
    }


    // --- DELETE ---
    public static Task<Void> deleteBook(String uid, Book book){
        return LibraryHelper.getLibraryCollection(uid).document(book.getId()).delete();
    }
}
