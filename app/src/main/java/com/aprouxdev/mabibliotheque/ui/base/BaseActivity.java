package com.aprouxdev.mabibliotheque.ui.base;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.aprouxdev.mabibliotheque.R;
import com.aprouxdev.mabibliotheque.database.firestoreDatabase.LibraryHelper;
import com.aprouxdev.mabibliotheque.models.Book;
import com.aprouxdev.mabibliotheque.ui.authentication.LoginActivity;
import com.aprouxdev.mabibliotheque.viewmodels.LocalBookViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import static com.aprouxdev.mabibliotheque.util.Constants.SHARED_PREF_NAME;
import static com.aprouxdev.mabibliotheque.util.Constants.SHARED_PREF_NO_LOGIN;
import static com.aprouxdev.mabibliotheque.util.Constants.SHARED_USER_UID;

public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";


    protected SharedPreferences bPreferences;
    protected FirebaseAuth bAuth;
    protected FirebaseUser bUser;
    public String bUserUid;

    public boolean bIsUserPrefNoLogin;

    private LocalBookViewModel bookViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(this.getFragmentLayout());

        setupBaseData();
        if (!bIsUserPrefNoLogin) addLocalLibraryInFirestore();
    }



    // Abstract Methods
    public abstract int getFragmentLayout();

    private void setupBaseData(){
        bPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        bAuth = FirebaseAuth.getInstance();
        isUserSignIn();
        saveBasedVariablesInPreferences();
    }

    private void saveBasedVariablesInPreferences(){
        bPreferences.edit().putString(SHARED_USER_UID, bUserUid).apply();
    }

    /**
     * If user is not authentified and No_SIGN_PREF is not true
     *  Go to login Activity
     */
    private void isUserSignIn() {
        bUser = bAuth.getCurrentUser();
        bIsUserPrefNoLogin = bPreferences.getBoolean(SHARED_PREF_NO_LOGIN, false);
        if (bUser == null && !bIsUserPrefNoLogin){
            startActivity(new Intent(this, LoginActivity.class));
        }
        if (bUser != null){
            bUserUid = bUser.getUid();
        }
    }

    /**
     * Method call only once : check if is it first time called
     * Observer to local database and add all books to Firestore
     */
    private void addLocalLibraryInFirestore() {
        bookViewModel = ViewModelProviders.of(this).get(LocalBookViewModel.class);
        bookViewModel.getBooks().observe(this, new Observer<List<Book>>() {
            @Override
            public void onChanged(List<Book> books) {
                if (books != null && books.size() > 0) addBooksInFirestore(books);
            }
        });
    }

    /**
     * Recursive Method : add next book when previous added is complete until index reach library size
     * @param index : index of the current book added
     * @param books : library of books (from local database)
     */
    private void addBooksInFirestore(int index, List<Book> books) {
        LibraryHelper.addBook(bUserUid, books.get(index)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                int newIndex = index + 1;
                if (newIndex < books.size()) addBooksInFirestore(newIndex, books);
            }
        });
    }
    private void addBooksInFirestore(List<Book> books) {
        addBooksInFirestore(0, books);
    }

    // TOOLS

    protected String getTheString(int resource){
        return getResources().getString(resource);
    }

    // UI

    protected void setupActionBar() {
        ActionBar actionBar = this.getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    // --------------------
    // ERROR HANDLER
    // --------------------

    protected OnFailureListener onFailureListener(){
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), getString(R.string.toast_unknown_error), Toast.LENGTH_LONG).show();
            }
        };
    }
}
