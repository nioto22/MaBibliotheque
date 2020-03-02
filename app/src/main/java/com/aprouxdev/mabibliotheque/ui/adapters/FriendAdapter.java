package com.aprouxdev.mabibliotheque.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aprouxdev.mabibliotheque.R;
import com.aprouxdev.mabibliotheque.models.User;
import com.aprouxdev.mabibliotheque.ui.adapters.ViewHolders.FriendViewHolder;
import com.bumptech.glide.RequestManager;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FriendAdapter extends RecyclerView.Adapter<FriendViewHolder> {


    private List<User> friendsList;
    private List<Boolean> friendsSelected;
    private OnItemClickListener mListener;
    private final RequestManager glide;


    public FriendAdapter(List<User> friendsList, RequestManager glide) {
        this.friendsList = friendsList;
        this.glide = glide;
    }

    public FriendAdapter(List<User> friendsList, List<Boolean> friendsSelected, RequestManager glide) {
        this.friendsList = friendsList;
        this.friendsSelected = friendsSelected;
        this.glide = glide;
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend, parent, false);
        return new FriendViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        final User user = friendsList.get(position);
        final boolean isSelected = friendsSelected != null ? friendsSelected.get(position) : false;
        holder.updateWithFriend(user, isSelected, this.glide);
    }


    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    public User getFriend(int id) { return friendsList.get(id); }


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(FriendAdapter.OnItemClickListener onItemClickListener){
        this.mListener = onItemClickListener;
    }
}
