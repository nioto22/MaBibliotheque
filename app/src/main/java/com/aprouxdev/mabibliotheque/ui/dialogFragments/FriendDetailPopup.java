package com.aprouxdev.mabibliotheque.ui.dialogFragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aprouxdev.mabibliotheque.R;
import com.aprouxdev.mabibliotheque.database.firestoreDatabase.DiscussionHelper;
import com.aprouxdev.mabibliotheque.database.firestoreDatabase.LibraryHelper;
import com.aprouxdev.mabibliotheque.models.Book;
import com.aprouxdev.mabibliotheque.models.Discussion;
import com.aprouxdev.mabibliotheque.models.User;
import com.aprouxdev.mabibliotheque.tools.general.Tools;
import com.aprouxdev.mabibliotheque.ui.friends.chat.discussion.DiscussionActivity;
import com.aprouxdev.mabibliotheque.util.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import static com.aprouxdev.mabibliotheque.util.Constants.BUNDLE_EXTRA_DISCUSSION_UID;

public class FriendDetailPopup extends DialogFragment implements View.OnClickListener {
    private static final String TAG = "FriendDetailPopup";

    // UI Vars
    private TextView friendUsername;
    private ImageView friendImage;
    private TextView numberOfBooks;
    private TextView numberOfFavorite;
    private Button sendMessageButton;
    private Button seeLibraryButton;
    private ProgressBar progressBar;
    // DATA
    private String userUid;
    private User friend;
    private Context context;

    public static FriendDetailPopup newInstance(String userUid, User friend) {
        FriendDetailPopup friendDetailPopup = new FriendDetailPopup();

        Bundle args = new Bundle();
        args.putString(Constants.FRIEND_DETAILS_POPUP_BUNDLE_USER_UID, userUid);
        args.putSerializable(Constants.FRIEND_DETAILS_POPUP_BUNDLE_FRIEND, friend);
        friendDetailPopup.setArguments(args);

        return friendDetailPopup;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();

        getPopupArguments();

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.popup_friend_details, container, false);
        setupViews(view);
        setupDataOnViews();
        return view;
    }


    public void dismissDialog(){
        this.dismiss();
    }

    //----------------------------
    //          DATA
    //----------------------------

    private void getPopupArguments() {
        if (getArguments() != null){
            userUid = getArguments().getString(Constants.FRIEND_DETAILS_POPUP_BUNDLE_USER_UID);
            friend = (User) getArguments().getSerializable(Constants.FRIEND_DETAILS_POPUP_BUNDLE_FRIEND);
            if(friend == null) {
                Toast.makeText(context, getResources().getString(R.string.toast_unknown_error), Toast.LENGTH_SHORT).show();
                dismissDialog();
            }
        }
    }

    //----------------------------
    //         UI
    //----------------------------
    private void setupViews(View view) {
        friendUsername = view.findViewById(R.id.popupFriendDetailsUsername);
        friendImage = view.findViewById(R.id.popupFriendDetailsImage);
        numberOfBooks = view.findViewById(R.id.popupFriendDetailsBooksNumber);
        numberOfFavorite = view.findViewById(R.id.popupFriendDetailsFavoriteNumber);
        sendMessageButton = view.findViewById(R.id.popupSaveDiscussionCreateButton);
        sendMessageButton.setOnClickListener(this);
        seeLibraryButton = view.findViewById(R.id.popupSaveDiscussionCancelButton);
        seeLibraryButton.setOnClickListener(this);
        progressBar = view.findViewById(R.id.popupFriendProgressBar);
    }

    private void setupDataOnViews() {
        friendUsername.setText(friend.getUsername());
        Picasso.get()
                .load(friend.getUrlPicture())
                .placeholder(R.drawable.ic_user_no_image)
                .into(friendImage);
        LibraryHelper.getAllBooks(friend.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null){
                    List<Book> friendLibrary = task.getResult().toObjects(Book.class);
                    if (friendLibrary.size() > 0){
                        numberOfBooks.setText(friendLibrary.size());
                        int favorites = 0;
                        for (Book book : friendLibrary) {
                           if (book.getMark() == 5) favorites ++;
                        }
                        numberOfFavorite.setText(favorites);
                    } else
                        numberOfBooks.setText("0");
                        numberOfFavorite.setText("0");
                }
            }
        });
    }

    private void showProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
    }
    private void hideProgressBar(){
        progressBar.setVisibility(View.GONE);
    }

    //----------------------------
    //   ACTION AND NAVIGATION
    //----------------------------

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case (R.id.popupSaveDiscussionCreateButton):
                openDiscussion(userUid, friend.getUid());
                break;
            case (R.id.popupSaveDiscussionCancelButton):
                // TODO FriendLibraryActivity
                Toast.makeText(context, "Non disponible pour le moment", Toast.LENGTH_SHORT).show();
                break;

        }
    }

    private void openDiscussion(String userUid, String friendUid) {
        showProgressBar();
        DiscussionHelper.getAllDiscussionsWithTheseOnlyTwoUsers(userUid, friendUid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null && task.getResult().size() > 0){
                            Discussion discussion = task.getResult().toObjects(Discussion.class).get(0);
                            hideProgressBar();
                            openDiscussionActivity(discussion.getUid());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String discussionUid = Tools.createNewId();
                        List<String> discussionUsers = Arrays.asList(userUid, friendUid);
                        DiscussionHelper.createDiscussion(discussionUid, null, discussionUsers)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        hideProgressBar();
                                        openDiscussionActivity(discussionUid);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        hideProgressBar();
                                        Toast.makeText(context, getResources().getString(R.string.toast_unknown_error), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
    }
    private void openDiscussionActivity(String discussionUid) {
        Intent intent = new Intent(context, DiscussionActivity.class);
        intent.putExtra(BUNDLE_EXTRA_DISCUSSION_UID, discussionUid);
        hideProgressBar();
        startActivity(intent);
    }
}
