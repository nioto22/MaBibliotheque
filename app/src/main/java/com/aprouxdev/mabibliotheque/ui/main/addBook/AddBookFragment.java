package com.aprouxdev.mabibliotheque.ui.main.addBook;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.aprouxdev.mabibliotheque.R;
import com.aprouxdev.mabibliotheque.models.Book;
import com.aprouxdev.mabibliotheque.ui.adapters.BookAdapter;
import com.aprouxdev.mabibliotheque.ui.bookDetail.BookDetailActivity;
import com.aprouxdev.mabibliotheque.ui.addCapturedBook.SimpleCaptureActivity;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.app.Activity.RESULT_OK;
import static com.aprouxdev.mabibliotheque.util.Constants.BUNDLE_EXTRA_BOOK;
import static com.aprouxdev.mabibliotheque.util.Constants.BUNDLE_EXTRA_IS_NEW_BOOK;
import static com.uber.autodispose.AutoDispose.autoDisposable;

public class AddBookFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "AddBookFragment";

    public static final int SIMPLE_CAPTURE_REQUEST_CODE = 220;

    private AddBookViewModel viewModel;
    private BookAdapter booksAdapter;

    private EditText bookSearchInput;
    private RecyclerView booksRecycler;
    private ImageButton captureButton;

    public static AddBookFragment newInstance() {
        return new AddBookFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_book, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        setupViews(view);
        subscribeObservers();
        subscribeListeners();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        subscribeObservers();
        subscribeListeners();

        if (requestCode == SIMPLE_CAPTURE_REQUEST_CODE && RESULT_OK == resultCode){
            CharSequence intentText = data.getStringExtra(SimpleCaptureActivity.BUNDLE_EXTRA_TEXT);
            bookSearchInput.setText(intentText);
            bookSearchInput.getOnFocusChangeListener();

        }
    }

    private void setupViews(View view) {
        bookSearchInput = view.findViewById(R.id.book_search_input);
        booksRecycler = view.findViewById(R.id.books_recycler);
        captureButton = view.findViewById(R.id.capture_button);

        viewModel = ViewModelProviders.of(this).get(AddBookViewModel.class);
        viewModel.init();

        final GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        booksRecycler.setLayoutManager(layoutManager);
        booksAdapter = new BookAdapter();
        booksRecycler.setAdapter(booksAdapter);

        booksAdapter.setOnItemClickListener(new BookAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Book selectedBook = booksAdapter.getBook(position);
                Log.d(TAG, "onItemClick: " + selectedBook.getTitle());
                Intent intent = new Intent(getContext(), BookDetailActivity.class);
                intent.putExtra(BUNDLE_EXTRA_BOOK, selectedBook);
                intent.putExtra(BUNDLE_EXTRA_IS_NEW_BOOK, true);
                startActivity(intent);
            }
        });

    }

    private void subscribeObservers() {
        RxTextView.textChanges(bookSearchInput)
                .debounce(500, TimeUnit.MILLISECONDS)
                .filter(inputText -> !TextUtils.isEmpty(inputText))
                .as(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(inputText -> viewModel.searchBooks(inputText));;

        viewModel.getBooks().observe(getViewLifecycleOwner(), booksAdapter::displayNewBooks);
    }

    private void subscribeListeners() { captureButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case (R.id.capture_button):{
                Intent intent = new Intent(getActivity(), SimpleCaptureActivity.class);
                startActivityForResult(intent, SIMPLE_CAPTURE_REQUEST_CODE);
                break;
            }
            default:
                break;
        }
    }
}
