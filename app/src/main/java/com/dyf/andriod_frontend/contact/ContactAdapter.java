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

import com.bumptech.glide.Glide;
import com.dyf.andriod_frontend.R;
import com.dyf.andriod_frontend.utils.HttpRequest;

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
        if(data.get(position).getType()==0)
        {
        Glide
                .with(context)
                .load(HttpRequest.media_url + contact.getIcon())
                .into(avatarIcon);
        }
        else {
            avatarIcon.setImageResource(contact.getAvatarIcon());
        }
        return convertView;
    }
}
