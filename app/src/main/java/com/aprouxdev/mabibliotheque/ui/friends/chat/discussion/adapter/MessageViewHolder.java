package com.aprouxdev.mabibliotheque.ui.friends.chat.discussion.adapter;

import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aprouxdev.mabibliotheque.R;
import com.aprouxdev.mabibliotheque.models.Message;
import com.aprouxdev.mabibliotheque.tools.general.Tools;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    // UI vars
    private RelativeLayout rootView;
    private LinearLayout profileContainer;
    private ImageView imageViewProfile;
    private RelativeLayout messageContainer;
    private CardView cardViewBookSent;
    private ImageView bookSentImage;
    private LinearLayout textMessageContainer;
    private TextView textViewMessage;
    private TextView textViewDate;

    //FOR DATA
    private final int colorCurrentUser;
    private final int colorRemoteUser;

    public MessageViewHolder(View itemView) {
        super(itemView);
        setupViews(itemView);
        colorCurrentUser = ContextCompat.getColor(itemView.getContext(), R.color.colorAccent);
        colorRemoteUser = ContextCompat.getColor(itemView.getContext(), R.color.background_orange);
    }


    public void updateWithMessage(Message message, String currentUserId, RequestManager glide){

        // Check if current user is the sender
        Boolean isCurrentUser = message.getUserSender().getUid().equals(currentUserId);

        // Update message TextView
        this.textViewMessage.setText(message.getMessage());
        this.textViewMessage.setTextAlignment(isCurrentUser ? View.TEXT_ALIGNMENT_TEXT_END : View.TEXT_ALIGNMENT_TEXT_START);

        // Update date TextView
        if (message.getDateCreated() != null) this.textViewDate.setText(Tools.convertDateToHour(message.getDateCreated()));

        // Update profile picture ImageView
        if (message.getUserSender().getUrlPicture() != null)
            glide.load(message.getUserSender().getUrlPicture())
                    .apply(RequestOptions.circleCropTransform())
                    .into(imageViewProfile);

        // Update image sent ImageView
        if (message.getBookImageLink() != null){
            glide.load(message.getBookImageLink())
                    .placeholder(R.drawable.book_placeholder)
                    .into(bookSentImage);
            this.bookSentImage.setVisibility(View.VISIBLE);
        } else {
            this.bookSentImage.setVisibility(View.GONE);
        }

        //Update Message Bubble Color Background
        ((GradientDrawable) textMessageContainer.getBackground()).setColor(isCurrentUser ? colorCurrentUser : colorRemoteUser);

        // Update all views alignment depending is current user or not
        this.updateDesignDependingUser(isCurrentUser);
    }

    private void updateDesignDependingUser(Boolean isSender){

        // PROFILE CONTAINER
        RelativeLayout.LayoutParams paramsLayoutHeader = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsLayoutHeader.addRule(isSender ? RelativeLayout.ALIGN_PARENT_RIGHT : RelativeLayout.ALIGN_PARENT_LEFT);
        this.profileContainer.setLayoutParams(paramsLayoutHeader);

        // MESSAGE CONTAINER
        RelativeLayout.LayoutParams paramsLayoutContent = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsLayoutContent.addRule(isSender ? RelativeLayout.LEFT_OF : RelativeLayout.RIGHT_OF, R.id.activity_discussion_item_profile_container);
        this.messageContainer.setLayoutParams(paramsLayoutContent);

        // CARDVIEW IMAGE SEND
        RelativeLayout.LayoutParams paramsImageView = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsImageView.addRule(isSender ? RelativeLayout.ALIGN_LEFT : RelativeLayout.ALIGN_RIGHT, R.id.activity_discussion_item_message_container_text_message_container);
        this.cardViewBookSent.setLayoutParams(paramsImageView);

        this.rootView.requestLayout();
    }

    // -----------------------
    //    SETUP VIEWS
    // -----------------------

    private void setupViews(View v) {
        rootView = v.findViewById(R.id.activity_discussion_item_root_view);
        profileContainer = v.findViewById(R.id.activity_discussion_item_profile_container);
        imageViewProfile = v.findViewById(R.id.activity_discussion_item_profile_container_profile_image);
        messageContainer = v.findViewById(R.id.activity_discussion_item_message_container);
        cardViewBookSent = v.findViewById(R.id.activity_discussion_item_message_container_book_sent_cardview);
        bookSentImage = v.findViewById(R.id.activity_discussion_item_message_container_book_sent_cardview_image);
        textMessageContainer = v.findViewById(R.id.activity_discussion_item_message_container_text_message_container);
        textViewMessage = v.findViewById(R.id.activity_discussion_item_message_container_text_message_container_text_view);
        textViewDate = v.findViewById(R.id.activity_discussion_item_message_container_text_view_date);
    }

}
