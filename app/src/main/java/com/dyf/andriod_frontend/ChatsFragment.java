package com.dyf.andriod_frontend;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.ListFragment;

import com.dyf.andriod_frontend.chat.Chat;
import com.dyf.andriod_frontend.chat.ChatAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import butterknife.BindView;

import static android.content.Context.NOTIFICATION_SERVICE;

public class ChatsFragment extends ListFragment {
    private ChatAdapter chatAdapter;
    private List<Chat> data;
    private ListView listView;

    @BindView(R.id.bottomNavigationView)
    public BottomNavigationView bottomNavigationView;

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView title = getActivity().findViewById(R.id.title_text);
        title.setText("聊天");
        Button title_back = getActivity().findViewById(R.id.title_back);
        title_back.setVisibility(View.INVISIBLE);
        Button title_back_2 = getActivity().findViewById(R.id.title_back2);
        title_back_2.setVisibility(View.INVISIBLE);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        bottomNavigationView = activity.findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.VISIBLE);
        Context context = getActivity();
        data = new ArrayList<Chat>();
        data.add(new Chat(getString(R.string.nickname1), R.drawable.contacts_1, "晚安", "2021/01/01"));
        data.add(new Chat(getString(R.string.nickname2), R.drawable.contacts_2, "hhh好滴", "2021/01/02"));
        data.add(new Chat(getString(R.string.nickname3), R.drawable.contacts_3, "OK", "2021/01/03"));
        data.add(new Chat(getString(R.string.nickname4), R.drawable.contacts_4, "你到了吗", "2021/01/04"));
        data.add(new Chat(getString(R.string.nickname5), R.drawable.contacts_5, "没事", "2021/01/05", 1));
        chatAdapter = new ChatAdapter(data,context);
        setListAdapter(chatAdapter);

//        Button titleBack_2 = (Button) getActivity().findViewById(R.id.title_back2);
//        titleBack_2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                CreateGroupMemberFragment creategroupFragment = new CreateGroupMemberFragment();
//                transaction.replace(R.id.flFragment, creategroupFragment);
//                transaction.addToBackStack(null);
//                transaction.commit();
//            }
//        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chats, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        // Notification
        Intent intent = new Intent(getContext(), NotificationActivity.class);
        PendingIntent pi = PendingIntent.getActivities(getContext(), 0, new Intent[]{intent}, 0);
        NotificationManager manager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
        // 兼容  API 26，Android 8.0
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // 第三个参数表示通知的重要程度，默认则只在通知栏闪烁一下
            NotificationChannel notificationChannel = new NotificationChannel("AppTestNotificationId", "AppTestNotificationName", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setShowBadge(true);
            notificationChannel.enableVibration(true);
            // 注册通道，注册后除非卸载再安装否则不改变
            manager.createNotificationChannel(notificationChannel);
        }
        Notification notification = new Notification.Builder(getContext())
                .setContentTitle("This is content title")
                .setContentText("This is content text")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_play)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_play))
                .setContentIntent(pi)
                .setAutoCancel(true) // 取消通知
                .setSound(Uri.fromFile(new File("/system/media/audio/notifications/Simple.ogg"))) // 通知铃声
                //        .setSound(Uri.fromFile(new File("/system/media/audio/ringtones/Luna.ogg")))
                .setVibrate(new long[]{0, 1000, 1000, 1000})
                .setLights(Color.GREEN, 1000, 1000) // LED灯
                .setChannelId("AppTestNotificationId")
                //        .setLights(Color.GREEN, 1000, 1000)
                .setDefaults(Notification.DEFAULT_ALL)
                //        .setStyle(new NotificationCompat.BigTextStyle().bigText("Learn how to build notifications, send and sync data, and use voice actions. Get the official Android IDE and developer tools to build apps for Android."))
//                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.contacts_6)))
                .setPriority(Notification.PRIORITY_MAX)
                .build();
        manager.notify(1, notification);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        MessagesFragment messagesFragment = new MessagesFragment();
        messagesFragment.setChatType(data.get(position).getChatType());
        transaction.replace(R.id.flFragment, messagesFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        TextView title = getActivity().findViewById(R.id.title_text);
        title.setText(data.get(position).getNickname());
        data.get(position).setLastSpeak("hhh加油呀");
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
        String createdate = sdf.format(date);
        data.get(position).setLastSpeakTime(createdate);


//        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) title.getLayoutParams();
//        lp.setMargins(0,0,180, 0);
//        title.setLayoutParams(lp);
        Log.d("position", String.valueOf(position));
    }

}