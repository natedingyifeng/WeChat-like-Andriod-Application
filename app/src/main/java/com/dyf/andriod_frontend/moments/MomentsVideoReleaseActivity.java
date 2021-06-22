package com.dyf.andriod_frontend.moments;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dyf.andriod_frontend.R;
import com.dyf.andriod_frontend.utils.HttpRequest;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class MomentsVideoReleaseActivity extends AppCompatActivity {

    private static final String LOG_TAG = "moments release";
    private int imageCount = 0;
    private static final int WRITE_PERMISSION = 0x01;
    private static int IMAGE_REQUEST_CODE = 114;

    private String videoPath;

    private ImageView videoImageView;
    private Button releaseBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moments_video_release);
        if(getSupportActionBar()!= null){
            getSupportActionBar().hide();
        }

        TextView title = findViewById(R.id.title_text);
        title.setText("发布动态");
        Button titleBack2 = findViewById(R.id.title_back2);
        titleBack2.setVisibility(View.INVISIBLE);
        Button titleBack = (Button) findViewById(R.id.title_back);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MomentsVideoReleaseActivity.this.finish();
            }
        });


        releaseBtn = findViewById(R.id.moments_release_upload_btn);
        videoImageView = findViewById(R.id.moments_release_video_image_view);

        videoImageView.setImageResource(R.drawable.add2);



        releaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    upload();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        videoImageView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                requestWritePermission();
                getVideo();
            }
        });


    }

    private void getVideo(){

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
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
                    videoPath = cursor.getString(columnIndex);
                    cursor.close();
                    File file = new File(videoPath);
                    Glide
                            .with(this)
                            .load(file)
                            .into(videoImageView);

                    try {
                        uploadFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

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

    private void upload() throws IOException, JSONException {
        String postType = "VIDEO";
        String content = ((EditText)findViewById(R.id.moments_text_edit)).getText().toString();
        IdentityHashMap<String, String> params = new IdentityHashMap<>();
        params.put("postType", postType);
        params.put("content", content);

        HashMap<String, List<String>> listParams = new HashMap<>();
        List<String> list = new ArrayList<>();
        list.add(videoPath);
        listParams.put("filePaths", list);

        HttpRequest.sendOkHttpPostRequest("post/new", new okhttp3.Callback() {
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
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(), "发布成功", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                        MomentsVideoReleaseActivity.this.finish();
                    }else{
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(),"发布错误", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(),R.string.json_parse_error, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }, params, listParams);
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
                        videoPath = jsonObject.getString("fileName");
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(), "视频上传成功", Toast.LENGTH_SHORT).show();
                    }else{
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(),"视频错误", Toast.LENGTH_SHORT).show();
                    }
                    Looper.loop();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(),R.string.json_parse_error, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

            }
        }, videoPath);
    }
}