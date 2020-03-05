package com.aprouxdev.mabibliotheque.ui.friends.friends.addFriend;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aprouxdev.mabibliotheque.R;
import com.aprouxdev.mabibliotheque.database.firestoreDatabase.UserHelper;
import com.aprouxdev.mabibliotheque.models.User;
import com.aprouxdev.mabibliotheque.tools.general.Tools;
import com.aprouxdev.mabibliotheque.ui.base.BaseActivity;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

public class AddFriendActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "AddFriendActivity";

    // UI Vars
    private EditText searchFriendEditText; //addFriendEditText
    private RelativeLayout editTextStroke; // addFriendEditTextContainer
    private ImageButton searchButton; //addFriendSearchButton
    private ConstraintLayout resultLayout; // addFriendResultLayout
    private ProgressBar progressBar; // addFriendProgressBar
    private TextView noUserFindText; // addFriendNoUserFindText
    private ImageView friendImage; // addFriendFriendImage
    private TextView friendUsername; // addFriendUsername
    private Button connectingButton; // addFriendConnectingButton

    //DATA
    private enum State{
        STARTING,
        LOADING,
        NO_USER_FIND,
        FRIEND_FIND
    }
    private enum EditTextState{
        NO_TEXT,
        WRONG_FORMAT,
        GOOD_FORMAT
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupActionBar();
        setupViews();
        setupVisibilities(State.STARTING);
    }

    @Override
    public int getFragmentLayout() {
        return (R.layout.activity_add_friend);
    }

    // ------------------------
    //         DATA
    // ------------------------
    private void searchFriend(String searchText) {
        UserHelper.getUserByMail(searchText)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null && task.getResult().size() > 0){
                            User friend = task.getResult().toObjects(User.class).get(0);
                            if (friend != null){
                                setupFriendViews(friend);
                                setupVisibilities(State.FRIEND_FIND);
                            } else {
                                setupVisibilities(State.NO_USER_FIND);
                            }
                        } else {
                            setupVisibilities(State.NO_USER_FIND);
                        }
                    }
                })
                .addOnFailureListener(this.onFailureListener());
    }


    // ------------------------
    //         UI
    // ------------------------

    private void setupFriendViews(User friend) {
        Glide.with(this)
        .load(friend.getUrlPicture())
        .placeholder(R.drawable.ic_user_no_image)
        .into(friendImage);

        friendUsername.setText(friend.getUsername());
    }


    private void setupVisibilities(State state){
        switch (state){
            case STARTING:
                resultLayout.setVisibility(View.GONE);
                break;
            case LOADING:
                resultLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                noUserFindText.setVisibility(View.GONE);
                friendImage.setVisibility(View.GONE);
                friendUsername.setVisibility(View.GONE);
                connectingButton.setVisibility(View.GONE);
                break;
            case NO_USER_FIND:
                resultLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                noUserFindText.setVisibility(View.VISIBLE);
                friendImage.setVisibility(View.GONE);
                friendUsername.setVisibility(View.GONE);
                connectingButton.setVisibility(View.GONE);
                break;
            case FRIEND_FIND:
                resultLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                noUserFindText.setVisibility(View.GONE);
                friendImage.setVisibility(View.VISIBLE);
                friendUsername.setVisibility(View.VISIBLE);
                connectingButton.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void setupEditText(EditTextState state){
        switch (state){
            case NO_TEXT:
                editTextStroke.setBackgroundColor(getResources().getColor(R.color.background_third_taupe));
                break;
            case WRONG_FORMAT:
                editTextStroke.setBackgroundColor(getResources().getColor(R.color.colorError));
                break;
            case GOOD_FORMAT:
                editTextStroke.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                break;
        }
    }

    private void showProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
    }
    private void hideProgressBar(){
        progressBar.setVisibility(View.GONE);
    }

    // ----------------------------
    //     ACTION AND NAVIGATION
    // ----------------------------
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case (R.id.addFriendSearchButton):
                if (searchFriendEditText.getText() != null
                        && !searchFriendEditText.getText().toString().equals("")){
                    String searchText = searchFriendEditText.getText().toString();
                    if (Tools.isEmailValid(searchText)){
                        searchFriend(searchText);
                    } else {
                        Toast.makeText(this, "Veuillez entrer un email valide !", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Veuillez entrer un email valide !", Toast.LENGTH_SHORT).show();
                }
                break;
            case (R.id.addFriendConnectingButton):
                // TODO SendConnectionAskToFriend
                // TODO Dialog connection asking send
                // TODO back pressed
                break;
        }
    }

    private void editTextListener() {
        searchFriendEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                setupEditText(EditTextState.NO_TEXT);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()){
                    setupVisibilities(State.STARTING);
                } else {
                    if (Tools.isEmailValid(s.toString())) setupEditText(EditTextState.GOOD_FORMAT);
                    else setupEditText(EditTextState.WRONG_FORMAT);
                }
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    // ------------------------
    //     SETUP VIEWS
    // ------------------------
    private void setupViews() {
        searchFriendEditText = findViewById(R.id.addFriendEditText);
        editTextStroke = findViewById(R.id.addFriendEditTextContainer); //
        searchButton = findViewById(R.id.addFriendSearchButton);
        resultLayout = findViewById(R.id.addFriendResultLayout);
        progressBar = findViewById(R.id.addFriendProgressBar);
        noUserFindText = findViewById(R.id.addFriendNoUserFindText);
        friendImage = findViewById(R.id.addFriendFriendImage);
        friendUsername = findViewById(R.id.addFriendUsername);
        connectingButton = findViewById(R.id.addFriendConnectingButton);
        setupButtonListeners();
    }

    private void setupButtonListeners() {
        searchButton.setOnClickListener(this);
        connectingButton.setOnClickListener(this);
        editTextListener();
    }




}
