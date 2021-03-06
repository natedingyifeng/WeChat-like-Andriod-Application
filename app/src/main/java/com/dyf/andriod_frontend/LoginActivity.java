package com.dyf.andriod_frontend;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.multidex.MultiDex;

import com.bumptech.glide.Glide;
import com.dyf.andriod_frontend.utils.HttpRequest;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import android.os.Handler;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;
    private TextView registerLink;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private String username;
    private String password;
    private ImageView imageView;
    private Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        MultiDex.install(this);
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.store), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.clear();
//        editor.commit();
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        Button title_back = findViewById(R.id.title_back);
        title_back.setVisibility(View.GONE);
        Button title_back_2 = findViewById(R.id.title_back2);
        title_back_2.setVisibility(View.GONE);

        loginButton = findViewById(R.id.login_btn);
        registerLink = findViewById(R.id.register_link_textView);
        usernameEditText = findViewById(R.id.username_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        imageView = findViewById(R.id.imageView);

        Glide.with(this).load("http://8.140.133.34:7423/media/image.jpg").into(imageView);

        SharedPreferences sp = getSharedPreferences(getString(R.string.store), MODE_PRIVATE);
        username = sp.getString("username", "");
        usernameEditText.setText(username);

        // ???????????????
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    private void login() {
        username = usernameEditText.getText().toString();
        password = passwordEditText.getText().toString();

        HashMap<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);

        HttpRequest.sendOkHttpPostRequest("user/login", new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(getApplicationContext(),R.string.network_error, Toast.LENGTH_SHORT).show();
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
                        // ???????????????????????????
                        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.store), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", username);
                        editor.putString("password", password);
                        editor.commit();

                        getSelfInfo();

                        Looper.prepare();
                        Toast.makeText(getApplicationContext(),"????????????", Toast.LENGTH_SHORT).show();
                        handler.sendEmptyMessage(1);
                        Looper.loop();
//                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                        startActivity(intent);
                        handler.sendEmptyMessage(1);

                    }else{
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(),R.string.username_or_password_error, Toast.LENGTH_SHORT).show();
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

        handler = new Handler(){
            @SuppressLint("HandlerLeak")
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
             }
        };
    }

    private void getSelfInfo(){
        SharedPreferences sp = getSharedPreferences(getString(R.string.store), Context.MODE_PRIVATE);
        String username = sp.getString("username", "");
        if(username == ""){
            Looper.prepare();
            Toast.makeText(getApplicationContext(),"????????????????????????", Toast.LENGTH_SHORT).show();
            Looper.loop();
            return;
        }
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
                if (resStr.charAt(resStr.length()-1) != '}'){
                    resStr = resStr + "}";
                }
                Log.e("response", resStr);
                try {
                    JSONObject jsonObject = new JSONObject(resStr);
                    if (jsonObject.getBoolean("success")){
                        // ??????????????????
                        JSONObject user = jsonObject.getJSONArray("users").getJSONObject(0);
                        // ???????????????????????????
                        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.store), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.putString("avatarUrl", user.getString("avatarUrl"));
                        editor.putString("nickname", user.getString("nickname"));
                        editor.putString("username", username);
                        editor.putString("password", password);
                        editor.putString("id", user.getString("id"));
                        if(user.has("slogan")){
                            editor.putString("slogan", user.getString("slogan"));
                        }else
                            editor.putString("slogan", "");
                        if(user.has("phoneNumber")){
                            editor.putString("phoneNumber", user.getString("phoneNumber"));
                        }else editor.putString("phoneNumber", "");
                        editor.apply();
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(),"????????????????????????", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }else{
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(),"????????????????????????", Toast.LENGTH_SHORT).show();
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

    private void register() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        LoginActivity.this.startActivity(intent);
    }

}
