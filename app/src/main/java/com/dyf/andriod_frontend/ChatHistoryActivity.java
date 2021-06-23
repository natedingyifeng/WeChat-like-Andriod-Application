package com.dyf.andriod_frontend;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.dyf.andriod_frontend.message.MessageA;
import com.dyf.andriod_frontend.message.MessageAdapter;
import com.dyf.andriod_frontend.moments.MomentsReleaseActivity;
import com.dyf.andriod_frontend.utils.HttpRequest;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

//import androidx.multidex.MultiDex;

public class ChatHistoryActivity extends AppCompatActivity {

    private ListView listView;
    private Handler handler_search;
    private Handler handler_chats;
    private Handler handler_remove;
    private LinkedList<MessageA> data;
    private String talkToId;
    private String talkToName;
    private MessageAdapter messageAdapter;
    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        this.talkToId = intent.getStringExtra("talkToId");
        this.talkToName = intent.getStringExtra("talkToName");
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.chat_history);
        TextView title = findViewById(R.id.title_text);
        title.setText("聊天历史");
        Button titleBack2 = findViewById(R.id.title_back2);
        titleBack2.setVisibility(View.INVISIBLE);
        Button titleBack = (Button) findViewById(R.id.title_back);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatHistoryActivity.this.finish();
            }
        });
        listView = (ListView) findViewById(R.id.message_history_listview);

        SharedPreferences sp = getSharedPreferences(getString(R.string.store), Context.MODE_PRIVATE);
        String username = sp.getString("username", "");
        HashMap<String, String> params = new HashMap<>();
        params.put("keyword", username);

        HttpRequest.sendOkHttpPostRequest("user/search", new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(getApplicationContext(),R.string.network_error, Toast.LENGTH_SHORT).show();
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
                        userId = user.getString("id");
                        handler_search.sendEmptyMessage(1);
                    }else{
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(),"好友列表获取失败", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                        handler_search.sendEmptyMessage(1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(),R.string.json_parse_error, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }, params);

        data = new LinkedList<>();
        handler_search = new Handler() {
            @SuppressLint("HandlerLeak")
            public void handleMessage(Message msg) {
                getChats();
            }
        };

        handler_chats = new Handler() {
            @SuppressLint("HandlerLeak")
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                messageAdapter = new MessageAdapter(data, ChatHistoryActivity.this);
                listView.setAdapter(messageAdapter);
                listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                                   final int position, long id) {
                        //定义AlertDialog.Builder对象，当长按列表项的时候弹出确认删除对话框
                        AlertDialog.Builder builder=new AlertDialog.Builder(ChatHistoryActivity.this);
                        builder.setMessage("确定删除?");
                        builder.setTitle("提示");

                        //添加AlertDialog.Builder对象的setPositiveButton()方法
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteSingleChat(data.get(position).getMsgId());
                                data.remove(position);
                                messageAdapter.notifyDataSetChanged();
                                Toast.makeText(getBaseContext(), "删除列表项", Toast.LENGTH_SHORT).show();
                            }
                        });

                        //添加AlertDialog.Builder对象的setNegativeButton()方法
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        builder.create().show();
                        return false;
                    }
                });
            }
        };
    }

    private void deleteSingleChat(String id) {
        IdentityHashMap<String, String> params = new IdentityHashMap<>();
        params.put("userId1", userId);
        params.put("userId2", talkToId);
        HashMap<String, List<String>> listParams = new HashMap<>();
        List<String> id_groups = new LinkedList<>();
        id_groups.add(id);
        listParams.put("messagesIds", id_groups);

        Log.d("list", id_groups.toString());

        try {
            HttpRequest.sendOkHttpPostRequest("chat/withdraw_id", new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(),R.string.network_error, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String resStr = response.body().string();
                    Log.e("response", resStr);
                    try {
                        JSONObject jsonObject = new JSONObject(resStr);
                        if (jsonObject.getBoolean("success")){
                            Looper.prepare();
                            Toast.makeText(getApplicationContext(),"删除成功", Toast.LENGTH_SHORT).show();
                        }else{
                            Looper.prepare();
                            Toast.makeText(getApplicationContext(),"添加失败", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(),R.string.json_parse_error, Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }
            }, params, listParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getChats() {
        HashMap<String, String> params = new HashMap<>();
        params.put("talkToUserId", talkToId);

        HttpRequest.sendOkHttpPostRequest("chat/get", new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resStr = response.body().string();
                Log.e("response", resStr);
                try {
                    JSONObject jsonObject = new JSONObject(resStr);
                    if (jsonObject.getBoolean("success")) {
                        JSONArray messages = jsonObject.getJSONArray("messages");
                        for (int j = 0; j < messages.length(); j++) {
                            int k = 0;
                            if (messages.getJSONObject(j).getString("messageType").equals("TEXT")) {
                                if (messages.getJSONObject(j).getJSONObject("fromUser").getString("username").equals(talkToName)) {
                                    k = 0;
                                } else {
                                    k = 1;
                                }
                                data.add(new MessageA(messages.getJSONObject(j).getJSONObject("fromUser").getString("username"), messages.getJSONObject(j).getJSONObject("fromUser").getString("avatarUrl"), messages.getJSONObject(j).getString("content"), k, messages.getJSONObject(j).getString("id")));
                            } else if (messages.getJSONObject(j).getString("messageType").equals("PHOTO")) {
                                if (messages.getJSONObject(j).getJSONObject("fromUser").getString("username").equals(talkToName)) {
                                    k = 2;
                                } else {
                                    k = 3;
                                }
                                data.add(new MessageA(messages.getJSONObject(j).getJSONObject("fromUser").getString("username"), messages.getJSONObject(j).getJSONObject("fromUser").getString("avatarUrl"), k, messages.getJSONObject(j).getString("content"), messages.getJSONObject(j).getString("id")));
                            } else if (messages.getJSONObject(j).getString("messageType").equals("VIDEO")) {
                                if (messages.getJSONObject(j).getJSONObject("fromUser").getString("username").equals(talkToName)) {
                                    k = 4;
                                } else {
                                    k = 5;
                                }
                                data.add(new MessageA(messages.getJSONObject(j).getJSONObject("fromUser").getString("username"), messages.getJSONObject(j).getJSONObject("fromUser").getString("avatarUrl"), k, messages.getJSONObject(j).getString("content"), messages.getJSONObject(j).getString("id")));
                            } else if (messages.getJSONObject(j).getString("messageType").equals("AUDIO")) {
                                if (messages.getJSONObject(j).getJSONObject("fromUser").getString("username").equals(talkToName)) {
                                    k = 6;
                                } else {
                                    k = 7;
                                }
                                data.add(new MessageA(messages.getJSONObject(j).getJSONObject("fromUser").getString("username"), messages.getJSONObject(j).getJSONObject("fromUser").getString("avatarUrl"), k, messages.getJSONObject(j).getString("content"), messages.getJSONObject(j).getString("id")));
                            }
                            else if (messages.getJSONObject(j).getString("messageType").equals("LOCATION")) {
                                if (messages.getJSONObject(j).getJSONObject("fromUser").getString("username").equals(talkToName)) {
                                    k = 8;
                                } else {
                                    k = 9;
                                }
                                data.add(new MessageA(messages.getJSONObject(j).getJSONObject("fromUser").getString("username"), messages.getJSONObject(j).getJSONObject("fromUser").getString("avatarUrl"), k, messages.getJSONObject(j).getString("content"), messages.getJSONObject(j).getString("id")));
                            }
                        }
                        handler_chats.sendEmptyMessage(1);
                    } else {
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(), "消息获取失败", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                        handler_chats.sendEmptyMessage(1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(), R.string.json_parse_error, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }, params);
    }

}
