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
import android.widget.Toast;

import com.aprouxdev.mabibliotheque.R;
import com.aprouxdev.mabibliotheque.database.firestoreDatabase.LibraryHelper;
import com.aprouxdev.mabibliotheque.models.Book;
import com.aprouxdev.mabibliotheque.ui.adapter.LibraryAdapter;
import com.aprouxdev.mabibliotheque.ui.bookDetail.BookDetailActivity;
import com.aprouxdev.mabibliotheque.ui.friends.chat.discussion.SelectBookActivity;
import com.aprouxdev.mabibliotheque.ui.main.MainActivity;
import com.aprouxdev.mabibliotheque.util.Constants.FILTERS;
import com.aprouxdev.mabibliotheque.viewmodels.LocalBookViewModel;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
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

import static android.app.Activity.RESULT_OK;
import static com.aprouxdev.mabibliotheque.ui.adapter.LibraryAdapter.DELETE_BUTTON_CLICKED;
import static com.aprouxdev.mabibliotheque.ui.adapter.LibraryAdapter.SHARE_BUTTON_CLICKED;
import static com.aprouxdev.mabibliotheque.util.Constants.BUNDLE_EXTRA_BOOK;
import static com.aprouxdev.mabibliotheque.util.Constants.BUNDLE_EXTRA_SELECTED_BOOK_ID;
import static com.aprouxdev.mabibliotheque.util.Constants.BUNDLE_EXTRA_SELECTED_BOOK_IMAGE;
import static com.aprouxdev.mabibliotheque.util.Constants.FILTERS.CATEGORY;
import static com.aprouxdev.mabibliotheque.util.Constants.FILTERS.MARK;
import static com.aprouxdev.mabibliotheque.util.Constants.FILTERS.READ;
import static com.aprouxdev.mabibliotheque.util.Constants.MEDIUM_CELL_SIZE;
import static com.aprouxdev.mabibliotheque.util.Constants.SHARED_PREF_NAME;
import static com.aprouxdev.mabibliotheque.util.Constants.SHARED_PREF_VIEW;
import static com.aprouxdev.mabibliotheque.util.Constants.SMALL_CELL_SIZE;
import static com.aprouxdev.mabibliotheque.util.Constants.VIEWS_PREF.LIST;
import static com.aprouxdev.mabibliotheque.util.Constants.VIEWS_PREF.MEDIUM;
import static com.aprouxdev.mabibliotheque.util.Constants.VIEWS_PREF.SMALL;

