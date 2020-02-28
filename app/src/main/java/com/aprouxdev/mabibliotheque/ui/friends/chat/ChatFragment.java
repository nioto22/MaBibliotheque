package com.aprouxdev.mabibliotheque.ui.friends.chat;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aprouxdev.mabibliotheque.R;
import com.aprouxdev.mabibliotheque.database.firestoreDatabase.DiscussionHelper;
import com.aprouxdev.mabibliotheque.database.firestoreDatabase.UserHelper;
import com.aprouxdev.mabibliotheque.models.Discussion;
import com.aprouxdev.mabibliotheque.models.User;
import com.aprouxdev.mabibliotheque.ui.friends.FriendsActivity;
import com.aprouxdev.mabibliotheque.ui.friends.chat.discussion.adapter.DiscussionsAdapter;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "ChatFragment";

    // UI Vars
    private TextView noDiscussion;
    private RecyclerView discussionsRecyclerView;
    private FloatingActionButton addDiscussionButton;
    // DATA
    private DiscussionsAdapter discussionsAdapter;
    private ChatViewModel mViewModel;
    private String bUserUid;
    private boolean isUserHaveFriend;
    private List<Discussion> userDiscussions;

    public static ChatFragment newInstance() {
        return new ChatFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ChatViewModel.class);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getBasedVars();
        setupViews(view);
        getAllUserDiscussionsInRecyclerView();
    }





    // ------------------------
    //         DATA
    // ------------------------


    private void getBasedVars() {
        FriendsActivity friendsActivity = (FriendsActivity) getActivity();
        if (friendsActivity != null){
            if (friendsActivity.bUserUid != null) bUserUid = friendsActivity.bUserUid;
        }
    }



    private void getAllUserDiscussionsInRecyclerView() {
        // Get the user and his discussions list
        UserHelper.getUser(bUserUid).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful() && task.getResult() != null){
                    User currentUser = task.getResult().toObject(User.class);
                    if (currentUser != null){
                        Log.d(TAG, "onComplete: Get current user : user email = " + currentUser.getEmail());
                        isUserHaveFriend = currentUser.getFriends().size() > 0;
                        final List<String> userDiscussions = currentUser.getDiscussions();
                        Log.d(TAG, "onComplete: user discussions string list size = " + userDiscussions.size());
                        if (userDiscussions.size() > 0) {
                            final List<Discussion> discussionsList = new ArrayList<>();
                            // getAllDiscussions
                            DiscussionHelper.getDiscussionsCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful() && task.getResult() != null){
                                        Log.d(TAG, "onComplete: get All discussions : number of total discussion = " + task.getResult().size());
                                        for(QueryDocumentSnapshot document : task.getResult()){
                                            final Discussion discussion = document.toObject(Discussion.class);
                                            if (userDiscussions.contains(discussion.getUid())){
                                                discussionsList.add(discussion);
                                            }
                                        }
                                        Log.d(TAG, "onComplete: get all user discussions : number = " + discussionsList.size());

                                        discussionsAdapter = new DiscussionsAdapter(discussionsList, Glide.with(ChatFragment.this));
                                        discussionsRecyclerView.setAdapter(discussionsAdapter);
                                        discussionsAdapter.notifyDataSetChanged();
                                        Log.d(TAG, "onComplete: Recycler view updated");
                                        discussionsRecyclerView.setVisibility(View.VISIBLE);
                                        noDiscussion.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    // ------------------------
    //          UI
    // ------------------------
//    private void setupRecyclerView(String bUserUid) {
//        this.bUserUid = bUserUid;
//
//        this.discussionsAdapter = new DiscussionsAdapter(userDiscussions, Glide.with(this));
//    }


    // ------------------------
    //     CLICKS AND NAV
    // ------------------------
    @Override
    public void onClick(View v) {
        if (v.getId() == (R.id.chatFragmentAddDiscussionFloatingButton)){
            // check if user have no friend
            Log.d(TAG, "onClick: isUserHaveFriend : " + isUserHaveFriend);
            // alertDialog No friend add a friend or cancel
            // else addDiscussionActivity
        }
    }


    // ---------------------
    //     SETUP VIEWS
    // ---------------------
    private void setupViews(View view) {
        noDiscussion = view.findViewById(R.id.chatFragmentNoDiscussionTV);
        discussionsRecyclerView = view.findViewById(R.id.chatFragmentDiscussionRecyclerView);
        addDiscussionButton = view.findViewById(R.id.chatFragmentAddDiscussionFloatingButton);
        addDiscussionButton.setOnClickListener(this);
    }
}
