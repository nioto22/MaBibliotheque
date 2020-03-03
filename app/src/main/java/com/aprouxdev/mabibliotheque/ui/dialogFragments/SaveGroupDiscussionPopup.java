package com.aprouxdev.mabibliotheque.ui.dialogFragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.aprouxdev.mabibliotheque.R;
import com.aprouxdev.mabibliotheque.database.firestoreDatabase.DiscussionHelper;
import com.aprouxdev.mabibliotheque.tools.general.Tools;
import com.aprouxdev.mabibliotheque.util.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import static com.aprouxdev.mabibliotheque.util.Constants.DISCUSSION_SAVE_POPUP_BUNDLE_USERS_LIST;
import static com.aprouxdev.mabibliotheque.util.Constants.DISCUSSION_SAVE_POPUP_BUNDLE_USER_UID;

public class SaveGroupDiscussionPopup extends DialogFragment implements View.OnClickListener {

    //UI Vars
    private EditText editName; //popupSaveDiscussionEditName
    private Button createButton; // popupFriendDetailsSendMessageButton
    private Button cancelButton; // popupSaveDiscussionCancelButton
    private ProgressBar progressBar; // popupSaveDiscussionProgressBar

    // Data
    private String userUid;
    private List<String> usersList;
    private Context context;
    private String discussionName;

    public static SaveGroupDiscussionPopup newInstance(String userUid, ArrayList<String> usersList) {
        SaveGroupDiscussionPopup discussionPopup = new SaveGroupDiscussionPopup();

        Bundle args = new Bundle();
        args.putString(DISCUSSION_SAVE_POPUP_BUNDLE_USER_UID, userUid);
        args.putStringArrayList(DISCUSSION_SAVE_POPUP_BUNDLE_USERS_LIST, usersList);
        discussionPopup.setArguments(args);

        return discussionPopup;
    }

    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        context = getActivity();

        getPopupArguments();

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.popup_save_group_discussion, container, false);
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
            userUid = getArguments().getString(DISCUSSION_SAVE_POPUP_BUNDLE_USER_UID);
            usersList = getArguments().getStringArrayList(DISCUSSION_SAVE_POPUP_BUNDLE_USERS_LIST);
            if(usersList == null) {
                Toast.makeText(context, getResources().getString(R.string.toast_unknown_error), Toast.LENGTH_SHORT).show();
                dismissDialog();
            } else {
                usersList.add(userUid);
            }
        }
    }


    //----------------------------
    //         UI
    //----------------------------

    private void setupViews(View view) {
        editName = view.findViewById(R.id.popupSaveDiscussionEditName);
        editName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 ) {
                    createButton.setEnabled(true);
                    discussionName = s.toString();
                }
                else {
                    createButton.setEnabled(false);
                    discussionName = s.toString();
                }
            }
        });
        createButton = view.findViewById(R.id.popupSaveDiscussionCreateButton);
        createButton.setEnabled(false);
        createButton.setOnClickListener(this);
        cancelButton = view.findViewById(R.id.popupSaveDiscussionCancelButton);
        cancelButton.setOnClickListener(this);
        progressBar = view.findViewById(R.id.popupSaveDiscussionProgressBar);
        progressBar.setVisibility(View.GONE);
    }

    private void setupDataOnViews() {

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
            case (R.id.popupSaveDiscussionCreateButton) :
                createDiscussion();
                break;
            case (R.id.popupSaveDiscussionCancelButton):
                dismissDialog();
                break;
        }
    }

    private void createDiscussion() {
        String discussionUid = Tools.createNewId();
        DiscussionHelper.createDiscussion(discussionUid, discussionName, usersList)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            openDiscussion(discussionUid);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, getResources().getString(R.string.toast_unknown_error), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openDiscussion(String discussionUid) {
    }

}
