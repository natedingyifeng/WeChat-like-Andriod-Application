package com.dyf.andriod_frontend.Setting;

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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dyf.andriod_frontend.R;
import com.dyf.andriod_frontend.moments.MomentsReleaseActivity;
import com.dyf.andriod_frontend.utils.HttpRequest;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

public class SettingsPasswordModifyActivity extends AppCompatActivity {

    private String passwordPre;
    private String passwordNew;
    private EditText passwordPreEdit;
    private EditText passwordNewEdit;
    private Button btn;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_password_modify);

        passwordNewEdit = findViewById(R.id.password_new_edit);
        passwordPreEdit = findViewById(R.id.password_previous_edit);
        btn = findViewById(R.id.password_edit_upload_btn);

        // 设置标题栏
        if(getSupportActionBar()!= null){
            getSupportActionBar().hide();
        }
        TextView title = findViewById(R.id.title_text);
        title.setText("修改密码");
        Button titleBack2 = findViewById(R.id.title_back2);
        titleBack2.setVisibility(View.INVISIBLE);
        Button titleBack = (Button) findViewById(R.id.title_back);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsPasswordModifyActivity.this.finish();
            }
        });

        handler = new Handler(){
            @SuppressLint("HandlerLeak")
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                if (msg.what == 0){
                   SettingsPasswordModifyActivity.this.finish();
                }
            }
        };

        //设置监听器
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                passwordPre = passwordPreEdit.getText().toString();
                passwordNew = passwordNewEdit.getText().toString();

                SharedPreferences sp = getApplication().getSharedPreferences(getString(R.string.store), Context.MODE_PRIVATE);
                if(!sp.getString("password", "").equals(passwordPre)){
                    Toast.makeText(SettingsPasswordModifyActivity.this,"原密码错误", Toast.LENGTH_SHORT).show();
                    return;
                }

                HashMap<String, String> params = new HashMap<>();
                params.put("newPassword", passwordNew);

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
                                editor.putString("password", passwordNew);
                                editor.apply();

                                handler.sendEmptyMessage(0);
                                Looper.prepare();
                                Toast.makeText(getApplicationContext(),"修改成功", Toast.LENGTH_SHORT).show();
                                Looper.loop();
//                        Intent intent = new Intent(getApplicationContext(), SettingsFragment.class);
//                        startActivity(intent);

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

            }
        });
    }
}