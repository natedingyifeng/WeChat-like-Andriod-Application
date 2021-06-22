package com.dyf.andriod_frontend.friendrequest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dyf.andriod_frontend.R;
import com.dyf.andriod_frontend.moments.MomentsComment;

import java.util.LinkedList;


public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.FriendRequestHolder> {
    LinkedList<FriendRequest> data;
    Context context;

    public FriendRequestAdapter(LinkedList<FriendRequest> data, Context context){
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public FriendRequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_friend_request, parent,false);
        FriendRequestHolder holder = new FriendRequestHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRequestHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class FriendRequestHolder extends RecyclerView.ViewHolder{



        public FriendRequestHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
