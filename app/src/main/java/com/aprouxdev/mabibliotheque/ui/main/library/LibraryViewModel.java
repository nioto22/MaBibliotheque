package com.aprouxdev.mabibliotheque.ui.main.library;

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
}
