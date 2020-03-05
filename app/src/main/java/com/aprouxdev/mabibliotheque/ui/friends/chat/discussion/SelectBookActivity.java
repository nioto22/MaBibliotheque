package com.aprouxdev.mabibliotheque.ui.friends.chat.discussion;

import android.os.Bundle;
import android.view.MenuItem;

import com.aprouxdev.mabibliotheque.R;
import com.aprouxdev.mabibliotheque.ui.base.BaseActivity;
import com.aprouxdev.mabibliotheque.ui.friends.home.FriendsHomeFragment;
import com.aprouxdev.mabibliotheque.ui.main.library.LibraryFragment;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class SelectBookActivity extends BaseActivity {


    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupActionBar();
        setupFragment();
    }

    @Override
    public int getFragmentLayout() {
        return (R.layout.activity_select_book);
    }

    private void setupFragment() {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        LibraryFragment libraryFragment = new LibraryFragment();
        fragmentTransaction.add(R.id.selectBookLibraryFragment, libraryFragment);
        fragmentTransaction.commit();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }
}
