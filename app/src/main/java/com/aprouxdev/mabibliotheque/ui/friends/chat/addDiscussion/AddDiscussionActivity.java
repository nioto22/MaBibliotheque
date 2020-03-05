package com.aprouxdev.mabibliotheque.ui.friends.chat.addDiscussion;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.aprouxdev.mabibliotheque.R;
import com.aprouxdev.mabibliotheque.database.firestoreDatabase.SocialUserHelper;
import com.aprouxdev.mabibliotheque.database.firestoreDatabase.UserHelper;
import com.aprouxdev.mabibliotheque.models.SocialUser;
import com.aprouxdev.mabibliotheque.models.User;
import com.aprouxdev.mabibliotheque.ui.base.BaseActivity;
import com.aprouxdev.mabibliotheque.ui.friends.friends.FriendsFragment;
import com.aprouxdev.mabibliotheque.ui.friends.friends.addFriend.AddFriendActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class AddDiscussionActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "AddDiscussionActivity";
    // UI Vars
    // Root containers
    private RelativeLayout noFriendRootContainer;
    private RelativeLayout addDiscussionRootContainer;
    private ProgressBar progressBar;
    // No friends layout
    private Button noFriendsButton;
    // Add Discussion Layout
    // New Group
    private RelativeLayout newGroupButton;

    // Data Vars
    private enum State {
        LOADING,
        NO_FRIEND,
        ADD_DISCUSSION,
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
        return (R.layout.activity_add_discussion);
    }

    // ------------------------
    //        DATA
    // ------------------------

    private void subscribeListeners() {
        SocialUserHelper.getSocialUser(bUserUid).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null){
                    SocialUser currentUser = task.getResult().toObject(SocialUser.class);
                    if (currentUser != null){
                        List<String> userFriends = currentUser.getFriends();
                        if (userFriends != null && userFriends.size() > 0){
                            Log.d(TAG, "onComplete: Add discussion fragment with friends");
                            setupVisibilities(State.ADD_DISCUSSION);
                            setupFragment();
                        } else {
                            Log.d(TAG, "onComplete: no user friends");
                            setupVisibilities(State.NO_FRIEND);
                        }
                    } else {
                        Log.d(TAG, "onComplete: error state : no current user");
                        setupVisibilities(State.ERROR);
                    }
                } else {
                    Log.d(TAG, "onComplete: error state : get user failed");
                    setupVisibilities(State.ERROR);
                }
            }
        });
    }



    // ------------------------
    //     CLICK AND NAV
    // ------------------------
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case (R.id.addDiscussionNoFriendsButton):
                startActivity(new Intent(AddDiscussionActivity.this, AddFriendActivity.class));
                break;
            case (R.id.addDiscussionGroupDiscussionNextButton):
                goToGroupDiscussion();
                break;
        }
    }


    private void goToGroupDiscussion(){
        startActivity(new Intent(AddDiscussionActivity.this, AddGroupDiscussionActivity.class));
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }
    // ------------------------
    //         UI
    // ------------------------

    private void setupVisibilities(State state) {
        switch (state){
            case LOADING :{
                noFriendRootContainer.setVisibility(View.GONE);
                addDiscussionRootContainer.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                break;
            }
            case NO_FRIEND:{
                noFriendRootContainer.setVisibility(View.VISIBLE);
                addDiscussionRootContainer.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                break;
            }
            case ADD_DISCUSSION:{
                noFriendRootContainer.setVisibility(View.GONE);
                addDiscussionRootContainer.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                break;
            }
            case ERROR:{
                noFriendRootContainer.setVisibility(View.GONE);
                addDiscussionRootContainer.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                Toast.makeText(this, getTheString(R.string.toast_unknown_error), Toast.LENGTH_SHORT).show();
                onBackPressed();
                break;
            }
        }
    }

    private void setupFragment() {
        fragmentTransaction = fragmentManager.beginTransaction();
        FriendsFragment friendsFragment = new FriendsFragment();
        fragmentTransaction.add(R.id.addDiscussionFriendsFragmentLayout, friendsFragment);
        fragmentTransaction.commit();

    }
    // ------------------------
    //     SETUP VIEWS
    // ------------------------

    private void setupViews() {
        noFriendRootContainer = findViewById(R.id.addDiscussionNoFriendsLayout);
        addDiscussionRootContainer = findViewById(R.id.addDiscussionNewDiscussionLayout);
        progressBar = findViewById(R.id.addDiscussionProgressBar);
        noFriendsButton = findViewById(R.id.addDiscussionNoFriendsButton);
        noFriendsButton.setOnClickListener(this);
        newGroupButton = findViewById(R.id.addDiscussionNewGroupContainerButton);
    }


}
