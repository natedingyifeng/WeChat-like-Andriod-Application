package com.dyf.andriod_frontend;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.ListFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dyf.andriod_frontend.contact.Contact;
import com.dyf.andriod_frontend.contact.ContactAdapter;
import com.dyf.andriod_frontend.utils.HttpRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.java_websocket.client.WebSocketClient;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import android.os.Handler;

public class ContactsFragment extends ListFragment {
    private ListView listView;
    private LinkedList<Contact> contacts;
    public MainActivity mainActivity;
    public String id;
    private Handler handler;

    private ListView ContactLstView;
    private RecyclerView friendRequestRecyclerView;

    @BindView(R.id.bottomNavigationView)
    public BottomNavigationView bottomNavigationView;
    private ContactsMessageReceiver contactMessageReceiver;

    private class ContactsMessageReceiver extends BroadcastReceiver {

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onReceive(Context context, Intent intent) {
            String message=intent.getStringExtra("message");
            try {
                // TODO 获得好友请求 WebSocket
                JSONArray json_contact = new JSONArray(message);
                Log.d("sentuser", json_contact.getJSONObject(0).getJSONObject("sentUser").get("id").toString());
                sendNotificationOfNewFriend(json_contact.getJSONObject(0).getJSONObject("sentUser").get("username").toString());
                id = new String(json_contact.getJSONObject(0).getJSONObject("sentUser").get("id").toString());
            } catch (JSONException e) {
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private void sendNotificationOfNewFriend(String name) {
            Intent intent = new Intent(getContext(), NotificationActivity.class);
            PendingIntent pi = PendingIntent.getActivities(getContext(), 0, new Intent[]{intent}, 0);
            NotificationManager manager = (NotificationManager) getActivity().getSystemService(getActivity().NOTIFICATION_SERVICE);
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
                    .setContentTitle("新的好友请求")
                    .setContentText(" "+name+" 向你发送了一条好友请求")
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
        }
    }

    private void doRegisterReceiver() {
        contactMessageReceiver = new ContactsMessageReceiver();
        IntentFilter filter = new IntentFilter("com.dyf.servicecallback.content");
        getActivity().registerReceiver(contactMessageReceiver, filter);
    }

    public ContactsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ContactsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static com.dyf.andriod_frontend.ContactsFragment newInstance() {
        com.dyf.andriod_frontend.ContactsFragment fragment = new com.dyf.andriod_frontend.ContactsFragment();
        return fragment;
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity ) getActivity();
        SharedPreferences sp = mainActivity.getSharedPreferences(getString(R.string.store), Context.MODE_PRIVATE);
        String username = sp.getString("username", "");
        String password = sp.getString("password", "");
        JSONObject ws_msg_login = new JSONObject();
        try {
            ws_msg_login.put("bizType", "USER_LOGIN");
            ws_msg_login.put("password", password);
            ws_msg_login.put("username", username);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mainActivity.sendMsg(ws_msg_login.toString());
        doRegisterReceiver();
        TextView title = getActivity().findViewById(R.id.title_text);
        title.setText(R.string.contacts);
        Button title_back = getActivity().findViewById(R.id.title_back);
        title_back.setVisibility(View.INVISIBLE);
        Button title_back_2 = getActivity().findViewById(R.id.title_back2);
        title_back_2.setVisibility(View.INVISIBLE);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        bottomNavigationView = activity.findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.VISIBLE);
        Context context = getActivity();
        contacts = new LinkedList<>();
        contacts.add(new Contact("添加朋友", R.drawable.add_friends, 1, null));
        contacts.add(new Contact("发起群聊", R.drawable.group_chat, 2, null));

        HashMap<String, String> params = new HashMap<>();
        params.put("keyword", username);

        HttpRequest.sendOkHttpPostRequest("user/search", new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(getActivity().getApplicationContext(),R.string.network_error, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resStr = response.body().string();
                Log.e("response", resStr);
                try {
                    JSONObject jsonObject = new JSONObject(resStr);
                    if (jsonObject.getBoolean("success")){
                        // 获取用户数据
                        JSONObject user = jsonObject.getJSONArray("users").getJSONObject(0);
                        try {
                            JSONArray friends = user.getJSONArray("contacts");
                            Log.d("len", friends.getJSONObject(0).getString("username") + "(" + friends.getJSONObject(0).getString("nickname") + ")");
                            for (int i = 0; i < friends.length(); i++) {
                                contacts.add(new Contact(friends.getJSONObject(i).getString("username"), R.drawable.contacts_1, 0, friends.getJSONObject(i).getString("id")));
                            }
                        } catch (JSONException e) {}
                        handler.sendEmptyMessage(1);
                    }else{
                        Looper.prepare();
                        Toast.makeText(getActivity().getApplicationContext(),"好友列表获取失败", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                        handler.sendEmptyMessage(1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(getActivity().getApplicationContext(),R.string.json_parse_error, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }, params);

        handler = new Handler(){
            @SuppressLint("HandlerLeak")
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                setListAdapter(new ContactAdapter(contacts, context));
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if(contacts.get(position).getType() == 0)
        {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            MessagesFragment messagesFragment = new MessagesFragment();
            messagesFragment.setInfo(0, contacts.get(position).getId(), contacts.get(position).getNickname());
            transaction.replace(R.id.flFragment, messagesFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            TextView title = getActivity().findViewById(R.id.title_text);
            title.setText(contacts.get(position).getNickname());
        }
        else if(contacts.get(position).getType() == 1)
        {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            AddFriendFragment addfriendFragment = new AddFriendFragment();
            transaction.replace(R.id.flFragment, addfriendFragment);
            transaction.addToBackStack(null);
            transaction.commit();
//            JSONObject ws_msg_FriendRequestReply = new JSONObject();
//            try {
//                ws_msg_FriendRequestReply.put("bizType", "FRIEND_REQUEST_REPLY");
//                ws_msg_FriendRequestReply.put("agree", true);
//                ws_msg_FriendRequestReply.put("replyToUserId", this.id);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            mainActivity.sendMsg(ws_msg_FriendRequestReply.toString());
        }
        else if(contacts.get(position).getType() == 2)
        {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            CreateGroupMemberFragment creategroupFragment = new CreateGroupMemberFragment();
            transaction.replace(R.id.flFragment, creategroupFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        Log.d("position", String.valueOf(position));
    }
}