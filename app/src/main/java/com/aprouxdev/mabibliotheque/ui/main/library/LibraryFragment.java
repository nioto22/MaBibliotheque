package com.aprouxdev.mabibliotheque.ui.main.library;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.aprouxdev.mabibliotheque.R;
import com.aprouxdev.mabibliotheque.models.Book;
import com.aprouxdev.mabibliotheque.ui.adapter.LibraryAdapter;
import com.aprouxdev.mabibliotheque.ui.bookDetail.BookDetailActivity;
import com.aprouxdev.mabibliotheque.viewmodels.BookViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
import static com.aprouxdev.mabibliotheque.util.Constants.SHARED_PREF_NAME;
import static com.aprouxdev.mabibliotheque.util.Constants.SHARED_PREF_VIEW;
import static com.aprouxdev.mabibliotheque.util.Constants.SMALL_CELL_SIZE;
import static com.aprouxdev.mabibliotheque.util.Constants.VIEWS_PREF.*;

public class LibraryFragment extends Fragment
        implements View.OnClickListener,
        AdapterView.OnItemSelectedListener {
    private static final String TAG = "LibraryFragment";
    // UI Vars
    // Top Container View
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
    private Spinner filterLayoutReadSpinner;
    private Spinner filterLayoutCategorySpinner;
    private TextView filterLayoutViewTitleInfo;


    // Data Vars
    private LibraryAdapter libraryAdapter;
    private BookViewModel bookViewModel;
    private LibraryViewModel viewModel;
    private List<Book> libraryBooks;
    private List<Book> filteredBooks;
    private List<String> categoryArray;
    private GestureDetector detector;
    private List<Integer> recyclerViewCellSize;
    private boolean isListAdapterSelected;
    private SharedPreferences preferences;
    private int numberOfFilterUsed;
    private String firstCategory;

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
        viewModel = ViewModelProviders.of(this).get(LibraryViewModel.class);

        subscribeObservers();


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        preferences = Objects.requireNonNull(getActivity()).getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        firstCategory = getResources().getString(R.string.all);
        setupViews(view);
        setupFilterViews();
        setupGestureDetector();

        setupData();
        setupRecyclerView();
    }



    private void subscribeObservers() {
        bookViewModel.getBooks().observe(getViewLifecycleOwner(), new Observer<List<Book>>() {
            @Override
            public void onChanged(List<Book> books) {
                libraryBooks = books;
                categoryArray = viewModel.getSpinnerCategoryArray(firstCategory, books);
                setupNumberOfBooksTextView();
                libraryAdapter.displayNewBooks(books);
                setupCategorySpinnerView();
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
        filterLayoutReadSpinner = view.findViewById(R.id.filterLayoutReadSpinner);
        filterLayoutReadSpinner.setOnItemSelectedListener(this);
        filterLayoutCategorySpinner = view.findViewById(R.id.filterLayoutCategorySpinner);
        filterLayoutCategorySpinner.setOnItemSelectedListener(this);
        filterLayoutViewTitleInfo = view.findViewById(R.id.filterLayoutViewTitleInfo);

    }

    private void setupFilterViews() {
        setupViewsFilterView();
        setupReadFilterSpinnerView();


    }

    private void setupCategorySpinnerView() {

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(Objects.requireNonNull(getContext()), R.layout.custom_spinner_simple_item,
                categoryArray);
        categoryAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        filterLayoutCategorySpinner.setAdapter(categoryAdapter);
    }

    private void setupReadFilterSpinnerView() {
        ArrayAdapter<CharSequence> readAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.read_array, R.layout.custom_spinner_simple_item);
        // Specify the layout to use when the list of choices appears
        readAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
// Apply the adapter to the spinner
        filterLayoutReadSpinner.setAdapter(readAdapter);
    }

    private void setupViewsFilterView() {
        allViewsFilterButtonInGray();
        // Setup views drawable
        String views = preferences.getString(SHARED_PREF_VIEW, LIST.name());
        switch (views){
            case "SMALL":
                setupFilterSmallView();
                break;
            case "MEDIUM":
                setupFilterMediumView();
                break;
            case "LIST":
            default:
                setupFilterListView();
                break;
        }
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
        String bookPural = libraryBooks.size() > 1 ? " livres" : " livre";
        String numberOfBooks = "" + libraryBooks.size() + bookPural;
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
                setupFilterListView();
                break;
            case (R.id.viewFilterSmallButton):
                setupFilterSmallView();
                break;
            case (R.id.viewFilterMediumButton):
                setupFilterMediumView();
                break;
        }
    }

    // Read Spinner Listener
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case (R.id.filterLayoutCategorySpinner):
                //TODO update data
                //TODO add filter int if not all
                break;
            case (R.id.filterLayoutReadSpinner):
                //TODO update data
                //TODO add filter int if not all
                break;
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private void setupFilterListView() {
        viewFilterListButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_view_list_turquoise));
        recyclerViewCellSize = null;
        isListAdapterSelected = true;
        preferences.edit().putString(SHARED_PREF_VIEW, LIST.name()).apply();
        filterLayoutViewTitleInfo.setText(getResources().getString(R.string.list_info));
        setupRecyclerView();
    }
    private void setupFilterSmallView() {
        viewFilterSmallButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_view_small_turquoise));
        recyclerViewCellSize = SMALL_CELL_SIZE;
        isListAdapterSelected = false;
        preferences.edit().putString(SHARED_PREF_VIEW, SMALL.name()).apply();
        filterLayoutViewTitleInfo.setText(getResources().getString(R.string.small_views_info));
        setupRecyclerView();
    }
    private void setupFilterMediumView() {
        viewFilterMediumButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_view_medium_turquoise));
        recyclerViewCellSize = MEDIUM_CELL_SIZE;
        isListAdapterSelected = false;
        preferences.edit().putString(SHARED_PREF_VIEW, MEDIUM.name()).apply();
        filterLayoutViewTitleInfo.setText(getResources().getString(R.string.medium_views_info));
        setupRecyclerView();
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
        scaleAnimation.setDuration(400);
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






















