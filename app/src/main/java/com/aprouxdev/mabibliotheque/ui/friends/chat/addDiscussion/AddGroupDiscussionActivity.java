package com.aprouxdev.mabibliotheque.ui.friends.chat.addDiscussion;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.aprouxdev.mabibliotheque.R;
import com.aprouxdev.mabibliotheque.database.firestoreDatabase.UserHelper;
import com.aprouxdev.mabibliotheque.models.User;
import com.aprouxdev.mabibliotheque.ui.base.BaseActivity;
import com.aprouxdev.mabibliotheque.ui.dialogFragments.FriendDetailPopup;
import com.aprouxdev.mabibliotheque.ui.dialogFragments.SaveGroupDiscussionPopup;
import com.aprouxdev.mabibliotheque.ui.friends.friends.FriendsFragment;
import com.aprouxdev.mabibliotheque.util.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import static com.aprouxdev.mabibliotheque.util.Constants.SHARED_PREF_NAME;
import static com.aprouxdev.mabibliotheque.util.Constants.SHARED_SELECTED_USERS_OF_DISCUSSION;

public class AddGroupDiscussionActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "AddGroupDiscussionActiv";

    // UI Vars
    // Root containers
    private RelativeLayout addGroupDiscussionRootContainer;
    private ProgressBar progressBar;
    // Add Discussion Layout
    private FloatingActionButton groupDiscussionNextButton;



    // Data Vars
    private enum State {
        LOADING,
        ADD_GROUP_DISCUSSION,
        ERROR
    }
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentManager = getSupportFragmentManager();
        preferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        setupViews();
        setupActionBar();
        setupVisibilities(State.LOADING);
        subscribeListeners();
    }

    @Override
    public int getFragmentLayout() {
        return (R.layout.activity_add_group_discussion);
    }

    // ------------------------
    //        DATA
    // ------------------------

    private void subscribeListeners() {
        UserHelper.getUser(bUserUid).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null){
                    User currentUser = task.getResult().toObject(User.class);
                    if (currentUser != null){
                        List<String> userFriends = currentUser.getFriends();
                        if (userFriends != null && userFriends.size() > 0){
                            setupVisibilities(State.ADD_GROUP_DISCUSSION);
                            setupFragment();
                        }
                    } else {
                        setupVisibilities(State.ERROR);
                    }
                } else {
                    setupVisibilities(State.ERROR);
                }
            }
        });
    }




    // ----------------------------
    //     ACTION AND NAVIGATION
    // ----------------------------
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.addGroupDiscussionGroupDiscussionNextButton) {
            // list of friends from pref
            Set<String> discussionUsers = preferences.getStringSet(SHARED_SELECTED_USERS_OF_DISCUSSION, null);
            if(discussionUsers != null){
                ArrayList<String> users = new ArrayList<>(discussionUsers);
                showCreateDiscussionPopup(users);
            }

        }
    }

    private void showCreateDiscussionPopup(ArrayList<String> users) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(Constants.DISCUSSION_SAVE_POPUP_TAG);
        if (prev != null) { ft.remove(prev);}
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = SaveGroupDiscussionPopup.newInstance(bUserUid, users);
        newFragment.show(ft, Constants.DISCUSSION_SAVE_POPUP_TAG);
    }

    // ------------------------
    //         UI
    // ------------------------

    @SuppressLint("RestrictedApi")
    private void setupVisibilities(State state) {
        switch (state){
            case LOADING :{
                addGroupDiscussionRootContainer.setVisibility(View.GONE);
                groupDiscussionNextButton.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                break;
            }
            case ADD_GROUP_DISCUSSION:{
                addGroupDiscussionRootContainer.setVisibility(View.VISIBLE);
                groupDiscussionNextButton.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                break;
            }
            case ERROR:{
                addGroupDiscussionRootContainer.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, getTheString(R.string.toast_unknown_error), Toast.LENGTH_SHORT).show();
                onBackPressed();
                break;
            }
        }
    }
    private void setupFragment() {
        fragmentTransaction = fragmentManager.beginTransaction();
        FriendsFragment friendsFragment = new FriendsFragment();
        fragmentTransaction.add(R.id.addGroupDiscussionFriendsFragmentLayout, friendsFragment);
        fragmentTransaction.commit();

    }


    // ------------------------
    //     SETUP VIEWS
    // ------------------------

    private void setupViews() {
        addGroupDiscussionRootContainer = findViewById(R.id.addGroupDiscussionNewDiscussionLayout);
        progressBar = findViewById(R.id.addGroupDiscussionProgressBar);
        groupDiscussionNextButton = findViewById(R.id.addGroupDiscussionGroupDiscussionNextButton);
        groupDiscussionNextButton.setOnClickListener(this);
    }

}
