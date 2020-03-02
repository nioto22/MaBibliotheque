package com.aprouxdev.mabibliotheque.ui.friends.friends;

import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
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
import android.widget.Toast;

import com.aprouxdev.mabibliotheque.R;
import com.aprouxdev.mabibliotheque.database.firestoreDatabase.DiscussionHelper;
import com.aprouxdev.mabibliotheque.database.firestoreDatabase.UserHelper;
import com.aprouxdev.mabibliotheque.models.Discussion;
import com.aprouxdev.mabibliotheque.models.User;
import com.aprouxdev.mabibliotheque.tools.general.Tools;
import com.aprouxdev.mabibliotheque.ui.adapters.FriendAdapter;
import com.aprouxdev.mabibliotheque.ui.base.BaseActivity;
import com.aprouxdev.mabibliotheque.ui.adapters.DiscussionsAdapter;
import com.aprouxdev.mabibliotheque.ui.friends.FriendsActivity;
import com.aprouxdev.mabibliotheque.ui.friends.chat.addDiscussion.AddDiscussionActivity;
import com.aprouxdev.mabibliotheque.ui.friends.chat.addDiscussion.AddGroupDiscussionActivity;
import com.aprouxdev.mabibliotheque.ui.friends.chat.discussion.DiscussionActivity;
import com.aprouxdev.mabibliotheque.ui.friends.friends.addFriend.AddFriendActivity;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.aprouxdev.mabibliotheque.util.Constants.BUNDLE_EXTRA_DISCUSSION_UID;

public class FriendsFragment extends Fragment {
    private static final String TAG = "FriendsFragment";

    // UI Vars
    private TextView noFriendTextView;
    private RecyclerView friendsRecyclerView;
    private FloatingActionButton addFriendButton;

    // Data
    private FriendAdapter friendsAdapter;
    private FriendsViewModel mViewModel;
    private String bUserUid;


    public static FriendsFragment newInstance() { return new FriendsFragment(); }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(FriendsViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getBasedVars();
        setupViews(view);
        setupClickListener();
        subscribesListeners();
    }


    // ------------------------
    //         DATA
    // ------------------------
    private void getBasedVars() {
        BaseActivity parentActivity = (BaseActivity) getActivity();
        if (parentActivity != null){
            if (parentActivity.bUserUid != null) bUserUid = parentActivity.bUserUid;
        }
    }

    // ------------------------
    //         LISTENERS
    // ------------------------

