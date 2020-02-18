package com.aprouxdev.mabibliotheque.ui.main.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aprouxdev.mabibliotheque.R;
import com.aprouxdev.mabibliotheque.database.firestoreDatabase.LibraryHelper;
import com.aprouxdev.mabibliotheque.models.Book;
import com.aprouxdev.mabibliotheque.ui.adapter.HorizontalAdapter;
import com.aprouxdev.mabibliotheque.ui.bookDetail.BookDetailActivity;
import com.aprouxdev.mabibliotheque.viewmodels.LocalBookViewModel;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.aprouxdev.mabibliotheque.ui.main.MainActivity.BUNDLE_IS_USER_PREF_NO_LOGIN;
import static com.aprouxdev.mabibliotheque.ui.main.MainActivity.BUNDLE_USER_UID;
import static com.aprouxdev.mabibliotheque.util.Constants.BUNDLE_EXTRA_BOOK;

public class HomeFragment extends Fragment
        implements HorizontalAdapter.OnItemClickListener {
    private static final String TAG = "HomeFragment";

    // UI Vars
    // Top Container
    private TextView homeNumberOfBooksNumber;
    private TextView homeNumberToReadNumber;
    private TextView homeNumberFavoriteNumber;
    private RelativeLayout topSeeAllButton;
    // Bottom Container
    private RelativeLayout homeLastEntriesSeeAllButton;
    private RecyclerView homeLastEntriesRecyclerView;

    private TextView homeToReadTitleNumber;
    private RelativeLayout homeToReadSeeAllButton;
    private RecyclerView homeToReadRecyclerView;

    private TextView homeFavoriteTitleNumber;
    private RelativeLayout homeFavoriteSeeAllButton;
    private RecyclerView homeFavoriteRecyclerView;
    // No book
    private TextView homeLastEntriesNoBook;
    private TextView homeToReadNoBook;
    private TextView homeFavoriteNoBook;
    private LinearLayout homeNoBooksAddLibraryButton;
    private LinearLayout homeNoBooksAddBookButton;


    // DATA
    private boolean bIsLocalDatabase;
    private String bUserUid;
    private HomeViewModel mViewModel;
    private LocalBookViewModel localBookViewModel;
    private List<Book> allBooks = new ArrayList<>();
    private List<Book> toReadBooks = new ArrayList<>();
    private List<Book> favoriteBooks = new ArrayList<>();
    private HorizontalAdapter allRecyclerAdapter;
    private HorizontalAdapter toReadRecyclerAdapter;
    private HorizontalAdapter favoriteRecyclerAdapter;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            bIsLocalDatabase = getArguments().getBoolean(BUNDLE_IS_USER_PREF_NO_LOGIN);
            bUserUid = getArguments().getString(BUNDLE_USER_UID);
            Log.d(TAG, "onActivityCreated: is local database : " + bIsLocalDatabase );
            Log.d(TAG, "onActivityCreated: user id" + bUserUid);
        }

        mViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        localBookViewModel = ViewModelProviders.of(this).get(LocalBookViewModel.class);



        if(bIsLocalDatabase){
            subscribeLocalObservers();
        } else {
            subscribeFirestoreObservers();
        }
        setupData();
    }




    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViews(view);


    }

    private void subscribeFirestoreObservers() {
        if(bUserUid != null){
            LibraryHelper.getAllBooks(bUserUid).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot books,
                                    @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    for (QueryDocumentSnapshot document : books) {
                        if (document != null){
                            final Book book = document.toObject(Book.class);
                            Log.d(TAG, "onEvent: Get Book successfull book name = " + book.getTitle());
                            Log.d(TAG, "onEvent: Get Book successfull book id = " + book.getId());
                            allBooks.add(book);
                        }
                    }
                    updateAllData();
                }
            });
        }

    }

    private void subscribeLocalObservers() {
        localBookViewModel.getBooks().observe(getViewLifecycleOwner(), new Observer<List<Book>>() {
            @Override
            public void onChanged(List<Book> books) {
                allBooks = books;
                updateAllData();
            }
        });
    }

    private void updateAllData() {
        toReadBooks = mViewModel.setupToReadBooks(allBooks);
        favoriteBooks = mViewModel.setupFavoriteBooks(allBooks);
        setupData();
        setupNoBookViews();
        setupRecyclerViews();
    }


    // -----------------------
    //      SETUP VIEWS
    // -----------------------

    private void setupViews(View view) {
        homeNumberOfBooksNumber = view.findViewById(R.id.homeNumberOfBooksNumber);
        homeNumberToReadNumber = view.findViewById(R.id.homeNumberToReadNumber);
        homeNumberFavoriteNumber = view.findViewById(R.id.homeNumberFavoriteNumber);
        topSeeAllButton = view.findViewById(R.id.topSeeAllButton);
        homeLastEntriesSeeAllButton = view.findViewById(R.id.homeLastEntriesSeeAllButton);
        homeLastEntriesRecyclerView = view.findViewById(R.id.homeLastEntriesRecyclerView);
        homeToReadTitleNumber = view.findViewById(R.id.homeToReadTitleNumber);
        homeToReadSeeAllButton = view.findViewById(R.id.homeToReadSeeAllButton);
        homeToReadRecyclerView = view.findViewById(R.id.homeToReadRecyclerView);
        homeFavoriteTitleNumber = view.findViewById(R.id.homeFavoriteTitleNumber);
        homeFavoriteSeeAllButton = view.findViewById(R.id.homeFavoriteSeeAllButton);
        homeFavoriteRecyclerView = view.findViewById(R.id.homeFavoriteRecyclerView);
        homeLastEntriesNoBook = view.findViewById(R.id.homeLastEntriesNoBook);
        homeToReadNoBook = view.findViewById(R.id.homeToReadNoBook);
        homeFavoriteNoBook = view.findViewById(R.id.homeFavoriteNoBook);
        homeNoBooksAddBookButton = view.findViewById(R.id.homeNoBooksAddBookButton);
        homeNoBooksAddLibraryButton = view.findViewById(R.id.homeNoBooksAddLibraryButton);
    }


    // -----------------------
    //      SETUP DATA
    // -----------------------

    private void setupData() {
        setupTextViewsText();
    }

    private void setupTextViewsText() {
        String allBooksNumber = String.valueOf(allBooks.size());
        String toReadNumber = String.valueOf(toReadBooks.size());
        String toReadTitleNumber = "(" + toReadNumber + ")";
        String favoriteNumber = String.valueOf(favoriteBooks.size());
        String favoriteTitleNumber = "(" + favoriteNumber + ")";
        homeNumberOfBooksNumber.setText(allBooksNumber);
        homeNumberToReadNumber.setText(toReadNumber);
        homeNumberFavoriteNumber.setText(favoriteNumber);
        homeToReadTitleNumber.setText(toReadTitleNumber);
        homeFavoriteTitleNumber.setText(favoriteTitleNumber);

    }

    private String getFormattedText(int resource, int size) {
        String charPlural = size > 1 ? "s" : "";
        String formattedText = (getResources().getString(resource)) + charPlural;
        return formattedText;
    }

    private void setupNoBookViews() {
        if (allBooks.size() == 0) {
            homeLastEntriesNoBook.setVisibility(View.VISIBLE);
        }
        if (toReadBooks.size() == 0) {
            homeToReadNoBook.setVisibility(View.VISIBLE);
        }
        if (favoriteBooks.size() == 0) {
            homeFavoriteNoBook.setVisibility(View.VISIBLE);
        }
    }

    private void setupRecyclerViews() {
        LinearLayoutManager allLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager toReadLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager favoriteLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        homeLastEntriesRecyclerView.setLayoutManager(allLayoutManager);
        allRecyclerAdapter = new HorizontalAdapter(allBooks);
        homeLastEntriesRecyclerView.setAdapter(allRecyclerAdapter);
        allRecyclerAdapter.setOnItemClickListener(this);

        homeToReadRecyclerView.setLayoutManager(toReadLayoutManager);
        toReadRecyclerAdapter = new HorizontalAdapter(toReadBooks);
        homeToReadRecyclerView.setAdapter(toReadRecyclerAdapter);
        toReadRecyclerAdapter.setOnItemClickListener(this);

        homeFavoriteRecyclerView.setLayoutManager(favoriteLayoutManager);
        favoriteRecyclerAdapter = new HorizontalAdapter(favoriteBooks);
        homeFavoriteRecyclerView.setAdapter(favoriteRecyclerAdapter);
        favoriteRecyclerAdapter.setOnItemClickListener(this);

    }

    private void updateRecyclerviews() {
        allRecyclerAdapter.notifyDataSetChanged();
        toReadRecyclerAdapter.notifyDataSetChanged();
        favoriteRecyclerAdapter.notifyDataSetChanged();
    }


    // -----------------------
    //      ACTION
    // -----------------------


    public void seeAllButtonClicked(View v){
        // don't remove
    }

    @Override
    public void onItemClick(Book selectedBook) {
        Intent intent = new Intent(getContext(), BookDetailActivity.class);
        intent.putExtra(BUNDLE_EXTRA_BOOK, selectedBook);
        startActivity(intent);
    }


}
