package com.aprouxdev.mabibliotheque.ui.main.library;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.aprouxdev.mabibliotheque.R;
import com.aprouxdev.mabibliotheque.models.Book;
import com.aprouxdev.mabibliotheque.viewmodels.BookViewModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class LibraryFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "LibraryFragment";
    // UI Vars
    LinearLayout filterButton;



    private BookViewModel bookViewModel;


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
    }

    private void setupViews(View view) {
        filterButton = view.findViewById(R.id.filterButton);
        filterButton.setOnClickListener(this);
    }

    private void subscribeObservers() {
        bookViewModel.getBooks().observe(getViewLifecycleOwner(), new Observer<List<Book>>() {
            @Override
            public void onChanged(List<Book> books) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case (R.id.filterButton):
                Log.d(TAG, "onClick: Filter button clicked");
                break;
            default:
                break;
        }
    }
}
