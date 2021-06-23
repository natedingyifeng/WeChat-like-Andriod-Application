package com.dyf.andriod_frontend.message;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dyf.andriod_frontend.BaiduLocationActivity;
import com.dyf.andriod_frontend.R;
import com.dyf.andriod_frontend.utils.HttpRequest;
//import com.mcoy_jiang.videomanager.ui.McoyVideoView;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

public class MessageAdapter extends BaseAdapter {

    private LinkedList<MessageA> data;
    private Context context;
//    private MediaPlayer mp = new MediaPlayer();

    public MessageAdapter(LinkedList<MessageA> data, Context context) {
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
        MessageA messageA = data.get(position);
        if(messageA.getComponentType() == 0) {
            convertView = LayoutInflater.from(context).inflate(R.layout.chat_message_item_left, parent, false);
            ImageView icon = (ImageView) convertView.findViewById(R.id.message_avatar_icon);
            Glide
                    .with(context)
                    .load(HttpRequest.media_url+messageA.getAvatarIcon())
                    .into(icon);
//            icon.setImageResource(messageA.getAvatarIcon());
            TextView content = (TextView) convertView.findViewById(R.id.message_content);
            content.setText(messageA.getContent());
            return convertView;
        }
        else if(messageA.getComponentType() == 1)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.chat_message_item_right, parent, false);
            ImageView icon = (ImageView) convertView.findViewById(R.id.message_avatar_icon);
            Glide
                    .with(context)
                    .load(HttpRequest.media_url+messageA.getAvatarIcon())
                    .into(icon);
//            icon.setImageResource(messageA.getAvatarIcon());
            TextView content = (TextView) convertView.findViewById(R.id.message_content);
            content.setText(messageA.getContent());
            return convertView;
        }
        else if(messageA.getComponentType() == 2)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.chat_message_image_left, parent, false);
            ImageView icon = (ImageView) convertView.findViewById(R.id.message_avatar_icon);
            Glide
                    .with(context)
                    .load(HttpRequest.media_url+messageA.getAvatarIcon())
                    .into(icon);
//            icon.setImageResource(messageA.getAvatarIcon());
            ImageView content = (ImageView) convertView.findViewById(R.id.message_content_image);
            Glide
                    .with(context)
                    .load(HttpRequest.media_url+messageA.getContentImage())
                    .into(content);
            return convertView;
        }
        else if(messageA.getComponentType() == 3)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.chat_message_image_right, parent, false);
            ImageView icon = (ImageView) convertView.findViewById(R.id.message_avatar_icon);
            Glide
                    .with(context)
                    .load(HttpRequest.media_url+messageA.getAvatarIcon())
                    .into(icon);
//            icon.setImageResource(messageA.getAvatarIcon());
            ImageView content = (ImageView) convertView.findViewById(R.id.message_content_image);
            Glide
                    .with(context)
                    .load(HttpRequest.media_url+messageA.getContentImage())
                    .into(content);
            return convertView;
        }
        else if(messageA.getComponentType() == 4)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.chat_message_video_left, parent, false);
            ImageView icon = (ImageView) convertView.findViewById(R.id.message_avatar_icon);
            Glide
                    .with(context)
                    .load(messageA.getAvatarIcon())
                    .into(icon);
//            icon.setImageResource(messageA.getAvatarIcon());
            JzvdStd videoView = (JzvdStd) convertView.findViewById(R.id.player_list_video);
            videoView.setUp(messageA.getContentImage(), "视频", Jzvd.SCREEN_NORMAL);
            Glide.with(context)
                    .load(HttpRequest.media_url+messageA.getContentImage())
                    .into(videoView.posterImageView);
            return convertView;
        }
        else if(messageA.getComponentType() == 5)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.chat_message_video_right, parent, false);
            ImageView icon = (ImageView) convertView.findViewById(R.id.message_avatar_icon);
            Glide
                    .with(context)
                    .load(HttpRequest.media_url+messageA.getAvatarIcon())
                    .into(icon);
//            icon.setImageResource(messageA.getAvatarIcon());
            JzvdStd videoView = (JzvdStd) convertView.findViewById(R.id.player_list_video);
            videoView.setUp(messageA.getContentImage(), "视频", Jzvd.SCREEN_NORMAL);
            Glide.with(context)
                    .load(messageA.getContentImage())
                    .into(videoView.posterImageView);
            return convertView;
        }
        else if(messageA.getComponentType() == 6)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.chat_message_voice_left, parent, false);
            ImageView icon = (ImageView) convertView.findViewById(R.id.message_avatar_icon);
            Glide
                    .with(context)
                    .load(HttpRequest.media_url+messageA.getAvatarIcon())
                    .into(icon);
//            icon.setImageResource(messageA.getAvatarIcon());
            Button voice = (Button) convertView.findViewById(R.id.voice_content);
            voice.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    MediaPlayer mp = new MediaPlayer();
                    if(mp.isPlaying() == false){
                        try {
                            mp.setDataSource(messageA.getContentImage());
                            mp.prepare();
                            mp.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        mp.stop();
                        mp.reset();
                    }
                }
            });
            return convertView;
        }
        else if(messageA.getComponentType() == 7)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.chat_message_voice_right, parent, false);
            ImageView icon = (ImageView) convertView.findViewById(R.id.message_avatar_icon);
            Glide
                    .with(context)
                    .load(HttpRequest.media_url+messageA.getAvatarIcon())
                    .into(icon);
//            icon.setImageResource(messageA.getAvatarIcon());
            Button voice = (Button) convertView.findViewById(R.id.voice_content);
            voice.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    MediaPlayer mp = new MediaPlayer();
                    if(mp.isPlaying() == false){
                        try {
                            mp.setDataSource(messageA.getContentImage());
                            mp.prepare();
                            mp.start();
                        } catch (IOException e) {
                            mp.stop();
                            mp.reset();
                            e.printStackTrace();
                        }
                    }
                    else {
                        mp.stop();
                        mp.reset();
                    }
                }
            });
            return convertView;
        }
        else if(messageA.getComponentType() == 8)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.chat_message_location_left, parent, false);
            ImageView icon = (ImageView) convertView.findViewById(R.id.message_avatar_icon);
            Glide
                    .with(context)
                    .load(HttpRequest.media_url+messageA.getAvatarIcon())
                    .into(icon);
//            icon.setImageResource(messageA.getAvatarIcon());
            TextView text = (TextView) convertView.findViewById(R.id.location_content);
            text.setText(messageA.getContentImage());
            Button location = (Button) convertView.findViewById(R.id.location_button);
            location.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, BaiduLocationActivity.class);
                    context.startActivity(intent);
                }
            });
            return convertView;
        }
        else if(messageA.getComponentType() == 9)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.chat_message_location_right, parent, false);
            ImageView icon = (ImageView) convertView.findViewById(R.id.message_avatar_icon);
            Glide
                    .with(context)
                    .load(messageA.getAvatarIcon())
                    .into(icon);
//            icon.setImageResource(messageA.getAvatarIcon());
            TextView text = (TextView) convertView.findViewById(R.id.location_content);
            text.setText(HttpRequest.media_url+messageA.getContentImage());
            Button location = (Button) convertView.findViewById(R.id.location_button);
            location.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, BaiduLocationActivity.class);
                    context.startActivity(intent);
                }
            });
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
