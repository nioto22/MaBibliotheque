package com.aprouxdev.mabibliotheque.ui.main.library;

import com.aprouxdev.mabibliotheque.R;
import com.aprouxdev.mabibliotheque.models.Book;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.ViewModel;

public class LibraryViewModel extends ViewModel {

    public List<String> getSpinnerCategoryArray(String allString, List<Book> books) {
        List<String> categoryArray = new ArrayList<>();
        categoryArray.add(allString);
        for (Book book : books ) {
            if (book.getCategory() != null){
                if (!categoryArray.contains(book.getCategory()))
                    categoryArray.add(book.getCategory());
            }
        }
        return categoryArray;
    }


    /**
     * Filtration only if filterPosition != null && != AllFilters
     * Category filtration on libraryBooks : filteredBooks keep only right category books
     * Read and Mark filtration on filteredBooks list
     * @param filters : List<String> : categoryFilterPosition, readFilterPosition, markFilterPosition
     *                               ,categoryAndReadAllFilter, readTrueState, markAllFilter
     * @param libraryBooks : List<Book> all books
     * @return filteredBooks
     */
//    public List<Book> filterAllBooks(List<String> filters, List<Book> libraryBooks) {
//        List<Book> filteredBooks = new ArrayList<>();
//        String categoryFilterPosition = filters.get(0);
//        String readFilterPosition = filters.get(1);
//        String markFilterPosition = filters.get(2);
//        String categoryAndReadAllFilter = filters.get(3);
//        String readTrueState = filters.get(4);
//        String markAllFilter = filters.get(5);
//
//        // Category filtration
//        if (categoryFilterPosition != null && !categoryFilterPosition.equals(categoryAndReadAllFilter)){
//            for (Book book : libraryBooks){
//                if (book.getCategory().equals(categoryFilterPosition)) filteredBooks.add(book);
//            }
//        } else {
//            filteredBooks = libraryBooks;
//        }
//        // Read filtration
//        if (readFilterPosition != null && !readFilterPosition.equals(categoryAndReadAllFilter)){
//            List<Book> tempBooks = new ArrayList<>();
//            if (readFilterPosition.equals(readTrueState)){
//                for(Book book :  filteredBooks){
//                    if (book.getHasBeenRead() != null && book.getHasBeenRead()) tempBooks.add(book);
//                }
//            } else {
//                for(Book book :  filteredBooks){
//                    if (book.getHasBeenRead() == null || !book.getHasBeenRead()) tempBooks.add(book);
//                }
//            }
//            filteredBooks = tempBooks;
//        }
//        // Mark Filtration
//        if (markFilterPosition != null && !markFilterPosition.equals(markAllFilter)){
//            List<Book> tempBooks = new ArrayList<>();
//            int minimumMark = getMarkPosition(markFilterPosition);
//                for (Book book : filteredBooks) {
//                    if (book.getMark()>= minimumMark) tempBooks.add(book);
//                }
//            filteredBooks = tempBooks;
//        }
//
//        return filteredBooks;
//    }


}
