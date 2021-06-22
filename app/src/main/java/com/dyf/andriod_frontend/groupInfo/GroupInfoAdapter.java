package com.dyf.andriod_frontend.groupInfo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dyf.andriod_frontend.R;

import java.util.LinkedList;

//import com.mcoy_jiang.videomanager.ui.McoyVideoView;

public class GroupInfoAdapter extends BaseAdapter {

    private LinkedList<groupInfo> data;
    private Context context;

    public GroupInfoAdapter(LinkedList<groupInfo> data, Context context) {
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
        groupInfo info = data.get(position);
        convertView = LayoutInflater.from(context).inflate(R.layout.group_info_item, parent, false);
        TextView title = (TextView) convertView.findViewById(R.id.title_info);
        title.setText(info.getTitle());
        TextView content = (TextView) convertView.findViewById(R.id.content_info);
        content.setText(info.getContent());
        return convertView;
    }
}
