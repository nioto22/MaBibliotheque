package com.aprouxdev.mabibliotheque.ui.friends.chat.discussion;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aprouxdev.mabibliotheque.R;
import com.aprouxdev.mabibliotheque.database.firestoreDatabase.DiscussionHelper;
import com.aprouxdev.mabibliotheque.database.firestoreDatabase.MessageHelper;
import com.aprouxdev.mabibliotheque.database.firestoreDatabase.UserHelper;
import com.aprouxdev.mabibliotheque.models.Message;
import com.aprouxdev.mabibliotheque.models.User;
import com.aprouxdev.mabibliotheque.tools.general.Tools;
import com.aprouxdev.mabibliotheque.ui.base.BaseActivity;
import com.aprouxdev.mabibliotheque.ui.adapters.MessagesAdapter;
import com.aprouxdev.mabibliotheque.util.Constants;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import static com.aprouxdev.mabibliotheque.util.Constants.BUNDLE_EXTRA_DISCUSSION_UID;
import static com.aprouxdev.mabibliotheque.util.Constants.RC_CHOOSE_BOOK;

public class DiscussionActivity extends BaseActivity implements MessagesAdapter.Listener, View.OnClickListener {

    // UI vars
    RecyclerView recyclerView;
    TextView textViewRecyclerViewEmpty;
    TextInputEditText editTextMessage;
    ImageView bookImagePreview;
    ImageButton addBookButton;
    Button sendButton;

    // FOR DATA
    private MessagesAdapter messagesAdapter;
    @Nullable
    private User modelCurrentUser;
    private String discussionUid;
    private String bookSelectedImage;
    private String bookSelectedId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setupViews();
        this.setupActionBar();
        this.getDiscussionUid();
        this.getCurrentUserFromFirestore();
        this.configureRecyclerView(discussionUid);

    }

    @Override
    public int getFragmentLayout() {
        return (R.layout.activity_discussion);
    }


    // --------------------
    //       DATA
    // --------------------

    private void getCurrentUserFromFirestore(){
        UserHelper.getUser(bUserUid).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                modelCurrentUser = documentSnapshot.toObject(User.class);
            }
        });
    }

    private void getDiscussionUid() {
        Intent intent = getIntent();
        if (intent != null){
            discussionUid = intent.getStringExtra(BUNDLE_EXTRA_DISCUSSION_UID);
            // TODO if discussionUid == null AlertDialog Error happen
        }
    }

    // --------------------
    // ACTIONS
    // --------------------

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case (R.id.activity_discussion_send_button):
                this.onClickSendMessage();
                break;
            case(R.id.activity_discussion_add_book_button):
                this.onClickAddBook();
                break;
        }
    }

    public void onClickSendMessage() {
        // Check if text field is not empty and current user properly downloaded from Firestore
        if (!TextUtils.isEmpty(editTextMessage.getText()) && bUser != null){
            String messageUid = Tools.createNewId();
            String message = editTextMessage.getText().toString();
            // Check if it's a message with or w/o book
            if(bookSelectedImage == null) {
                MessageHelper.createMessageForDiscussion(messageUid, message, this.discussionUid, modelCurrentUser)
                        .addOnFailureListener(this.onFailureListener());
            } else {
                MessageHelper.createMessageForDiscussion(messageUid, message, this.bookSelectedId, this.bookSelectedImage, this.discussionUid, modelCurrentUser)
                        .addOnFailureListener(this.onFailureListener());
            }
            // Reset text field
            this.editTextMessage.setText("");
        }
    }



    public void onClickAddBook() {
        Intent intent = new Intent(DiscussionActivity.this, SelectBookActivity.class);
        startActivityForResult(intent, RC_CHOOSE_BOOK);
    }



    // --------------------
    // UI
    // --------------------

    private void configureRecyclerView(String discussionUid){
        //Track current chat name
        this.discussionUid = discussionUid;

        //Configure Adapter & RecyclerView
        this.messagesAdapter = new MessagesAdapter(generateOptionsForAdapter(DiscussionHelper.getAllMessagesForDiscussion(this.discussionUid)),
                Glide.with(this),
                this,
                this.bUserUid);
        messagesAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.smoothScrollToPosition(messagesAdapter.getItemCount()); // Scroll to bottom on new messages
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(this.messagesAdapter);
    }

    private FirestoreRecyclerOptions<Message> generateOptionsForAdapter(Query query){
        return new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .setLifecycleOwner(this)
                .build();
    }

    // --------------------
    // ADD BOOK METHOD
    // --------------------

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 6 - Calling the appropriate method after activity result
        this.handleResponse(requestCode, resultCode, data);
    }


    private void handleResponse(int requestCode, int resultCode, Intent data){
        if (requestCode == RC_CHOOSE_BOOK) {
            if (resultCode == RESULT_OK) { //SUCCESS
                this.bookSelectedId = data.getStringExtra(Constants.BUNDLE_EXTRA_SELECTED_BOOK_ID);
                this.bookSelectedImage = data.getStringExtra(Constants.BUNDLE_EXTRA_SELECTED_BOOK_IMAGE);
                Glide.with(this) //SHOWING PREVIEW OF IMAGE
                        .load(this.bookSelectedImage)
                        .apply(RequestOptions.circleCropTransform())
                        .into(this.bookImagePreview);
            } else {
                Toast.makeText(this, getString(R.string.toast_title_no_book_chosen), Toast.LENGTH_SHORT).show();
            }
        }
    }




    // --------------------
    // CALLBACK
    // --------------------

    @Override
    public void onDataChanged() {
        textViewRecyclerViewEmpty.setVisibility(this.messagesAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    // --------------------
    //    SETUP VIEWS
    // --------------------

    private void setupViews() {
        recyclerView = findViewById(R.id.activity_discussion_recycler_view);
        textViewRecyclerViewEmpty = findViewById(R.id.activity_discussion_text_view_recycler_view_empty);
        editTextMessage = findViewById(R.id.activity_discussion_message_edit_text);
        bookImagePreview = findViewById(R.id.activity_discussion_image_chosen_preview);
        addBookButton = findViewById(R.id.activity_discussion_add_book_button);
        addBookButton.setOnClickListener(this);
        sendButton = findViewById(R.id.activity_discussion_send_button);
        sendButton.setOnClickListener(this);
    }

}


