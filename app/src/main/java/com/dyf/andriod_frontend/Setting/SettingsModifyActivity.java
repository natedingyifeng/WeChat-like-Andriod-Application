package com.dyf.andriod_frontend.Setting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.dyf.andriod_frontend.LoginActivity;
import com.dyf.andriod_frontend.MainActivity;
import com.dyf.andriod_frontend.R;
import com.dyf.andriod_frontend.utils.HttpRequest;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

public class SettingsModifyActivity extends AppCompatActivity {

    private Button confirmBtn;
    private EditText nicknameEditText;
    private EditText usernameEditText;
    private EditText PhoneEditText;
    private EditText SloganEditText;
    private ImageView avatarView;
    private TextView nicknameView;
    private TextView usernameView;

    private String username;
    private String nickname;
    private String phone;
    private String slogan;

    private String nickname_previous;
    private String username_previous;
    private String phone_previous;
    private String slogan_previous;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_settings_edit);

        avatarView = findViewById(R.id.settings_avatar_image_view);
        nicknameView = findViewById(R.id.settings_nickname_text_view);
        usernameView = findViewById(R.id.settings_username_text_view);



        SharedPreferences sp = getSharedPreferences(getString(R.string.store), Context.MODE_PRIVATE);

        Glide.with(this).load(HttpRequest.media_url + sp.getString("avatarUrl", "")).into(avatarView);

        username_previous = sp.getString("username", "获取失败");
        usernameView.setText(username_previous);

        nickname_previous = sp.getString("nickname", "获取失败");
        nicknameView.setText(nickname_previous);

        phone_previous = sp.getString("phoneNumber", "");
        slogan_previous = sp.getString("slogan", "");

        confirmBtn = findViewById(R.id.settings_modify_confirm_btn);

        nicknameEditText = findViewById(R.id.setting_edit_nickname);
        nicknameEditText.setText(nickname_previous);

        PhoneEditText = findViewById(R.id.editTextPhone_settings_edit);
        PhoneEditText.setText(phone_previous);

        SloganEditText = findViewById(R.id.editTextTextPersonName2);
        SloganEditText.setText(slogan_previous);

        // 注册监听器
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInfo();
//                Intent intent = new Intent(SettingsModifyActivity.this, MainActivity.class);
//                startActivity(intent);
            }
        });
    }

    private void close(){
        this.finish();
    }

    private void saveInfo(){
        username = usernameEditText.getText().toString();
        nickname = nicknameEditText.getText().toString();
        phone = PhoneEditText.getText().toString();
        slogan = SloganEditText.getText().toString();

        HashMap<String, String> params = new HashMap<>();
        params.put("newNickname", nickname);
        params.put("newPhoneNumber", phone);
        params.put("newSlogan", slogan);

        HttpRequest.sendOkHttpPostRequest("user/update", new okhttp3.Callback() {
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
                        // 将用户数据存入本地
                        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.store), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("nickname", nickname);
                        editor.putString("phoneNumber", phone);
                        editor.putString("slogan", slogan);
                        editor.apply();



                        Looper.prepare();
                        Toast.makeText(getApplicationContext(),"修改成功", Toast.LENGTH_SHORT).show();
                        Looper.loop();

//                        Intent intent = new Intent(getApplicationContext(), SettingsFragment.class);
//                        startActivity(intent);

                        close();

                    }else{
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(), "修改失败", Toast.LENGTH_SHORT).show();
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



//        TextView sloganView = findViewById(R.id.settings_slogan_text_view);
//        TextView phoneNumberView = findViewById(R.id.settings_phone_number_text_view);
//        nicknameView.setText(username);
//        usernameView.setText(ID);
//        phoneNumberView.setText(phone);
//        sloganView.setText(slogan);

    }




}
