package com.dyf.andriod_frontend.Setting;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.dyf.andriod_frontend.LoginActivity;
import com.dyf.andriod_frontend.MainActivity;
import com.dyf.andriod_frontend.R;
import com.dyf.andriod_frontend.moments.MomentsReleaseActivity;
import com.dyf.andriod_frontend.utils.HttpRequest;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

public class SettingsModifyActivity extends AppCompatActivity {
    private static final String LOG_TAG = "SettingModifyActivity";
    private static final int WRITE_PERMISSION = 0x01;
    private static int IMAGE_REQUEST_CODE = 114;

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
    private String avatarUrl;

    private String nickname_previous;
    private String username_previous;
    private String phone_previous;
    private String slogan_previous;

    private Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_settings_edit);

        avatarView = findViewById(R.id.settings_avatar_image_view);
        nicknameView = findViewById(R.id.settings_nickname_text_view);
        usernameView = findViewById(R.id.settings_username_text_view);

        // 设置标题栏
        if(getSupportActionBar()!= null){
            getSupportActionBar().hide();
        }

        TextView title = findViewById(R.id.title_text);
        title.setText("修改信息");
        Button titleBack2 = findViewById(R.id.title_back2);
        titleBack2.setVisibility(View.INVISIBLE);
        Button titleBack = (Button) findViewById(R.id.title_back);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsModifyActivity.this.finish();
            }
        });

        handler = new Handler(){
            @SuppressLint("HandlerLeak")
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                if (msg.what == 0){
                    SettingsModifyActivity.this.finish();
                }
            }
        };

        SharedPreferences sp = getSharedPreferences(getString(R.string.store), Context.MODE_PRIVATE);

        avatarUrl = sp.getString("avatarUrl", "");
        Glide.with(this).load(HttpRequest.media_url + avatarUrl).into(avatarView);

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

        avatarView.setClickable(true);
        avatarView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                requestWritePermission();
                getPicture();
            }
        });
    }

    private void saveInfo(){
//        username = usernameEditText.getText().toString();
        nickname = nicknameEditText.getText().toString();
        phone = PhoneEditText.getText().toString();
        slogan = SloganEditText.getText().toString();

        HashMap<String, String> params = new HashMap<>();
        params.put("newNickname", nickname);
        params.put("newPhoneNumber", phone);
        params.put("newSlogan", slogan);
        params.put("newAvatar", avatarUrl);

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
                        editor.putString("avatarUrl",avatarUrl);
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



//        TextView sloganView = findViewById(R.id.settings_slogan_text_view);
//        TextView phoneNumberView = findViewById(R.id.settings_phone_number_text_view);
//        nicknameView.setText(username);
//        usernameView.setText(ID);
//        phoneNumberView.setText(phone);
//        sloganView.setText(slogan);

    }

    private void getPicture(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAGE_REQUEST_CODE) {
            if(resultCode == RESULT_OK){
                try {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    avatarUrl = cursor.getString(columnIndex);
                    cursor.close();
                    File file = new File(avatarUrl);
                    Glide
                            .with(this)
                            .load(file)
                            .into(avatarView);
                    uploadFile();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if(requestCode == WRITE_PERMISSION){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(LOG_TAG, "Write Permission Failed");
                Toast.makeText(this, "You must allow permission write external storage to your mobile device.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestWritePermission(){
        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},WRITE_PERMISSION);
        }
    }

    private void uploadFile() throws IOException {
        HttpRequest.sendOkHttpUploadFile("file/upload", new okhttp3.Callback() {
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
                        avatarUrl = jsonObject.getString("fileName");

                        Looper.prepare();
                        Toast.makeText(getApplicationContext(), "图片上传成功", Toast.LENGTH_SHORT).show();
                    }else{
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(),"图片错误", Toast.LENGTH_SHORT).show();
                    }
                    Looper.loop();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(),R.string.json_parse_error, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

            }
        }, avatarUrl);
    }

}
