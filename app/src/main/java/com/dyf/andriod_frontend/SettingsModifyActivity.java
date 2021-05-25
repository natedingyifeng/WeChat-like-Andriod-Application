package com.dyf.andriod_frontend;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
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

import com.bumptech.glide.Glide;
import com.dyf.andriod_frontend.utils.HttpRequest;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SettingsModifyActivity extends AppCompatActivity {

    private Button confirmBtn;
    private EditText usernameEditText;
    private EditText IDEditText;
    private EditText PhoneEditText;
    private EditText SloganEditText;
    private ImageView avatarView;
    private TextView nicknameView;
    private TextView usernameView;

    private String username;
    private String ID;
    private String phone;
    private String slogan;

    private String username_previous;
    private String ID_previous;
    private String phone_previous;
    private String slogan_previous;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_settings_edit);

        avatarView = findViewById(R.id.settings_avatar_image_view);
        nicknameView = findViewById(R.id.settings_nickname_text_view);
        usernameView = findViewById(R.id.settings_username_text_view);

        Glide.with(this).load(getString(R.string.test_user_avatar_url)).into(avatarView);

        SharedPreferences sp = getSharedPreferences(getString(R.string.store), Context.MODE_PRIVATE);

        ID_previous = sp.getString("username", "yihao_xu");
        usernameView.setText(ID_previous);
        username_previous = sp.getString("nickname", "九月的南瓜");
        nicknameView.setText(username_previous);
        phone_previous = sp.getString("phoneNumber", "15808901623");
        slogan_previous = sp.getString("slogan", "为祖国健康工作五十年！");

        confirmBtn = findViewById(R.id.settings_modify_confirm_btn);
        usernameEditText = findViewById(R.id.editTextTextPersonName4);
        usernameEditText.setText(username_previous);
        IDEditText = findViewById(R.id.editTextTextPersonName3);
        IDEditText.setText(ID_previous);
        PhoneEditText = findViewById(R.id.editTextPhone_settings_edit);
        PhoneEditText.setText(phone_previous);
        SloganEditText = findViewById(R.id.editTextTextPersonName2);
        SloganEditText.setText(slogan_previous);

        // 注册监听器
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInfo();
                Intent intent = new Intent(SettingsModifyActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void saveInfo(){
        username = usernameEditText.getText().toString();
        ID = IDEditText.getText().toString();
        phone = PhoneEditText.getText().toString();
        slogan = SloganEditText.getText().toString();

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.store), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", ID);
        editor.putString("nickname", username);
        editor.putString("phoneNumber", phone);
        editor.putString("slogan", slogan);
        editor.commit();

//        TextView sloganView = findViewById(R.id.settings_slogan_text_view);
//        TextView phoneNumberView = findViewById(R.id.settings_phone_number_text_view);
//        nicknameView.setText(username);
//        usernameView.setText(ID);
//        phoneNumberView.setText(phone);
//        sloganView.setText(slogan);

    }




}
