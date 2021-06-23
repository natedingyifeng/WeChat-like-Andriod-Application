package com.dyf.andriod_frontend;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dyf.andriod_frontend.groupInfo.GroupInfoAdapter;
import com.dyf.andriod_frontend.groupInfo.groupInfo;
import com.dyf.andriod_frontend.groupMemberIcons.Icons;
import com.dyf.andriod_frontend.groupMemberIcons.IconsAdapter;
import com.dyf.andriod_frontend.utils.HttpRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import butterknife.BindView;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import android.os.Handler;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroupInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupInfoFragment extends Fragment {
    private IconsAdapter iconsAdapter;
    private GroupInfoAdapter groupInfoAdapter;
    private LinkedList<Icons> data_icons;
    private LinkedList<groupInfo> data_groupInfos;
    private RecyclerView recyclerView;
    private ListView listView;
    private int chat_type;
    private String message_name;
    private String group_id;
    private Handler handler_group_chats;
    private Handler handler_leave;
    private Button button_add;
    private Button button_leave;
    private Button button_history;
    private String user_id;
    private int chattype;
    private String taketoname;

    @BindView(R.id.bottomNavigationView)
    public BottomNavigationView bottomNavigationView;

    public GroupInfoFragment() {
        // Required empty public constructor
    }

    public void saveMessageInfo(int checktype, String name, String id) {
        this.chattype = checktype;
        this.taketoname = name;
        this.group_id = id;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MessageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupInfoFragment newInstance() {
        GroupInfoFragment fragment = new GroupInfoFragment();
        return fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        bottomNavigationView = (MainActivity) getActivity().getMenu();
//        bottomNavigationView.animate().translationY(bottomNavigationView.getHeight());
//        getActivity().getWindow().setNavigationBarColor(Color.TRANSPARENT);
        TextView title = getActivity().findViewById(R.id.title_text);
        title.setText("聊天信息");
        listView = getView().findViewById(R.id.groupinfo_listview);
        recyclerView = getView().findViewById(R.id.member_icons);
        Button title_back = getActivity().findViewById(R.id.title_back);
        title_back.setVisibility(View.VISIBLE);
        Button title_back_2 = getActivity().findViewById(R.id.title_back2);
        title_back_2.setVisibility(View.INVISIBLE);
        Context context = getActivity();
        // 向ListView 添加数据，新建ChatAdapter，并向listView绑定该Adapter
        // 添加数据的样例代码如下:
        // data = new LinkedList<>();
        // data.add(new Chat(getString(R.string.nickname1), R.drawable.avatar1, getString(R.string.sentence1), "2021/01/01"));
        // data.add(new Chat(getString(R.string.nickname2), R.drawable.avatar2, getString(R.string.sentence2), "2021/01/01"));
        // TODO
        data_icons = new LinkedList<>();

        HashMap<String, String> params = new HashMap<>();

        HttpRequest.sendOkHttpPostRequest("group/get", new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(getActivity().getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
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
                        int index=0;
                        for(int i=0;i<groups.length();i++) {
                            if(groups.getJSONObject(i).getString("id").equals(group_id))
                            {
                                index = i;
                            }
                        }
                        JSONArray groupMembers = groups.getJSONObject(index).getJSONArray("members");
                        for(int j=0;j<groupMembers.length();j++)
                        {
                            data_icons.add(new Icons(groupMembers.getJSONObject(j).getString("username"), groupMembers.getJSONObject(j).getString("avatarUrl")));
                        }
                        handler_group_chats.sendEmptyMessage(1);
                    } else {
                        Looper.prepare();
                        Toast.makeText(getActivity().getApplicationContext(), "群聊成员获取失败", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                        handler_group_chats.sendEmptyMessage(1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(getActivity().getApplicationContext(), R.string.json_parse_error, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }, params);

        SharedPreferences sp = getActivity().getSharedPreferences(getString(R.string.store), Context.MODE_PRIVATE);
        String username = sp.getString("username", "");
        HashMap<String, String> params_new = new HashMap<>();
        params_new.put("keyword", username);

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
                        user_id = user.getString("id");
                    }else{
                        Looper.prepare();
                        Toast.makeText(getActivity().getApplicationContext(),"好友列表获取失败", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
//                    Looper.prepare();
//                    Toast.makeText(getActivity().getApplicationContext(),R.string.json_parse_error, Toast.LENGTH_SHORT).show();
//                    Looper.loop();
                }
            }
        }, params_new);

        handler_group_chats = new Handler() {
            @SuppressLint("HandlerLeak")
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
//                data_icons.add(new Icons(R.drawable.contacts_more, 1));
                iconsAdapter = new IconsAdapter(data_icons, context);
                recyclerView.setAdapter(iconsAdapter);
                GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 5);
                recyclerView.setLayoutManager(layoutManager);
            }
        };

        data_groupInfos = new LinkedList<>();
//        data_groupInfos.add(new groupInfo("查找聊天内容"));
        groupInfoAdapter = new GroupInfoAdapter(data_groupInfos, context);
        listView.setAdapter(groupInfoAdapter);
//        listView.setSelection(listView.getBottom());

        Button titleBack = (Button) getActivity().findViewById(R.id.title_back);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                title.setText(taketoname);
                MessagesFragment messagesFragment = new MessagesFragment();
                messagesFragment.setInfo(chattype, taketoname, group_id);
                transaction.replace(R.id.flFragment, messagesFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        button_history = getActivity().findViewById(R.id.button_group_messages_history);
        button_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),GroupChatHistoryActivity.class);
                intent.putExtra("talkToId",group_id);
                intent.putExtra("talkToName",taketoname);
                startActivity(intent);
            }
        });
        button_add = getActivity().findViewById(R.id.button_addmorefriend);
        button_add .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                title.setText("添加联系人");
                AddNewGroupMemberFragment addgroupmemberFragment = new AddNewGroupMemberFragment();
                addgroupmemberFragment.setInfo(group_id);
                transaction.replace(R.id.flFragment, addgroupmemberFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        button_leave = getActivity().findViewById(R.id.button_leavegroup);
        button_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> params = new HashMap<>();
                params.put("leaveUserId", user_id);
                params.put("groupId", group_id);

                HttpRequest.sendOkHttpPostRequest("group/leave", new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Looper.prepare();
                        Toast.makeText(getActivity().getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String resStr = response.body().string();
                        Log.e("response", resStr);
                        try {
                            handler_leave.sendEmptyMessage(1);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Looper.prepare();
                            Toast.makeText(getActivity().getApplicationContext(), R.string.json_parse_error, Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                }, params);
            }
        });

        handler_leave = new Handler() {
            @SuppressLint("HandlerLeak")
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                title.setText("消息");
                ChatsFragment chatsFragment = new ChatsFragment();
                transaction.replace(R.id.flFragment, chatsFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        };

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        bottomNavigationView = activity.findViewById(R.id.bottomNavigationView);
//        bottomNavigationView.inflateMenu(R.menu.chat_editor);
        bottomNavigationView.setVisibility(View.GONE);
//        setHasOptionsMenu(false);
//        ButterKnife.bind(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.group_chat_info, container, false);
    }
}