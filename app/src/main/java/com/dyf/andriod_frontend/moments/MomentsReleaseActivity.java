package com.dyf.andriod_frontend.moments;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dyf.andriod_frontend.LoginActivity;
import com.dyf.andriod_frontend.MainActivity;
import com.dyf.andriod_frontend.R;
import com.dyf.andriod_frontend.utils.HttpRequest;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class MomentsReleaseActivity extends AppCompatActivity {

    private static final String LOG_TAG = "moments release";
    private int imageCount = 0;
    private static final int WRITE_PERMISSION = 0x01;
    private static int IMAGE_REQUEST_CODE = 114;
    private ImageView imageView[] = new ImageView[9];
    private List<String> filePaths = new ArrayList<>();
    private String imagePath[] = new String[9];
    private Button releaseBtn;
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moments_release);
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
                MomentsReleaseActivity.this.finish();
            }
        });


        imageView[0]= findViewById(R.id.moments_release_imageView1);
        imageView[1]= findViewById(R.id.moments_release_imageView2);
        imageView[2]= findViewById(R.id.moments_release_imageView3);
        imageView[3]= findViewById(R.id.moments_release_imageView4);
        imageView[4]= findViewById(R.id.moments_release_imageView5);
        imageView[5]= findViewById(R.id.moments_release_imageView6);
        imageView[6]= findViewById(R.id.moments_release_imageView7);
        imageView[7]= findViewById(R.id.moments_release_imageView8);
        imageView[8]= findViewById(R.id.moments_release_imageView9);

        releaseBtn = findViewById(R.id.moments_release_upload_btn);


        handler = new Handler(){
            @SuppressLint("HandlerLeak")
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                if (msg.what == 0){
                    MomentsReleaseActivity.this.finish();
                }
            }
        };

        imageView[0].setImageResource(R.drawable.add2);

        TableRow row1 = findViewById(R.id.moments_release_row1);
        row1.setMinimumHeight(300);

        imageView[0].setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if(imageCount == 0) {
                    requestWritePermission();
                    getPicture();
                }
            }
        });
        imageView[1].setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if(imageCount == 1) {
                    requestWritePermission();
                    getPicture();
                }
            }
        });
        imageView[2].setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if(imageCount == 2) {
                    requestWritePermission();
                    getPicture();
                }
            }
        });
        imageView[3].setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if(imageCount == 3) {
                    requestWritePermission();
                    getPicture();
                }
            }
        });
        imageView[4].setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if(imageCount == 4) {
                    requestWritePermission();
                    getPicture();
                }
            }
        });
        imageView[5].setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if(imageCount == 5) {
                    requestWritePermission();
                    getPicture();
                }
            }
        });
        imageView[6].setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if(imageCount == 6) {
                    requestWritePermission();
                    getPicture();
                }
            }
        });
        imageView[7].setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if(imageCount == 7) {
                    requestWritePermission();
                    getPicture();
                }
            }
        });
        imageView[8].setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if(imageCount == 8) {
                    requestWritePermission();
                    getPicture();
                }
            }
        });

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


    }

    private void getPicture(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_REQUEST_CODE);

//        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(intent, IMAGE_REQUEST_CODE);
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
                    imagePath[imageCount] = cursor.getString(columnIndex);
                    cursor.close();
                    File file = new File(imagePath[imageCount]);
                    Glide
                            .with(this)
                            .load(file)
                            .into(imageView[imageCount]);
                    imageCount++;
                    if(imageCount == 3) {
                        TableRow row2 = findViewById(R.id.moments_release_row2);
                        row2.setMinimumHeight(300);
                    }
                    if(imageCount == 6) {
                        TableRow row3 = findViewById(R.id.moments_release_row3);
                        row3.setMinimumHeight(300);
                    }
                    if(imageCount != 9)
                        imageView[imageCount].setImageResource(R.drawable.add2);

                    try {
                        uploadFile(imageCount - 1);
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
        String postType = "PHOTO";
        String content = ((EditText)findViewById(R.id.moments_text_edit)).getText().toString();
        IdentityHashMap<String, String> params = new IdentityHashMap<>();
        params.put("postType", postType);
        params.put("content", content);
//        params.put("filePaths", filePaths.toString());

        HashMap<String, List<String>> listParams = new HashMap<>();
        listParams.put("filePaths", filePaths);

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
                if (resStr.charAt(resStr.length()-1) != '}'){
                    resStr = resStr + "}";
                }
                Log.e("response", resStr);
                try {
                    JSONObject jsonObject = new JSONObject(resStr);
                    if (jsonObject.getBoolean("success")){
                        handler.sendEmptyMessage(0);
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(), "发布成功", Toast.LENGTH_SHORT).show();
                        Looper.loop();
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

    private void uploadFile(int index) throws IOException {
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
                        filePaths.add(jsonObject.getString("fileName"));
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(), "图片"+ Integer.toString(index) +"上传成功", Toast.LENGTH_SHORT).show();
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
        }, imagePath[index]);
    }


    private void uploadFile() throws IOException {
        for(int i = 0; i < imageCount; i++){
            int finalI = i;
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
                            filePaths.add(jsonObject.getString("fileName"));
                            Looper.prepare();
                            Toast.makeText(getApplicationContext(), "图片"+ Integer.toString(finalI) +"上传成功", Toast.LENGTH_SHORT).show();
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
            }, imagePath[i]);
        }
    }
}