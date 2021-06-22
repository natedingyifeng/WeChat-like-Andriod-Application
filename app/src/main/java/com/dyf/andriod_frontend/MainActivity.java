package com.dyf.andriod_frontend;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.ListFragment;
import androidx.fragment.app.Fragment;

import com.dyf.andriod_frontend.utils.HttpRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.net.http.HttpResponseCache;
import android.os.Bundle;

import butterknife.BindView;
import butterknife.ButterKnife;

import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.dyf.andriod_frontend.moments.MomentsFragment;

import org.java_websocket.client.WebSocketClient;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity  {

    @BindView(R.id.bottomNavigationView)
    public BottomNavigationView bottomNavigationView;

    public static Handler msgHandler;
    private HttpResponseCache MultiDex;

    private WebSocketClient client;
    private WebSocketService.MyWebSocketClientBinder binder;
    private WebSocketService myWebSClientService;
    private ContactsMessageReceiver contactMessageReceiver;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.e("MainActivity", "服务与活动成功绑定");
            binder = (WebSocketService.MyWebSocketClientBinder) iBinder;
            myWebSClientService = binder.getService();
            client = WebSocketService.socketClient;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e("MainActivity", "服务与活动成功断开");
        }
    };

    /**
     * 绑定服务
     */
    public void bindService() {
        Intent bindIntent = new Intent(this, WebSocketService.class);
        bindService(bindIntent, serviceConnection, this.BIND_AUTO_CREATE);
    }
    /**
     * 启动服务（websocket客户端服务）
     */
    public void startMyWebSClientService() {
        Intent intent = new Intent(this, WebSocketService.class);
        startService(intent);
    }

    private class ContactsMessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String message=intent.getStringExtra("message");
        }
    }

    private void doRegisterReceiver() {
        contactMessageReceiver = new ContactsMessageReceiver();
        IntentFilter filter = new IntentFilter("com.xch.servicecallback.content");
        registerReceiver(contactMessageReceiver, filter);
    }

    /**
     * 权限申请
     */
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, permissions, 200);
                    return;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        }
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        startMyWebSClientService();
        bindService();
        doRegisterReceiver();

        // 修改下面的代码，添加向 settingsFragment的跳转逻辑
        // TODO

        ListFragment chatsFragment = new ChatsFragment();
//        Fragment messagesFragment = new MessagesFragment();
        Fragment contactsFragment = new ContactsFragment();
        Fragment settingsFragment = new SettingsFragment();
        Fragment momentsFragment = new MomentsFragment();
//        Fragment discoverFragment = new DiscoverFragment();
//        Fragment settingsFragment = new SettingsFragment();

        setCurrentFragment(chatsFragment); // 初始的Fragment为chatsFragment

        bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.chats:
                            setCurrentListFragment(chatsFragment);
                            return true;
                        case R.id.contacts:
                            setCurrentFragment(contactsFragment);
                            return true;
                        case R.id.moments:
                            setCurrentFragment(momentsFragment);
                            return true;
                        case R.id.settings:
                            setCurrentFragment(settingsFragment);
                            return true;
                    }
                    return false;
                }
        );

//        // 消息处理
//        msgHandler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                Toast.makeText(MainActivity.this, msg.obj.toString(), Toast.LENGTH_LONG).show();
//            }
//        };
//
        // 初始化websocket
//        WebSocketService.initSocket();

        HashMap<String, String> params = new HashMap<>();
        params.put("username", "yihao_xu");
        params.put("password", "123456");

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
                Log.e("response", resStr);
                try {
                    JSONObject jsonObject = new JSONObject(resStr);
                    if (jsonObject.getBoolean("success")){
                        // 将用户数据存入本地
                        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.store), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", "yihao_xu");
                        editor.commit();
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

//        // 消息处理
//        msgHandler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                Toast.makeText(MainActivity.this, msg.obj.toString(), Toast.LENGTH_LONG).show();
//            }
//        };

        // 初始化websocket
//        WebSocket.initSocket();

        Log.d("position", "String.valueOf(position)");


    }

    private void setCurrentFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, fragment).commit();
    }

    private void setCurrentListFragment(ListFragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, fragment).commit();
    }
}