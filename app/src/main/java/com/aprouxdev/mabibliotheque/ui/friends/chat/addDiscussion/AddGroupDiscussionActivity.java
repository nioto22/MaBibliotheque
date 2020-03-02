package com.aprouxdev.mabibliotheque.ui.friends.chat.addDiscussion;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.aprouxdev.mabibliotheque.R;
import com.aprouxdev.mabibliotheque.database.firestoreDatabase.UserHelper;
import com.aprouxdev.mabibliotheque.models.User;
import com.aprouxdev.mabibliotheque.ui.base.BaseActivity;
import com.aprouxdev.mabibliotheque.ui.friends.friends.FriendsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentManager = getSupportFragmentManager();

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
            // TODO Fragment new group discussion
        }
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
        // TODO select fragment Friends or New Group
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
