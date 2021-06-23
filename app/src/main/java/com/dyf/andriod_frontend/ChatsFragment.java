package com.dyf.andriod_frontend;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
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
import com.dyf.andriod_frontend.contact.Contact;
import com.dyf.andriod_frontend.contact.ContactAdapter;
import com.dyf.andriod_frontend.utils.HttpRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import android.os.Handler;
import android.widget.Toast;

import static android.content.Context.NOTIFICATION_SERVICE;

public class ChatsFragment extends ListFragment {
    private ChatAdapter chatAdapter;
    private List<Chat> data;
    private ListView listView;
    private LinkedList<String> contacts_id;
    private LinkedList<String> contacts_name;
    private Handler handler;
    private Handler handler_chats;
    private Handler handler_group_chats;
    private Handler handler_ws;
    public MainActivity mainActivity;
    private Context context;

    @BindView(R.id.bottomNavigationView)
    public BottomNavigationView bottomNavigationView;

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity ) getActivity();
        TextView title = getActivity().findViewById(R.id.title_text);
        title.setText("聊天");
        Button title_back = getActivity().findViewById(R.id.title_back);
        title_back.setVisibility(View.INVISIBLE);
        Button title_back_2 = getActivity().findViewById(R.id.title_back2);
        title_back_2.setVisibility(View.INVISIBLE);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        bottomNavigationView = activity.findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.VISIBLE);
        context = getActivity();
        SharedPreferences sp = getActivity().getSharedPreferences(getString(R.string.store), Context.MODE_PRIVATE);
        String username = sp.getString("username", "");
        handler = new Handler(){
            @SuppressLint("HandlerLeak")
            public void handleMessage(Message msg){
                super.handleMessage(msg);

                getGroupChats();
            }
        };
        handler_group_chats = new Handler(){
            @SuppressLint("HandlerLeak")
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                getChatMessages();
            }
        };
        handler_chats = new Handler(){
            @SuppressLint("HandlerLeak")
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                showChatMessages();
            }
        };
        contacts_id = new LinkedList<>();
        contacts_name = new LinkedList<>();
        data = new ArrayList<Chat>();

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
                if (resStr.charAt(resStr.length()-1) != '}'){
                    resStr = resStr + "}";
                }
                Log.e("response", resStr);
                try {
                    JSONObject jsonObject = new JSONObject(resStr);
                    if (jsonObject.getBoolean("success")){
                        // 获取用户数据
                        JSONObject user = jsonObject.getJSONArray("users").getJSONObject(0);
                        JSONArray friends = new JSONArray();
                        if(user.has("contacts"))
                            friends =user.getJSONArray("contacts");
//                        Log.d("len", friends.getJSONObject(0).getString("username")+"("+friends.getJSONObject(0).getString("nickname")+")");
                        for (int i = 0; i < friends.length(); i++)
                        {
                            contacts_id.add(friends.getJSONObject(i).getString("id"));
                            contacts_name.add(friends.getJSONObject(i).getString("username"));
                        }
                        handler.sendEmptyMessage(1);
                    }else{
                        Looper.prepare();
                        Toast.makeText(getActivity().getApplicationContext(),"好友列表获取失败", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                        handler.sendEmptyMessage(1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
//                    Looper.prepare();
//                    Toast.makeText(getActivity().getApplicationContext(),R.string.json_parse_error, Toast.LENGTH_SHORT).show();
//                    Looper.loop();
                    handler.sendEmptyMessage(1);
                }
            }
        }, params);

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

    public void showChatMessages() {
//        data.add(new Chat(getString(R.string.nickname1), R.drawable.contacts_1, "晚安", "2021/01/01"));
//        data.add(new Chat(getString(R.string.nickname2), R.drawable.contacts_2, "hhh好滴", "2021/01/02"));
//        data.add(new Chat(getString(R.string.nickname3), R.drawable.contacts_3, "OK", "2021/01/03"));
//        data.add(new Chat(getString(R.string.nickname4), R.drawable.contacts_4, "你到了吗", "2021/01/04"));
//        data.add(new Chat(getString(R.string.nickname5), R.drawable.contacts_5, "没事", "2021/01/05", 1));
        chatAdapter = new ChatAdapter(data,context);
        setListAdapter(chatAdapter);
    }

    public void getGroupChats() {
        HashMap<String, String> params = new HashMap<>();
        HttpRequest.sendOkHttpPostRequest("group/get", new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(getActivity().getApplicationContext(),R.string.network_error, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resStr = response.body().string();
                if (resStr.charAt(resStr.length()-1) != '}'){
                    resStr = resStr + "}";
                }
                Log.e("response", resStr);
                try {
                    JSONObject jsonObject = new JSONObject(resStr);
                    if (jsonObject.getBoolean("success")){
                        JSONArray groups = jsonObject.getJSONArray("groups");
                        for(int i=0;i<groups.length();i++) {
                            try {
                                JSONObject groupMessages = groups.getJSONObject(i).getJSONArray("groupMessages").getJSONObject(groups.getJSONObject(i).getJSONArray("groupMessages").length() - 1);
                                Log.d("12", groupMessages.toString());
                                if(isHttpUrl(groupMessages.getString("content")))
                                {
                                    data.add(new Chat(groups.getJSONObject(i).getString("name"), R.drawable.group_chat_avatar, "点击查看", "", 1, groups.getJSONObject(i).getString("id")));
                                }
                                else
                                {
                                    data.add(new Chat(groups.getJSONObject(i).getString("name"), R.drawable.group_chat_avatar, groupMessages.getString("content"), "", 1, groups.getJSONObject(i).getString("id")));
                                }
                            }
                            catch (JSONException e)
                            {
                                data.add(new Chat(groups.getJSONObject(i).getString("name"), R.drawable.group_chat_avatar, "", "", 1, groups.getJSONObject(i).getString("id")));
                            }
                        }
                        handler_group_chats.sendEmptyMessage(1);
                    }else{
                        Looper.prepare();
                        Toast.makeText(getActivity().getApplicationContext(),"群聊消息获取失败", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                        handler_group_chats.sendEmptyMessage(1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
//                    Looper.prepare();
//                    Toast.makeText(getActivity().getApplicationContext(),R.string.json_parse_error, Toast.LENGTH_SHORT).show();
//                    Looper.loop();
                }
            }
        }, params);
    }

    public void getChatMessages() {
        for(int i=0;i<contacts_id.size();i++)
        {
            HashMap<String, String> params = new HashMap<>();
            params.put("talkToUserId", contacts_id.get(i));
            String talkToId_tem = contacts_id.get(i);
            String talkToName_tem = contacts_name.get(i);
            Log.e("request", contacts_id.get(i)+contacts_name.get(i));

            HttpRequest.sendOkHttpPostRequest("chat/get", new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Looper.prepare();
                    Toast.makeText(getActivity().getApplicationContext(),R.string.network_error, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String resStr = response.body().string();
                    if (resStr.charAt(resStr.length()-1) != '}'){
                        resStr = resStr + "}";
                    }
                    Log.e("response", resStr);
                    try {
                        JSONObject jsonObject = new JSONObject(resStr);
                        if (jsonObject.getBoolean("success")){
                            JSONArray messages = jsonObject.getJSONArray("messages");
                            if(messages.length()>0) {
                                data.add(new Chat(talkToName_tem, R.drawable.contacts_1, messages.getJSONObject(messages.length() - 1).getString("content"), formatTime(messages.getJSONObject(messages.length() - 1).getString("createdAt")), talkToId_tem));
//                            Log.d("messages", messages.toString());
                                handler_chats.sendEmptyMessage(1);
                            }
                        }else{
                            Looper.prepare();
                            Toast.makeText(getActivity().getApplicationContext(),"好友消息获取失败", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                            handler_chats.sendEmptyMessage(1);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
//                        Looper.prepare();
//                        Toast.makeText(getActivity().getApplicationContext(),R.string.json_parse_error, Toast.LENGTH_SHORT).show();
//                        Looper.loop();
                    }
                }
            }, params);
        }
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
//        SharedPreferences sp = mainActivity.getSharedPreferences(getString(R.string.store), Context.MODE_PRIVATE);
//        String username = sp.getString("username", "");
//        String password = sp.getString("password", "");
//        JSONObject ws_msg_login = new JSONObject();
//        try {
//            ws_msg_login.put("bizType", "USER_LOGIN");
//            ws_msg_login.put("password", password);
//            ws_msg_login.put("username", username);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        mainActivity.sendMsg(ws_msg_login.toString());
//        handler_ws.sendEmptyMessage(1);
//
//        handler_ws = new Handler(){
//            @SuppressLint("HandlerLeak")
//            public void handleMessage(Message msg){
//                super.handleMessage(msg);
//                getGroupChats();
//            }
//        };

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        MessagesFragment messagesFragment = new MessagesFragment();
        messagesFragment.setInfo(data.get(position).getChatType(), data.get(position).getId(), data.get(position).getNickname());
        transaction.replace(R.id.flFragment, messagesFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        TextView title = getActivity().findViewById(R.id.title_text);
        title.setText(data.get(position).getNickname());
//        data.get(position).setLastSpeak("hhh加油呀");
//        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
//        String createdate = sdf.format(date);
//        data.get(position).setLastSpeakTime(createdate);


//        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) title.getLayoutParams();
//        lp.setMargins(0,0,180, 0);
//        title.setLayoutParams(lp);
        Log.d("position", String.valueOf(position));
    }

    private String formatTime(String timeMillis) {
        long timeMillisl=Long.parseLong(timeMillis);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(timeMillisl);
        return simpleDateFormat.format(date);
    }

    public static boolean isHttpUrl(String urls) {
        boolean isurl = false;
        String regex = "(((https|http)?://)?([a-z0-9]+[.])|(www.))"
                + "\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;\u4E00-\u9FA5]]+)+)?([.][a-z0-9]{0,}+|/?)";//设置正则表达式

        Pattern pat = Pattern.compile(regex.trim());//比对
        Matcher mat = pat.matcher(urls.trim());
        isurl = mat.matches();//判断是否匹配
        if (isurl) {
            isurl = true;
        }
        return isurl;
    }

}