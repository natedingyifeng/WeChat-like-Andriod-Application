package com.dyf.andriod_frontend.user;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dyf.andriod_frontend.R;
import com.dyf.andriod_frontend.contact.Contact;
import com.dyf.andriod_frontend.utils.HttpRequest;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UserInfoActivity extends AppCompatActivity {

    private String username;
    private String nickname;
    private String slogan;
    private String phoneNumber;
    private String avatarUrl;
    private String id;

    ImageView avatarView;
    TextView nicknameView;
    TextView usernameView;
    TextView sloganView;
    TextView phoneNumberView;
    Button deleteButton;
    Button addButton;

    Handler handler;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        // 隐藏标题栏
        if(getSupportActionBar()!= null){
            getSupportActionBar().hide();
        }
        TextView title = findViewById(R.id.title_text);
        title.setText("个人信息");
        Button titleBack2 = findViewById(R.id.title_back2);
        titleBack2.setVisibility(View.INVISIBLE);
        Button titleBack = (Button) findViewById(R.id.title_back);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfoActivity.this.finish();
            }
        });

        avatarView = findViewById(R.id.user_avatar_image_view);
        nicknameView = findViewById(R.id.user_nickname_text_view);
        usernameView = findViewById(R.id.user_username_text_view);
        sloganView = findViewById(R.id.users_slogan_text_view);
        phoneNumberView = findViewById(R.id.user_phone_number_text_view);
        deleteButton = findViewById(R.id.user_delete_friend_btn);
        addButton = findViewById(R.id.user_add_friend_btn);
        addButton.setVisibility(View.INVISIBLE);

        if(getIntent()!=null){
            username = getIntent().getStringExtra("username");
        }


        handler = new Handler(){
            @SuppressLint("HandlerLeak")
            public void handleMessage(Message msg){
                if (msg.what == 0){
                    setData();
                }else if (msg.what == 1){
                    deleteButton.setVisibility(View.INVISIBLE);
                }
            }
        };

        getInfo();

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFriend();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    private void getInfo(){
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

                        nickname = user.getString("nickname");
                        username = user.getString("username");
                        avatarUrl = user.getString("avatarUrl");
                        id = user.getString("id");
                        if(user.has("slogan"))
                            slogan = user.getString("slogan");
                        if(user.has("phoneNumber"))
                            phoneNumber = user.getString("phoneNumber");

                        handler.sendEmptyMessage(0);

                        checkIsFriend();

                        Looper.prepare();
                        Toast.makeText(getApplicationContext(),"用户信息获取成功", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }else{
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(),"用户信息获取失败", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(),R.string.json_parse_error, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }, params);
    }

    private void setData(){
        phoneNumberView.setText(phoneNumber);
        nicknameView.setText(nickname);
        usernameView.setText(username);
        sloganView.setText(slogan);
        avatarUrl = HttpRequest.media_url+ avatarUrl;
        Glide.with(this).load(avatarUrl).into(avatarView);
    }

    private void deleteFriend(){
        HashMap<String, String > params = new HashMap<>();
        params.put("friendIdToRemove", id);

        HttpRequest.sendOkHttpPostRequest("user/delete_friend", new Callback() {
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
                        handler.sendEmptyMessage(1);
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(),"删除成功", Toast.LENGTH_SHORT).show();
                        Looper.loop();

                    }else{
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(),"删除失败", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(),R.string.json_parse_error, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }, params);
    }

    private void addFriend(){

    }

    private void checkIsFriend(){
        SharedPreferences sp = getSharedPreferences(getString(R.string.store), MODE_PRIVATE);
        HashMap<String, String> params = new HashMap<>();
        params.put("keyword", sp.getString("username", ""));

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
                        try {
                            JSONArray friends = user.getJSONArray("contacts");
                            Log.d("len", friends.getJSONObject(0).getString("username") + "(" + friends.getJSONObject(0).getString("nickname") + ")");
                            for (int i = 0; i < friends.length(); i++) {
                                if(friends.getJSONObject(i).getString("username").equals(username)){
                                    return;
                                }
                            }
                        } catch (JSONException e) {}
                        handler.sendEmptyMessage(1);
                    }else{
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(),"好友列表获取失败", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                        handler.sendEmptyMessage(1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(),R.string.json_parse_error, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }, params);
    }

}