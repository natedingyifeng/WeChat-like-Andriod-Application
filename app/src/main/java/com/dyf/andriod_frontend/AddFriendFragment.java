package com.dyf.andriod_frontend;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dyf.andriod_frontend.groupInfo.GroupInfoAdapter;
import com.dyf.andriod_frontend.groupInfo.groupInfo;
import com.dyf.andriod_frontend.groupMemberIcons.Icons;
import com.dyf.andriod_frontend.groupMemberIcons.IconsAdapter;
import com.dyf.andriod_frontend.user.UserInfoActivity;
import com.dyf.andriod_frontend.utils.HttpRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddFriendFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddFriendFragment extends Fragment {
    private Button add_friend_button;
    private Button search_friend_button;
    private TextView targetNameText;
    private ImageView avatarView;
    private EditText add_friend_name;
    private TextView my_account;

    private String friendNickname = "";
    private String friendUsername = "";
    private String friendAvatarUrl = "";

    private Handler handler;

    @BindView(R.id.bottomNavigationView)
    public BottomNavigationView bottomNavigationView;

    public AddFriendFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MessageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddFriendFragment newInstance() {
        AddFriendFragment fragment = new AddFriendFragment();
        return fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        bottomNavigationView = (MainActivity) getActivity().getMenu();
//        bottomNavigationView.animate().translationY(bottomNavigationView.getHeight());
//        getActivity().getWindow().setNavigationBarColor(Color.TRANSPARENT);

        TextView title = getActivity().findViewById(R.id.title_text);
        title.setText("添加好友");
        Button title_back = getActivity().findViewById(R.id.title_back);
        title_back.setVisibility(View.VISIBLE);
        Button title_back_2 = getActivity().findViewById(R.id.title_back2);
        title_back_2.setVisibility(View.INVISIBLE);
        Context context = getActivity();
        Button titleBack = (Button) getActivity().findViewById(R.id.title_back);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                title.setText("通讯录");
                ContactsFragment contactsFragment = new ContactsFragment();
                transaction.replace(R.id.flFragment, contactsFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        handler = new Handler(){
            @SuppressLint("HandlerLeak")
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                setData();
            }
        };

        add_friend_button = getActivity().findViewById(R.id.button_addfriend);
        add_friend_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendAddFriendRequest();
            }
        });

        search_friend_button = getActivity().findViewById(R.id.button_searchfriend);
        search_friend_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriend();
            }
        });

        add_friend_name = getActivity().findViewById(R.id.add_friend_edit);
        my_account = getActivity().findViewById(R.id.my_account_text);
        SharedPreferences sp = getActivity().getSharedPreferences(getString(R.string.store), Context.MODE_PRIVATE);
        my_account.setText("我的用户名：" + sp.getString("username", ""));
        avatarView = getActivity().findViewById(R.id.add_friend_avatar);
        targetNameText = getActivity().findViewById(R.id.add_friend_target_name_text);

        avatarView.setClickable(false);

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
        return inflater.inflate(R.layout.add_friends_dialog, container, false);
    }


    private void addFriend(){
        String inputFriendUsername = add_friend_name.getText().toString();
        HashMap<String, String> params = new HashMap<>();
        params.put("keyword", inputFriendUsername);

        HttpRequest.sendOkHttpPostRequest("user/search", new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(getActivity(),R.string.network_error, Toast.LENGTH_SHORT).show();
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
                        JSONArray userArray = jsonObject.getJSONArray("users");
                        if(userArray.length() == 0){
                            Looper.prepare();
                            Toast.makeText(getActivity(),"未找到用户", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                            return;
                        }

                        JSONObject user = userArray.getJSONObject(0);

                        friendNickname = user.getString("nickname");
                        friendUsername = user.getString("username");
                        friendAvatarUrl = user.getString("avatarUrl");

                        if(friendUsername.equals(inputFriendUsername)){
                            handler.sendEmptyMessage(1);
                            Looper.prepare();
                            Toast.makeText(getActivity(),"搜索成功", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }

                    }else{
                        Looper.prepare();
                        Toast.makeText(getActivity(),"用户信息获取失败", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(getActivity(),R.string.json_parse_error, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }, params);
    }

    private void sendAddFriendRequest(){
        SharedPreferences sp = getActivity().getSharedPreferences(getString(R.string.store), Context.MODE_PRIVATE);
        HashMap<String, String> params = new HashMap<>();
        params.put("id", sp.getString("id", ""));
        params.put("remark", "你好，我是"+ sp.getString("nickname", "王阿姨介绍的") + "。");
        params.put("targetFriendName", friendUsername);

        HttpRequest.sendOkHttpPostRequest("user/request", new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(getActivity(),R.string.network_error, Toast.LENGTH_SHORT).show();
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
                        Looper.prepare();
                        Toast.makeText(getActivity(),"发送请求成功", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }else{
                        Looper.prepare();
                        Toast.makeText(getActivity(),"发送请求失败", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(getActivity(),R.string.json_parse_error, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }, params);
    }

    private void setData(){
        avatarView.setClickable(true);
        avatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UserInfoActivity.class);
                intent.putExtra("username", friendUsername);
                startActivity(intent);
            }
        });
        Glide.with(getActivity())
                .load(HttpRequest.media_url + friendAvatarUrl)
                .into(avatarView);
        targetNameText.setText(friendNickname);
    }
}