public class LibraryFragment extends Fragment
        implements View.OnClickListener,
        AdapterView.OnItemSelectedListener{
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
    private TextView filterLayoutTitle;
    private Button closeFilterListener;
    private View openFilterGestureListener;
    private ImageView viewFilterListButton;
    private ImageView viewFilterSmallButton;
    private ImageView viewFilterMediumButton;
    private Spinner filterLayoutReadSpinner;
    private Spinner filterLayoutCategorySpinner;
    private TextView filterLayoutViewTitleInfo;
    private Spinner filterLayoutMarkSpinner;
    private ImageView filterStar1Button, filterStar2Button, filterStar3Button, filterStar4Button,
            filterStar5Button;
    private TextView filterStarAndMoreTextView;


    // Data Vars
    private boolean bIsLocalDatabase;
    private String bUserUid;
    private LibraryAdapter libraryAdapter;
    private LocalBookViewModel localBookViewModel;
    private LibraryViewModel viewModel;
    private List<Book> libraryBooks;
    private List<Book> filteredBooks;
    private List<String> categoryArray;
    private GestureDetector detector;
    private List<Integer> recyclerViewCellSize;
    private boolean isListAdapterSelected;
    private SharedPreferences preferences;

    private String firstCategory;

    private int numberOfFilterUsed;
    private String categoryFilterPosition;
    private String readFilterPosition;
    private String markFilterPosition;

    public static LibraryFragment newInstance() {
        return new LibraryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_library, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        localBookViewModel = ViewModelProviders.of(this).get(LocalBookViewModel.class);
        viewModel = ViewModelProviders.of(this).get(LibraryViewModel.class);

        getBasedVars();

        subscribeObservers();

    }


    private void getBasedVars() {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            if (mainActivity.bUserUid != null) bUserUid = mainActivity.bUserUid;
            bIsLocalDatabase = mainActivity.bIsUserPrefNoLogin;

            // Get filter from MainActivity
            if (mainActivity.libraryFragmentReadFilterCalled) {
                numberOfFilterUsed = 1;
                readFilterPosition = getResources().getString(R.string.read_filter_read);
                updateAllData();
            }
            if (mainActivity.libraryFragmentFavoriteFilterCalled){
                numberOfFilterUsed = 1;
                markFilterPosition = getResources().getString(R.string.mark_favorite_state);
                updateAllData();
            }
        }
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
        if (bIsLocalDatabase) subscribeLocalObserver();
        else subscribeFirestoreObserver();
    }

    private void subscribeFirestoreObserver() {
        if(bUserUid != null){
            LibraryHelper.getAllBooks(bUserUid).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot books,
                                    @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }
                    libraryBooks = new ArrayList<>();
                    for (QueryDocumentSnapshot document : books) {
                        if (document != null){
                            final Book book = document.toObject(Book.class);
                            libraryBooks.add(book);
                        }
                    }
                    categoryArray = viewModel.getSpinnerCategoryArray(firstCategory, libraryBooks);
                    updateAllData();
                }
            });
        } else {
            subscribeLocalObserver();
        }
    }

    private void subscribeLocalObserver() {
        localBookViewModel.getBooks().observe(getViewLifecycleOwner(), new Observer<List<Book>>() {
            @Override
            public void onChanged(List<Book> books) {
                libraryBooks = books;
                categoryArray = viewModel.getSpinnerCategoryArray(firstCategory, libraryBooks);
                updateAllData();
            }
        });
    }

    private void updateAllData() {
        filterAllBooks();
        setupNumberOfBooksTextView();
        setupCategorySpinnerView();
    }

    /**
     * Filtration only if filterPosition != null && != AllFilters
     * Category filtration on libraryBooks : filteredBooks keep only right category books
     * Read and Mark filtration on filteredBooks list
     */
    public void filterAllBooks() {
        filteredBooks = new ArrayList<>();
        String categoryAndReadAllFilter = getResources().getString(R.string.readAndCategoryAllFilter);
        String markAllFilter = getResources().getString(R.string.markAllFilter);
        String readTrueState = getResources().getString(R.string.read_filter_read);

        // Category filtration
        if (categoryFilterPosition != null && !categoryFilterPosition.equals(categoryAndReadAllFilter)){
            for (Book book : libraryBooks){
                if (book.getCategory().equals(categoryFilterPosition)) filteredBooks.add(book);
            }
        } else {
            filteredBooks = libraryBooks;
        }
        // Read filtration
        if (readFilterPosition != null && !readFilterPosition.equals(categoryAndReadAllFilter)){
            List<Book> tempBooks = new ArrayList<>();
            if (readFilterPosition.equals(readTrueState)){
                for(Book book :  filteredBooks){
                    if (book.getHasBeenRead() != null && book.getHasBeenRead()) tempBooks.add(book);
                }
            } else {
                for(Book book :  filteredBooks){
                    if (book.getHasBeenRead() == null || !book.getHasBeenRead()) tempBooks.add(book);
                }
            }
            filteredBooks = tempBooks;
        }
        // Mark Filtration
        if (markFilterPosition != null && !markFilterPosition.equals(markAllFilter)){
            List<Book> tempBooks = new ArrayList<>();
            int minimumMark = getMarkPosition(markFilterPosition);
            for (Book book : filteredBooks) {
                if (book.getMark()>= minimumMark) tempBooks.add(book);
            }
            filteredBooks = tempBooks;
        }
        libraryAdapter.displayNewBooks(filteredBooks);
    }

    private int getMarkPosition(String markFilterPosition) {
        char numberChar = markFilterPosition.charAt(0);
        return Integer.parseInt(String.valueOf(numberChar));
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
        filterLayoutTitle = view.findViewById(R.id.filterLayoutTitle);
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
        filterLayoutMarkSpinner = view.findViewById(R.id.filterLayoutMarkSpinner);
        filterLayoutMarkSpinner.setOnItemSelectedListener(this);
        filterStar1Button = view.findViewById(R.id.filterStar1Button);
        filterStar2Button = view.findViewById(R.id.filterStar2Button);
        filterStar3Button = view.findViewById(R.id.filterStar3Button);
        filterStar4Button = view.findViewById(R.id.filterStar4Button);
        filterStar5Button = view.findViewById(R.id.filterStar5Button);
        filterStarAndMoreTextView = view.findViewById(R.id.filterStarAndMoreTextView);

    }

    private void setupFilterViews() {
        setupViewsFilterView();
        setupReadFilterSpinnerView();
        setupMarkFilterSpinnerView();
        // INFO : setupCategorySpinnerView is in onChanged of observer

    }

    private void setupCategorySpinnerView() {
        // INFO : setupCategorySpinnerView is in onChanged of observer
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(Objects.requireNonNull(getContext()), R.layout.item_custom_spinner_simple,
                categoryArray);
        categoryAdapter.setDropDownViewResource(R.layout.item_custom_spinner_dropdown);
        filterLayoutCategorySpinner.setAdapter(categoryAdapter);
    }

    private void setupReadFilterSpinnerView() {
        ArrayAdapter<CharSequence> readAdapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getContext()),
                R.array.read_array, R.layout.item_custom_spinner_simple);
        // Specify the layout to use when the list of choices appears
        readAdapter.setDropDownViewResource(R.layout.item_custom_spinner_dropdown);
