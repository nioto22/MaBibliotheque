package com.aprouxdev.mabibliotheque.ui.friends.chat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aprouxdev.mabibliotheque.R;
import com.aprouxdev.mabibliotheque.database.firestoreDatabase.DiscussionHelper;
import com.aprouxdev.mabibliotheque.models.Discussion;
import com.aprouxdev.mabibliotheque.ui.adapters.DiscussionsAdapter;
import com.aprouxdev.mabibliotheque.ui.friends.FriendsActivity;
import com.aprouxdev.mabibliotheque.ui.friends.chat.addDiscussion.AddDiscussionActivity;
import com.aprouxdev.mabibliotheque.ui.friends.chat.discussion.DiscussionActivity;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import static com.aprouxdev.mabibliotheque.util.Constants.BUNDLE_EXTRA_DISCUSSION_UID;

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
        //getAllUserDiscussionsInRecyclerView();
        subscribesListeners();
    }

    private void subscribesListeners() {
        DiscussionHelper.getAllDiscussionsForUser(bUserUid).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                getAllUserDiscussionsInRecyclerView();
            }
        });
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
        final List<Discussion> discussionsList = new ArrayList<>();
        DiscussionHelper.getAllDiscussionsForUser(bUserUid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null){
                    Log.d(TAG, "onComplete: get All discussions : number of total discussion = " + task.getResult().size());
                    for(QueryDocumentSnapshot document : task.getResult()){
                        final Discussion discussion = document.toObject(Discussion.class);
                        discussionsList.add(discussion);
                    }
                    Log.d(TAG, "onComplete: get all user discussions : number = " + discussionsList.size());
                    setupRecyclerView();
                }
            }
            private void setupRecyclerView() {
                discussionsAdapter = new DiscussionsAdapter(discussionsList, Glide.with(ChatFragment.this));
                discussionsRecyclerView.setAdapter(discussionsAdapter);
                discussionsAdapter.notifyDataSetChanged();
                Log.d(TAG, "onComplete: Recycler view updated");
                discussionsRecyclerView.setVisibility(View.VISIBLE);
                noDiscussion.setVisibility(View.GONE);

                discussionsAdapter.setOnItemClickListener(new DiscussionsAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        String discussionUid = discussionsList.get(position).getUid();
                        Intent intent = new Intent(getActivity(), DiscussionActivity.class);
                        intent.putExtra(BUNDLE_EXTRA_DISCUSSION_UID, discussionUid);
                        startActivity(intent);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), getResources().getString(R.string.toast_unknown_error), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailure: Fail to get all discussion for user", e);
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
            startActivity(new Intent(getActivity(), AddDiscussionActivity.class));
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
