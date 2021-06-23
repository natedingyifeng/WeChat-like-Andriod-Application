package com.dyf.andriod_frontend.chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dyf.andriod_frontend.R;
import com.dyf.andriod_frontend.utils.HttpRequest;

import java.util.List;

public class ChatAdapter extends BaseAdapter {

    private List<com.dyf.andriod_frontend.chat.Chat> data;
    private Context context;

    public ChatAdapter(List<com.dyf.andriod_frontend.chat.Chat> data, Context context) {
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

        convertView = LayoutInflater.from(context).inflate(R.layout.item_list_chat, parent, false);
        com.dyf.andriod_frontend.chat.Chat chat = data.get(position);
        // 修改View中各个控件的属性，使之显示对应位置Chat的内容
        // 使用convertView.findViewById()方法来寻找对应的控件
        // 控件ID见 res/layout/item_list_chat.xml
        // TODO
        ImageView icon = (ImageView) convertView.findViewById(R.id.chat_avatar_icon);
        icon.setFocusable(false);
        icon.setFocusableInTouchMode(false);
        if(chat.getChatType() == 0)
        {
            Glide
                    .with(context)
                    .load(HttpRequest.media_url+chat.getAvatarIcon())
                    .into(icon);
        }
        else {
           icon.setImageResource(chat.getIcon());
        }
        TextView nickname = (TextView) convertView.findViewById(R.id.nickname_text);
        nickname.setFocusable(false);
        nickname.setFocusableInTouchMode(false);
        nickname.setText(chat.getNickname());
        TextView last_speak_time = (TextView) convertView.findViewById(R.id.last_speak_time_text);
        last_speak_time.setFocusable(false);
        last_speak_time.setFocusableInTouchMode(false);
        last_speak_time.setText(chat.getLastSpeakTime());
        TextView last_speak = (TextView) convertView.findViewById(R.id.last_speak_text);
        last_speak.setFocusable(false);
        last_speak.setFocusableInTouchMode(false);
        last_speak.setText(chat.getLastSpeak());
        return convertView;
    }
}
