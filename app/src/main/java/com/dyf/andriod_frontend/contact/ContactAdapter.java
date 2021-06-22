package com.dyf.andriod_frontend.contact;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dyf.andriod_frontend.R;

import java.util.LinkedList;

public class ContactAdapter extends BaseAdapter {
    private LinkedList<Contact> data;
    private Context context;

    public ContactAdapter(LinkedList<Contact> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.item_recycle_contact, parent, false);
        Contact contact = data.get(position);
        TextView nickname = (TextView) convertView.findViewById(R.id.contact_nickname_text);
        nickname.setText(contact.getNickname());
        ImageView avatarIcon = (ImageView) convertView.findViewById(R.id.contact_avatar_icon);
        avatarIcon.setImageResource(contact.getAvatarIcon());
        return convertView;
    }
}
