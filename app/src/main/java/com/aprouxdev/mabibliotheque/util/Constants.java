package com.aprouxdev.mabibliotheque.util;

import com.aprouxdev.mabibliotheque.R;

import java.util.Arrays;
import java.util.List;

public class Constants {

    // GOOGLE BOOKS API
    public static final String KEY_BOOKS_API = "AIzaSyDF8TnlHQAM5QYFpyUYOXS3KsD5rzAG55g";

    public static final List<Integer> months = Arrays.asList(R.string.month0, R.string.month1,
            R.string.month2, R.string.month3, R.string.month4, R.string.month5, R.string.month6,
            R.string.month7, R.string.month8, R.string.month9, R.string.month10, R.string.month11);


    public static final String BUNDLE_EXTRA_BOOK = "BUNDLE_EXTRA_BOOK";
    public static final List<Integer> SMALL_CELL_SIZE = Arrays.asList(4, 4);
    public static final List<Integer> MEDIUM_CELL_SIZE = Arrays.asList(3, 3);
}


// https://www.googleapis.com/books/v1/volumes?q=les fureurs invisibles