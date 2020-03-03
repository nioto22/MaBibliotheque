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

    // BUNDLE
    public static final String BUNDLE_EXTRA_BOOK = "BUNDLE_EXTRA_BOOK";
    public static final String BUNDLE_EXTRA_IS_NEW_BOOK = "BUNDLE_EXTRA_IS_NEW_BOOK";
    public static final int ADD_LIBRARY_INTENT_FOR_RESULT = 12 ;
    public static final int ADD_LIBRARY_DRAWER_ITEM_INDEX = 3;
    public static final String BUNDLE_EXTRA_DISCUSSION_UID = "BUNDLE_EXTRA_DISCUSSION_UID";
    public static final String BUNDLE_EXTRA_SELECTED_BOOK_ID = "BUNDLE_EXTRA_SELECTED_BOOK_ID";
    public static final String BUNDLE_EXTRA_SELECTED_BOOK_IMAGE = "BUNDLE_EXTRA_SELECTED_BOOK_IMAGE";

    // Popup Fragment
    public static final String FRIEND_DETAILS_POPUP_TAG = "FRIEND_DETAILS_POPUP_TAG";
    public static final String FRIEND_DETAILS_POPUP_BUNDLE_USER_UID = "FRIEND_DETAILS_POPUP_BUNDLE_USER_UID";
    public static final String FRIEND_DETAILS_POPUP_BUNDLE_FRIEND = "FRIEND_DETAILS_POPUP_BUNDLE_FRIEND";

    public static final String DISCUSSION_SAVE_POPUP_TAG = "DISCUSSION_SAVE_POPUP_TAG";
    public static final String DISCUSSION_SAVE_POPUP_BUNDLE_USER_UID = "DISCUSSION_SAVE_POPUP_BUNDLE_USER_UID";
    public static final String DISCUSSION_SAVE_POPUP_BUNDLE_USERS_LIST = "DISCUSSION_SAVE_POPUP_BUNDLE_USERS_LIST";

    // Library data
    public static final List<Integer> SMALL_CELL_SIZE = Arrays.asList(4, 4);
    public static final List<Integer> MEDIUM_CELL_SIZE = Arrays.asList(3, 3);

    public enum FILTERS {
        VIEWS,
        CATEGORY,
        READ,
        MARK
    }

    // Shared Preference
    public static final String SHARED_PREF_NAME = "SHARED_PREF_NAME";
    public static final String SHARED_PREF_VIEW = "SHARED_PREF_VIEW";
    public static enum VIEWS_PREF {
        LIST,
        SMALL,
        MEDIUM
    }
    public static final String SHARED_PREF_NO_LOGIN = "SHARED_PREF_LOGIN";
    public static final String SHARED_USER_UID = "SHARED_USER_UID";
    public static final String SHARED_LOCAL_LIBRARY_ALREADY_ADDED_BOOLEAN = "SHARED_LOCAL_LIBRARY_ALREADY_ADDED_BOOLEAN";
    public static final String SHARED_SELECTED_USERS_OF_DISCUSSION = "SHARED_SELECTED_USERS_OF_DISCUSSION";

    // Friends Activity
    public static final int FRIENDS_HOME_TAB_TAG = 0;
    public static final int FRIENDS_FRIENDS_TAB_TAG = 1;
    public static final int FRIENDS_CHAT_TAB_TAG = 2;

    // Discussion Activity
    public static final int RC_CHOOSE_BOOK = 54;

    // Tools
    public static final String YESTERDAY = "Hier";
}


