package com.dyf.andriod_frontend;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dyf.andriod_frontend.utils.HttpRequest;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    private Button registerBtn;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText passwordConfirmEditText;
    private EditText nicknameEditText;

    private String username;
    private String password;
    private String passwordConfirm;
    private String nickname;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(true);

        registerBtn = findViewById(R.id.register_btn);
        usernameEditText = findViewById(R.id.register_username_edit_text);
        passwordEditText = findViewById(R.id.register_password_edit_text);
        passwordConfirmEditText = findViewById(R.id.register_password_confirm_edit_text);
        nicknameEditText = findViewById(R.id.register_nickname_edit_text);

        // 注册监听器
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    private void register(){
        username = usernameEditText.getText().toString();
        password = passwordEditText.getText().toString();
        passwordConfirm = passwordConfirmEditText.getText().toString();
        nickname = nicknameEditText.getText().toString();

        if(!password.equals(passwordConfirm)){
            Toast.makeText(this, R.string.password_confirm_error, Toast.LENGTH_SHORT).show();
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("nickname", nickname);

        HttpRequest.sendOkHttpPostRequest("user/register", new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(RegisterActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resStr = response.body().string();
                Log.e("response", resStr);

                try {
                    JSONObject jsonObject = new JSONObject(resStr);
                    if(jsonObject.getBoolean("success")){
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(), R.string.register_success, Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }else{
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(), jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                        Looper.loop();
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
