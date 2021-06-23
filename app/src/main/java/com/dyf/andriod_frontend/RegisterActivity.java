package com.dyf.andriod_frontend;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.dyf.andriod_frontend.moments.MomentsReleaseActivity;
import com.dyf.andriod_frontend.utils.HttpRequest;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {
    private static final String LOG_TAG = "Register";
    private static final int WRITE_PERMISSION = 0x01;
    private static int IMAGE_REQUEST_CODE = 114;

    private Button registerBtn;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText passwordConfirmEditText;
    private EditText nicknameEditText;
    private ImageView avatarView;

    private String username;
    private String password;
    private String passwordConfirm;
    private String nickname;
    private String avatarUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);
        TextView title = findViewById(R.id.title_text);
        title.setText("注册");
        Button titleBack2 = findViewById(R.id.title_back2);
        titleBack2.setVisibility(View.INVISIBLE);
        Button titleBack = (Button) findViewById(R.id.title_back);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.this.finish();
            }
        });


        registerBtn = findViewById(R.id.register_btn);
        usernameEditText = findViewById(R.id.register_username_edit_text);
        passwordEditText = findViewById(R.id.register_password_edit_text);
        passwordConfirmEditText = findViewById(R.id.register_password_confirm_edit_text);
        nicknameEditText = findViewById(R.id.register_nickname_edit_text);
        avatarView = findViewById(R.id.register_avatar_view);

        avatarView.setImageResource(R.drawable.add2);

        // 注册监听器
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
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

    private void register(){
        username = usernameEditText.getText().toString();
        password = passwordEditText.getText().toString();
        passwordConfirm = passwordConfirmEditText.getText().toString();
        nickname = nicknameEditText.getText().toString();


        if(!password.equals(passwordConfirm)){
            Toast.makeText(this, R.string.password_confirm_error, Toast.LENGTH_SHORT).show();
        }else if(password.isEmpty()){
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
        }else if(username.isEmpty()){
            Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
        }else if(avatarUrl.isEmpty()){
            Toast.makeText(this,  "头像不能为空", Toast.LENGTH_SHORT).show();
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("nickname", nickname);
        params.put("avatar", avatarUrl);

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
                if (resStr.charAt(resStr.length()-1) != '}'){
                    resStr = resStr + "}";
                }
                Log.e("response", resStr);

                try {
                    JSONObject jsonObject = new JSONObject(resStr);
                    if(jsonObject.getBoolean("success")){
//                        Toast.makeText(getApplicationContext(), R.string.register_success, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
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
                if (resStr.charAt(resStr.length()-1) != '}'){
                    resStr = resStr + "}";
                }
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
