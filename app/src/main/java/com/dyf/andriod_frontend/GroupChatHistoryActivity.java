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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dyf.andriod_frontend.message.MessageA;
import com.dyf.andriod_frontend.message.MessageAdapter;
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

public class GroupChatHistoryActivity extends AppCompatActivity {

    private ListView listView;
    private Handler handler_search;
    private Handler handler_group_chats;
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
        title.setText("????????????");
        Button titleBack2 = findViewById(R.id.title_back2);
        titleBack2.setVisibility(View.INVISIBLE);
        Button titleBack = (Button) findViewById(R.id.title_back);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupChatHistoryActivity.this.finish();
            }
        });
        listView = (ListView) findViewById(R.id.message_history_listview);

        data = new LinkedList<>();
        getChats();

        handler_group_chats = new Handler() {
            @SuppressLint("HandlerLeak")
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                messageAdapter = new MessageAdapter(data, GroupChatHistoryActivity.this);
                listView.setAdapter(messageAdapter);
                listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                                   final int position, long id) {
                        //??????AlertDialog.Builder???????????????????????????????????????????????????????????????
                        AlertDialog.Builder builder=new AlertDialog.Builder(GroupChatHistoryActivity.this);
                        builder.setMessage("?????????????");
                        builder.setTitle("??????");

                        //??????AlertDialog.Builder?????????setPositiveButton()??????
                        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteSingleChat(data.get(position).getMsgId());
                                data.remove(position);
                                messageAdapter.notifyDataSetChanged();
                                Toast.makeText(getBaseContext(), "???????????????", Toast.LENGTH_SHORT).show();
                            }
                        });

                        //??????AlertDialog.Builder?????????setNegativeButton()??????
                        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {

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
        params.put("groupId", talkToId);
        HashMap<String, List<String>> listParams = new HashMap<>();
        List<String> id_groups = new LinkedList<>();
        id_groups.add(id);
        listParams.put("messagesIds", id_groups);

        Log.d("list", id_groups.toString());

        try {
            HttpRequest.sendOkHttpPostRequest("group/withdraw", new Callback() {
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
                            Toast.makeText(getApplicationContext(),"????????????", Toast.LENGTH_SHORT).show();
                        }else{
                            Looper.prepare();
                            Toast.makeText(getApplicationContext(),"????????????", Toast.LENGTH_SHORT).show();
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
        Log.d("ID", talkToId);

        HttpRequest.sendOkHttpPostRequest("group/get", new Callback() {
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
                        JSONArray groups = jsonObject.getJSONArray("groups");
                        int index = 0;
                        for (int i = 0; i < groups.length(); i++) {
                            if (groups.getJSONObject(i).getString("id").equals(talkToId)) {
                                index = i;
                            }
                        }
                        try {
                            SharedPreferences sp = getSharedPreferences(getString(R.string.store), Context.MODE_PRIVATE);
                            String username = sp.getString("username", "");
                            JSONArray groupMessages = groups.getJSONObject(index).getJSONArray("groupMessages");
                            for (int j = 0; j < groupMessages.length(); j++) {
                                int k = 0;
                                if (groupMessages.getJSONObject(j).getString("messageType").equals("TEXT")) {
                                    if (groupMessages.getJSONObject(j).getJSONObject("sendUser").getString("username").equals(username)) {
                                        k = 1;
                                    } else {
                                        k = 0;
                                    }
                                    data.add(new MessageA(groupMessages.getJSONObject(j).getJSONObject("sendUser").getString("username"), groupMessages.getJSONObject(j).getJSONObject("sendUser").getString("avatarUrl"), groupMessages.getJSONObject(j).getString("content"), k, groupMessages.getJSONObject(j).getString("id")));
                                } else if (groupMessages.getJSONObject(j).getString("messageType").equals("PHOTO")) {
                                    if (groupMessages.getJSONObject(j).getJSONObject("sendUser").getString("username").equals(talkToName)) {
                                        k = 2;
                                    } else {
                                        k = 3;
                                    }
                                    data.add(new MessageA(groupMessages.getJSONObject(j).getJSONObject("sendUser").getString("username"), groupMessages.getJSONObject(j).getJSONObject("sendUser").getString("avatarUrl"), k, groupMessages.getJSONObject(j).getString("content"), groupMessages.getJSONObject(j).getString("id")));
                                } else if (groupMessages.getJSONObject(j).getString("messageType").equals("VIDEO")) {
                                    if (groupMessages.getJSONObject(j).getJSONObject("sendUser").getString("username").equals(talkToName)) {
                                        k = 4;
                                    } else {
                                        k = 5;
                                    }
                                    data.add(new MessageA(groupMessages.getJSONObject(j).getJSONObject("sendUser").getString("username"),groupMessages.getJSONObject(j).getJSONObject("sendUser").getString("avatarUrl"), k, groupMessages.getJSONObject(j).getString("content"), groupMessages.getJSONObject(j).getString("id")));
                                } else if (groupMessages.getJSONObject(j).getString("messageType").equals("AUDIO")) {
                                    if (groupMessages.getJSONObject(j).getJSONObject("sendUser").getString("username").equals(talkToName)) {
                                        k = 6;
                                    } else {
                                        k = 7;
                                    }
                                    data.add(new MessageA(groupMessages.getJSONObject(j).getJSONObject("sendUser").getString("username"), groupMessages.getJSONObject(j).getJSONObject("sendUser").getString("avatarUrl"), k, groupMessages.getJSONObject(j).getString("content"), groupMessages.getJSONObject(j).getString("id")));
                                }
                            }
                        } catch (JSONException e) {

                        }
                        handler_group_chats.sendEmptyMessage(1);
                    } else {
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(), "????????????????????????", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                        handler_group_chats.sendEmptyMessage(1);
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