    private void subscribesListeners() {
        UserHelper.getAllDiscussionsForUser(bUserUid).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                getAllUserFriendsInRecyclerView();
            }
        });
    }

    private void getAllUserFriendsInRecyclerView() {
        // Get the user and his friendsUid list
        UserHelper.getUser(bUserUid).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful() && task.getResult() != null){
                    User currentUser = task.getResult().toObject(User.class);
                    if (currentUser != null){
                        final List<String> userFriends = currentUser.getFriends();
                        Log.d(TAG, "onComplete: user friends string list size = " + userFriends.size());
                        if (userFriends.size() > 0) {
                            getAllUserFriends(userFriends);
                        } else {
                            setupViewsVisibility(false);
                        }
                    }
                }
            }

            private void getAllUserFriends(List<String> userFriends) {
                final List<User> friendsList = new ArrayList<>();
                final List<Boolean> friendsSelectedList = new ArrayList<>();
                UserHelper.getUsersCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null){
                            Log.d(TAG, "onComplete: get All friends : number of total discussion = " + task.getResult().size());
                            for(QueryDocumentSnapshot document : task.getResult()){
                                final User user = document.toObject(User.class);
                                if (userFriends.contains(user.getUid())){
                                    friendsList.add(user);
                                    friendsSelectedList.add(false);
                                }
                            }
                            Log.d(TAG, "onComplete: get all user friends : number = " + friendsList.size());
                            setupRecyclerView();
                        }
                    }
                    private void setupRecyclerView() {
                        Class parentActivityClass = Objects.requireNonNull(getActivity()).getClass();
                        if(parentActivityClass == AddGroupDiscussionActivity.class){
                            friendsAdapter = new FriendAdapter(friendsList, friendsSelectedList, Glide.with(FriendsFragment.this));
                        } else {
                            friendsAdapter = new FriendAdapter(friendsList, Glide.with(FriendsFragment.this));
                        }

                        friendsRecyclerView.setAdapter(friendsAdapter);
                        friendsAdapter.notifyDataSetChanged();
                        Log.d(TAG, "onComplete: Recycler view updated");
                        setupViewsVisibility(true);

                        friendsAdapter.setOnItemClickListener(new FriendAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                User friend = friendsAdapter.getFriend(position);
                                // Check if it's FriendsActivity or AddDiscussionActivity or AddGroupDiscussionActivity
                                if (parentActivityClass == AddGroupDiscussionActivity.class){

                                    friendClickOnAddGroupDiscussionActivity(position);

                                } else if (parentActivityClass == AddDiscussionActivity.class){

                                    friendClickOnAddDiscussionActivity(friend);

                                } else {
                                    friendClickOnFriendsActivity(friend);
                                }
                            }

                            /**
                             * FriendsActivity : Friend clicked action
                             * @param friend Friend clicked
                             */
                            private void friendClickOnFriendsActivity(User friend) {
                                // TODO Popup friend details
                            }


                            /**
                             * AddGroupDiscussionActivity : Friend clicked action :
                             *
                             * Update friendsSelectedList
                             * UpdateAdapters and ViewHolder as selected
                             * @param position Int friendClicked Position
                             */
                            private void friendClickOnAddGroupDiscussionActivity(int position) {
                                friendsSelectedList.set(position, true);
                                friendsAdapter.notifyDataSetChanged();
                            }

                            /**
                             * AddDiscussionActivity Friend clicked action :
                             *
                             *  Check if these users have already a discussion
                             *  Then Open the discussion or create it
                             * @param friend Friend clicked
                             */
                            private void friendClickOnAddDiscussionActivity(User friend) {
                                // Check if these users have already a discussion
                                    // Then Open the discussion or create it
                                String friendUid = friend.getUid();
                                DiscussionHelper.getAllDiscussionsWithTheseOnlyTwoUsers(bUserUid, friendUid)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                // Open the discussion
                                                if (task.isSuccessful() && task.getResult() != null){
                                                    Log.d(TAG, "onComplete: get discussion with selected users" );
                                                    List<Discussion> discussions = task.getResult().toObjects(Discussion.class);
                                                    Discussion discussion = discussions.get(0);
                                                    if (discussions.get(0) != null) goToDiscussionActivity(discussion.getUid());
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Create
                                        createNewDiscussion();
                                    }

                                    private void createNewDiscussion() {
                                        String newDiscussionUid = Tools.createNewId();
                                        String discussionName = friend.getUsername();
                                        List<String> usersListUid = Arrays.asList(bUserUid, friendUid);
                                        DiscussionHelper.createDiscussion(newDiscussionUid, discussionName, usersListUid)
                                                .addOnCompleteListener(task -> {
                                                    if (task.isSuccessful() && task.getResult() != null){
                                                        Log.d(TAG, "onComplete: new discussion created" );
                                                        goToDiscussionActivity(newDiscussionUid);
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e(TAG, "onFailure: Fail to create new Discussion", e);
                                                Toast.makeText(getActivity(), getResources().getString(R.string.toast_unknown_error), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    private void setupViewsVisibility(boolean userHaveFriend) {
        if (userHaveFriend) {
            noFriendTextView.setVisibility(View.GONE);
            friendsRecyclerView.setVisibility(View.VISIBLE);
        } else {
            noFriendTextView.setVisibility(View.VISIBLE);
            friendsRecyclerView.setVisibility(View.GONE);
        }
    }

    private void goToDiscussionActivity(String uid) {
        Intent intent = new Intent(getActivity(), DiscussionActivity.class);
        intent.putExtra(BUNDLE_EXTRA_DISCUSSION_UID, uid);
        startActivity(intent);
    }

    private void setupClickListener() {
        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddFriendActivity.class));
            }
        });
    }

    private void setupViews(View view) {
        noFriendTextView = view.findViewById(R.id.friendsFragmentNoFriendsTV);
        friendsRecyclerView = view.findViewById(R.id.friendsFragmentFriendsRecyclerView);
        friendsRecyclerView.setVisibility(View.GONE);
        addFriendButton = view.findViewById(R.id.friendsFragmentAddFriendButton);
    }

}
