package com.dyf.andriod_frontend.contact;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dyf.andriod_frontend.R;

import java.util.LinkedList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private LinkedList<Contact> data;

    // 完成类ContactViewHolder
    // 使用itemView.findViewById()方法来寻找对应的控件
    // TODO
    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        protected TextView nickname;
        protected ImageView avatarIcon;
        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            nickname = (TextView) itemView.findViewById(R.id.contact_nickname_text);
            avatarIcon = (ImageView) itemView.findViewById(R.id.contact_avatar_icon);
        }

    }

    public ContactAdapter(LinkedList<Contact> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // TODO
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycle_contact, parent, false);
        return new ContactViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        // TODO
        Contact contact = data.get(position);
        holder.nickname.setText(contact.getNickname());
        holder.avatarIcon.setImageResource(contact.getAvatarIcon());
    }

    @Override
    public int getItemCount() {
        // TODO
        return data.size();
    }
}
