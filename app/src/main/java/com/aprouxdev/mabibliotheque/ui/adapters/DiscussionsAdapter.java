package com.aprouxdev.mabibliotheque.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aprouxdev.mabibliotheque.R;
import com.aprouxdev.mabibliotheque.models.Discussion;
import com.aprouxdev.mabibliotheque.ui.adapters.ViewHolders.DiscussionViewHolder;
import com.bumptech.glide.RequestManager;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DiscussionsAdapter extends RecyclerView.Adapter<DiscussionViewHolder> {

    //FOR DATA
    private List<Discussion> discussions;
    private OnItemClickListener mListener;
    private final RequestManager glide;


    public DiscussionsAdapter(List<Discussion> discussions, RequestManager glide) {
        this.discussions = discussions;
        this.glide = glide;
    }




    @Override
    public DiscussionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_discussion, parent, false);
        return new DiscussionViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull DiscussionViewHolder holder, int position) {
        final Discussion discussion = discussions.get(position);
        holder.updateWithDiscussion(discussion, this.glide);
    }

    @Override
    public int getItemCount() {
        return discussions.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.mListener = onItemClickListener;
    }



}
