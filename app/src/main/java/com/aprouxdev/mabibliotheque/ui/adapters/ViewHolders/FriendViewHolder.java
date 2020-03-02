package com.aprouxdev.mabibliotheque.ui.adapters.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aprouxdev.mabibliotheque.R;
import com.aprouxdev.mabibliotheque.models.User;
import com.aprouxdev.mabibliotheque.ui.adapters.FriendAdapter;
import com.bumptech.glide.RequestManager;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FriendViewHolder extends RecyclerView.ViewHolder {

    // UI Vars
    private ImageView friendImage; //
    private ImageView isSelectedImage; //
    private TextView friendName; //




    public FriendViewHolder(@NonNull View itemView, final FriendAdapter.OnItemClickListener listener) {
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

    private void setupViews(View view) {
        friendImage = view.findViewById(R.id.itemFriendImageView);
        isSelectedImage = view.findViewById(R.id.itemFriendSelectedImage);
        isSelectedImage.setVisibility(View.GONE);
        friendName = view.findViewById(R.id.itemFriendUserName);
    }

    public void updateWithFriend(User user, boolean isSelected, RequestManager glide) {
        if (user.getUrlPicture() != null){
            glide.load(user.getUrlPicture())
                    .placeholder(R.drawable.ic_user_no_image)
                    .into(friendImage);
        }
        if (user.getUsername() != null){
            friendName.setText(user.getUsername());
        }
        int selectedImageVisibility = isSelected ? View.VISIBLE : View.GONE;
        isSelectedImage.setVisibility(selectedImageVisibility);
    }

    public void updateSelectedFriend() {
    }
}