// Apply the adapter to the spinner
        filterLayoutReadSpinner.setAdapter(readAdapter);
    }

    private void setupMarkFilterSpinnerView() {
        ArrayAdapter<CharSequence> markAdapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getContext()),
                R.array.mark_array, R.layout.item_custom_mark_spinner_simple);
        markAdapter.setDropDownViewResource(R.layout.item_custom_spinner_dropdown);
        filterLayoutMarkSpinner.setAdapter(markAdapter);
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
                // Check if it's LibraryActivity(Main) or SelectedBookActivity
                Class parentActivityClass = Objects.requireNonNull(getActivity()).getClass();
                if (parentActivityClass == MainActivity.class) {
                    Intent intent = new Intent(getContext(), BookDetailActivity.class);
                    intent.putExtra(BUNDLE_EXTRA_BOOK, selectedBook);
                    startActivity(intent);
                } else if (parentActivityClass == SelectBookActivity.class) {
                    Intent intent = new Intent();
                    intent.putExtra(BUNDLE_EXTRA_SELECTED_BOOK_ID, selectedBook.getId());
                    intent.putExtra(BUNDLE_EXTRA_SELECTED_BOOK_IMAGE, selectedBook.getThumbnailLink());
                    getActivity().setResult(RESULT_OK, intent);
                }

            }
        });
        libraryAdapter.setOnItemLongClickListener(new LibraryAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(String click, int position) {
                Book selectedBook = libraryAdapter.getBook(position);
                if (click.equals(SHARE_BUTTON_CLICKED)){
                    Toast.makeText(getContext(), "Share button clicked for " + selectedBook.getTitle(), Toast.LENGTH_SHORT).show();
                } else if (click.equals(DELETE_BUTTON_CLICKED)){
                    Toast.makeText(getContext(), "Delete button clicked for " + selectedBook.getTitle(), Toast.LENGTH_SHORT).show();
                }
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
                String categoryState = parent.getItemAtPosition(position).toString();
                updateNumberOfFilters(CATEGORY, categoryState);

                break;
            case (R.id.filterLayoutReadSpinner):
                String readState = parent.getItemAtPosition(position).toString();
                updateNumberOfFilters(READ, readState);
                break;
            case (R.id.filterLayoutMarkSpinner):
                String markState = parent.getItemAtPosition(position).toString();
                updateNumberOfFilters(MARK, markState);
                updateStarViews(markState);
                break;
        }
    }

    private void updateStarViews(String markState) {
        int numberOfStars = markState.equals(getResources().getString(R.string.markAllFilter)) ? 0 : getMarkPosition(markState);
        int andMoreVisibility = numberOfStars == 5 ? View.INVISIBLE : View.VISIBLE;
        filterStarAndMoreTextView.setVisibility(andMoreVisibility);
        List<ImageView> stars = Arrays.asList(filterStar1Button, filterStar2Button, filterStar3Button, filterStar4Button, filterStar5Button);
        for (ImageView image : stars) {
            image.setImageDrawable(getResources().getDrawable(R.drawable.ic_empty_star));
        }
        for (int i = 0; i < numberOfStars ; i++) {
            stars.get(i).setImageDrawable(getResources().getDrawable(R.drawable.ic_full_star));
        }
    }

    private void updateNumberOfFilters(FILTERS filterType, String filterState) {
        String previousState;
        switch (filterType) {
            case CATEGORY :
                previousState = categoryFilterPosition != null ? categoryFilterPosition : getResources().getString(R.string.readAndCategoryAllFilter);
                if (!filterState.equals(previousState)) {  // New filter state clicked
                    if (filterState.equals(getResources().getString(R.string.readAndCategoryAllFilter))){ // All state clicked
                        numberOfFilterUsed --;
                    } else if (previousState.equals(getResources().getString(R.string.readAndCategoryAllFilter))){  // Previous was All state
                        numberOfFilterUsed ++;
                    }
                }
                categoryFilterPosition = filterState;
                break;
            case READ :
                previousState = readFilterPosition != null ? readFilterPosition : getResources().getString(R.string.readAndCategoryAllFilter);
                if (!filterState.equals(previousState)) {  // New filter state clicked
                    if (filterState.equals(getResources().getString(R.string.readAndCategoryAllFilter))){ // All state clicked
                        numberOfFilterUsed --;
                    } else if (previousState.equals(getResources().getString(R.string.readAndCategoryAllFilter))) {  // Previous was All state
                        numberOfFilterUsed ++;
                    }
                }
                readFilterPosition = filterState;
                break;
            case MARK :
                previousState = markFilterPosition != null ? markFilterPosition : getResources().getString(R.string.markAllFilter);
                if (!filterState.equals(previousState)) {  // New filter state clicked
                    if (filterState.equals(getResources().getString(R.string.markAllFilter))){ // All state clicked
                        numberOfFilterUsed --;
                    } else if (previousState.equals(getResources().getString(R.string.markAllFilter))){  // Previous was All state
                        numberOfFilterUsed ++;
                    }
                }
                markFilterPosition = filterState;
                break;
        }
        updateNumberOfFiltersViews();
        filterAllBooks();
    }

    private void updateNumberOfFiltersViews() {
        String filterButtonText = numberOfFilterUsed == 0 ? "Aucun sélectionné" : numberOfFilterUsed == 1 ? "1 sélectionné" : "" + numberOfFilterUsed + " sélectionnés";
        numberOfFilterTextView.setText(filterButtonText);
        String filterTitleText = numberOfFilterUsed == 0 ? "FILTRES" : "FILTERS (" + numberOfFilterUsed + ")";
        filterLayoutTitle.setText(filterTitleText);
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






















