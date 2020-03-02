package com.aprouxdev.mabibliotheque.ui.adapters.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aprouxdev.mabibliotheque.R;
import com.aprouxdev.mabibliotheque.database.firestoreDatabase.DiscussionHelper;
import com.aprouxdev.mabibliotheque.database.firestoreDatabase.UserHelper;
import com.aprouxdev.mabibliotheque.models.Discussion;
import com.aprouxdev.mabibliotheque.models.Message;
import com.aprouxdev.mabibliotheque.models.User;
import com.aprouxdev.mabibliotheque.tools.general.Tools;
import com.aprouxdev.mabibliotheque.ui.adapters.DiscussionsAdapter;
import com.bumptech.glide.RequestManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DiscussionViewHolder extends RecyclerView.ViewHolder {

    // UI vars
    private ImageView discussionImageView;
    private TextView discussionTitle;
    private TextView discussionDate;
    private TextView discussionLastMessage;
    private ImageView discussionNewMessageImage;



    public DiscussionViewHolder(View itemView, final DiscussionsAdapter.OnItemClickListener listener) {
        super(itemView);
        setupViews(itemView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        listener.onItemClick(position);
                    }
                }
            }
        });
    }


    public void updateWithDiscussion(Discussion discussion, RequestManager glide){

        // Setup discussion title
        if (discussion.getName() == null){
            String titleFormatted = formatDiscussionTitle(discussion);
            discussionTitle.setText(titleFormatted);
        } else {
            discussionTitle.setText(discussion.getName());
        }

        // Setup ImageView
        String firstUserUid = discussion.getUsersUid().get(0);
        UserHelper.getUser(firstUserUid).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    User firstUser = Objects.requireNonNull(task.getResult()).toObject(User.class);
                    if (firstUser != null && firstUser.getUrlPicture() != null){
                        glide.load(firstUser.getUrlPicture())
                                .placeholder(R.drawable.ic_user_no_image)
                                .into(discussionImageView);
                    }

                }
            }
        });
        DiscussionHelper.getAllMessagesForDiscussion(discussion.getUid()).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    Message lastMessage = Objects.requireNonNull(task.getResult()).getDocuments().get(0).toObject(Message.class);
                    if (lastMessage != null){
                        if (lastMessage.getMessage() != null){
                            discussionLastMessage.setText(lastMessage.getMessage());
                        }
                        if (lastMessage.getDateCreated() != null){
                            Date today = Calendar.getInstance().getTime();
                            String date = Tools.convertDateToFormattedDate(today, lastMessage.getDateCreated());
                            discussionDate.setText(date);
                        }
                    }
                }
            }
        });

        // TODO newMessageImage visibility
        discussionNewMessageImage.setVisibility(View.INVISIBLE);
    }

    private String formatDiscussionTitle(Discussion discussion) {
        StringBuilder title = new StringBuilder();

        for (int i = 0; i < discussion.getUsersName().size() - 1 ; i++) {
            title.append(discussion.getUsersName().get(i));
            title.append(", ");
        }
        int lastNameIndex = discussion.getUsersName().size() - 1;
        title.append(discussion.getUsersName().get(lastNameIndex));

        return title.toString();
    }



    // -----------------------
    //    SETUP VIEWS
    // -----------------------

    private void setupViews(View v) {
        discussionImageView = v.findViewById(R.id.itemDiscussionImageView);
        discussionTitle = v.findViewById(R.id.itemDiscussionTitle);
        discussionDate = v.findViewById(R.id.itemDiscussionDate);
        discussionLastMessage = v.findViewById(R.id.itemDiscussionLastMessage);
        discussionNewMessageImage = v.findViewById(R.id.itemDiscussionNewMessageImage);
    }

}
