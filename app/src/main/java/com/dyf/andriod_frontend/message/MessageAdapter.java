package com.dyf.andriod_frontend.message;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dyf.andriod_frontend.R;
//import com.mcoy_jiang.videomanager.ui.McoyVideoView;

import java.util.LinkedList;

public class MessageAdapter extends BaseAdapter {

    private LinkedList<Message> data;
    private Context context;

    public MessageAdapter(LinkedList<Message> data, Context context) {
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
        Message message = data.get(position);
        if(message.getComponentType() == 0) {
            convertView = LayoutInflater.from(context).inflate(R.layout.chat_message_item_left, parent, false);
            ImageView icon = (ImageView) convertView.findViewById(R.id.message_avatar_icon);
            icon.setImageResource(message.getAvatarIcon());
            TextView content = (TextView) convertView.findViewById(R.id.message_content);
            content.setText(message.getContent());
            return convertView;
        }
        else if(message.getComponentType() == 1)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.chat_message_item_right, parent, false);
            ImageView icon = (ImageView) convertView.findViewById(R.id.message_avatar_icon);
            icon.setImageResource(message.getAvatarIcon());
            TextView content = (TextView) convertView.findViewById(R.id.message_content);
            content.setText(message.getContent());
            return convertView;
        }
        else if(message.getComponentType() == 2)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.chat_message_image_left, parent, false);
            ImageView icon = (ImageView) convertView.findViewById(R.id.message_avatar_icon);
            icon.setImageResource(message.getAvatarIcon());
            ImageView content = (ImageView) convertView.findViewById(R.id.message_content_image);
            content.setImageURI(message.getContentImage());
            return convertView;
        }
        else if(message.getComponentType() == 3)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.chat_message_image_right, parent, false);
            ImageView icon = (ImageView) convertView.findViewById(R.id.message_avatar_icon);
            icon.setImageResource(message.getAvatarIcon());
            ImageView content = (ImageView) convertView.findViewById(R.id.message_content_image);
            content.setImageURI(message.getContentImage());
            return convertView;
        }
        else if(message.getComponentType() == 5)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.chat_message_video_right, parent, false);
            ImageView icon = (ImageView) convertView.findViewById(R.id.message_avatar_icon);
            icon.setImageResource(message.getAvatarIcon());
//            McoyVideoView videoView = (McoyVideoView) convertView.findViewById(R.id.videoView);
//            videoView.setVideoUrl(getRealFilePath(context, message.getContentImage()));
            return convertView;
        }
        else return null;
    }

    public static String getRealFilePath( final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }
}
