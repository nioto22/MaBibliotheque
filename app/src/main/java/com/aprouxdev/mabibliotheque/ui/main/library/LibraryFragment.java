package com.aprouxdev.mabibliotheque.ui.main.library;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aprouxdev.mabibliotheque.R;
import com.aprouxdev.mabibliotheque.models.Book;
import com.aprouxdev.mabibliotheque.ui.adapter.LibraryAdapter;
import com.aprouxdev.mabibliotheque.ui.bookDetail.BookDetailActivity;
import com.aprouxdev.mabibliotheque.viewmodels.BookViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.aprouxdev.mabibliotheque.util.Constants.BUNDLE_EXTRA_BOOK;
import static com.aprouxdev.mabibliotheque.util.Constants.MEDIUM_CELL_SIZE;
import static com.aprouxdev.mabibliotheque.util.Constants.SMALL_CELL_SIZE;

public class LibraryFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "LibraryFragment";
    // UI Vars
    // Top Container VIew
    private TextView numberOfBooksTextView;
    private LinearLayout filterButton;
    private TextView numberOfFilterTextView;
    private LinearLayout sortingButton;
    private TextView sortTypeTextView;
    // RecyclerView
    private RecyclerView libraryRecyclerView;
    // Filter Menu
    private RelativeLayout filterLayout;
    private Button closeFilterListener;
    private View openFilterGestureListener;
    private ImageView viewFilterListButton;
    private ImageView viewFilterSmallButton;
    private ImageView viewFilterMediumButton;

    // Data Vars
    private LibraryAdapter libraryAdapter;
    private BookViewModel bookViewModel;
    private List<Book> libraryBooks;
    private List<Book> filteredBooks;
    private GestureDetector detector;
    private List<Integer> recyclerViewCellSize;
    private boolean isListAdapterSelected;


    public static LibraryFragment newInstance() {
        return new LibraryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.library_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bookViewModel = ViewModelProviders.of(this).get(BookViewModel.class);

        subscribeObservers();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViews(view);
        setupFIlterView();
        setupGestureDetector();
        setupData();
        setupRecyclerView();
    }



    private void subscribeObservers() {
        bookViewModel.getBooks().observe(getViewLifecycleOwner(), new Observer<List<Book>>() {
            @Override
            public void onChanged(List<Book> books) {
                libraryBooks = books;
                setupNumberOfBooksTextView();
                libraryAdapter.displayNewBooks(books);
                Log.d(TAG, "onChanged: " + books.toString());
            }
        });
    }


    // -----------------------
    //      SETUP VIEWS
    // -----------------------

    private void setupViews(View view) {
        // Top container
        numberOfBooksTextView = view.findViewById(R.id.numberOfBooksTextView);
        filterButton = view.findViewById(R.id.filterButton);
        filterButton.setOnClickListener(this);
        numberOfFilterTextView = view.findViewById(R.id.numberOfFilterTextView);
        sortingButton = view.findViewById(R.id.sortingButton);
        sortingButton.setOnClickListener(this);
        sortTypeTextView = view.findViewById(R.id.sortTypeTextView);
        // RecyclerView
        libraryRecyclerView = view.findViewById(R.id.libraryRecyclerView);
        // Filter Menu
        filterLayout = view.findViewById(R.id.filterLayout);
        openFilterGestureListener = view.findViewById(R.id.filterGestureListener);
        closeFilterListener = view.findViewById(R.id.closeFilterListener);
        closeFilterListener.setOnClickListener(this);
        viewFilterListButton = view.findViewById(R.id.viewFilterListButton);
        viewFilterListButton.setOnClickListener(this::viewFilterButtonClicked);
        viewFilterSmallButton = view.findViewById(R.id.viewFilterSmallButton);
        viewFilterSmallButton.setOnClickListener(this::viewFilterButtonClicked);
        viewFilterMediumButton = view.findViewById(R.id.viewFilterMediumButton);
        viewFilterMediumButton.setOnClickListener(this::viewFilterButtonClicked);
    }

    private void setupFIlterView() {
        // TODO change views drawable
    }
    private void setupRecyclerView() {

        if (isListAdapterSelected){
            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            libraryRecyclerView.setLayoutManager(linearLayoutManager);
        } else {
            final GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), recyclerViewCellSize.get(0));
            libraryRecyclerView.setLayoutManager(gridLayoutManager);
        }
        libraryAdapter = new LibraryAdapter(getContext(), libraryBooks, recyclerViewCellSize);
        libraryRecyclerView.setAdapter(libraryAdapter);

        libraryAdapter.setOnItemClickListener(new LibraryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Book selectedBook = libraryAdapter.getBook(position);
                Log.d(TAG, "onItemClick: " + selectedBook.getTitle());
                Intent intent = new Intent(getContext(), BookDetailActivity.class);
                intent.putExtra(BUNDLE_EXTRA_BOOK, selectedBook);
                startActivity(intent);
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupGestureDetector() {
        detector = new GestureDetector(getContext(), new FilterMenuGestureListener());
        filterLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return detector.onTouchEvent(event);
            }
        });
        openFilterGestureListener.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return detector.onTouchEvent(event);
            }
        });
    }

    // -----------------------
    //      SETUP DATA
    // -----------------------

    private void setupData() {
        libraryBooks = new ArrayList<>();
        isListAdapterSelected = recyclerViewCellSize == null;
        setupTopContainerData();
    }

    private void setupTopContainerData() {
        setupNumberOfBooksTextView();
        setupFilterInfoTextView();
        setupSortingInfoTextView();
    }

    private void setupNumberOfBooksTextView() {
        String numberOfBooks = "" + libraryBooks.size() + " livres";
        numberOfBooksTextView.setText(numberOfBooks);
    }

    private void setupFilterInfoTextView(){
        setupFilterInfoTextView(0);
    }
    private void setupFilterInfoTextView(int numberOfFilters) {
        String filterText = numberOfFilters == 0 ? getResources().getString(R.string.no_filter)
                : numberOfFilters == 1 ? "" + numberOfFilters + "filtre sélectionné"
                : "" + numberOfFilters + "filtres sélectionnés";
        numberOfFilterTextView.setText(filterText);
    }

    private void setupSortingInfoTextView() {
        setupSortingInfoTextView(null);
    }
    private void setupSortingInfoTextView(String sort) {
        String sortText = (sort == null) ? getResources().getString(R.string.default_sort)
                : sort ;
        sortTypeTextView.setText(sortText);
    }
    // --------------------
    //        ACTION
    // --------------------

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.filterButton):
            case (R.id.closeFilterListener):
                openFilterLayout();
                break;
            case (R.id.sortingButton):
                //TODO openSortingPopupMenu()
                break;
            default:
                break;
        }
    }

    public void viewFilterButtonClicked(View v) {
        allViewsFilterButtonInGray();
        switch (v.getId()){
            case (R.id.viewFilterListButton):
                viewFilterListButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_view_list_turquoise));
                recyclerViewCellSize = null;
                isListAdapterSelected = true;
                setupRecyclerView();
                break;
            case (R.id.viewFilterSmallButton):
                viewFilterSmallButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_view_small_turquoise));
                recyclerViewCellSize = SMALL_CELL_SIZE;
                isListAdapterSelected = false;
                setupRecyclerView();
                break;
            case (R.id.viewFilterMediumButton):
                viewFilterMediumButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_view_medium_turquoise));
                recyclerViewCellSize = MEDIUM_CELL_SIZE;
                isListAdapterSelected = false;
                setupRecyclerView();
                break;
        }
    }

    private void allViewsFilterButtonInGray() {
        viewFilterListButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_view_list_gray));
        viewFilterSmallButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_view_small_gray));
        viewFilterMediumButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_view_medium_gray));
    }

    private void openFilterLayout() {
        boolean isFilterOpen = filterLayout.getVisibility() == View.VISIBLE;
        float fromX = isFilterOpen ? filterLayout.getScaleX() : 0f;
        float fromY = filterLayout.getScaleY();
        float toX = isFilterOpen ? 0f : filterLayout.getScaleX();
        float toY = fromY;
        animateFilterLayout(isFilterOpen, fromX, toX, fromY, toY);

    }

    private void animateFilterLayout(boolean isFilterOpen, float fromX, float toX, float fromY, float toY) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                fromX, toX, fromY, toY,
                Animation.RELATIVE_TO_SELF, 1f,
                Animation.RELATIVE_TO_SELF, 1f);
        scaleAnimation.setDuration(500);
        scaleAnimation.setInterpolator(new LinearOutSlowInInterpolator());
        scaleAnimation.setRepeatCount(0);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (!isFilterOpen) {
                    filterLayout.setVisibility(View.VISIBLE);
                    closeFilterListener.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                if (isFilterOpen) {
                    filterLayout.setVisibility(View.INVISIBLE);
                    closeFilterListener.setVisibility(View.GONE);
                }
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        filterLayout.startAnimation(scaleAnimation);
    }


    class FilterMenuGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent event) {
            Log.d(TAG, "onDown: ");

            // don't return false here or else none of the other
            // gestures will work
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.i(TAG, "onSingleTapConfirmed: ");
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.i(TAG, "onLongPress: ");
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.i(TAG, "onDoubleTap: ");
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            Log.i(TAG, "onScroll: ");
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            Log.d(TAG, "onFling: " + velocityX);
            boolean isFilterOpen = filterLayout.getVisibility() == View.VISIBLE;
            if ( isFilterOpen && velocityX > 0) openFilterLayout();
            if (!isFilterOpen && velocityX < 0) openFilterLayout();
            return true;
        }
    }

}






















