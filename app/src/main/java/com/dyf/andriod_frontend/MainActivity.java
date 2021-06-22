package com.dyf.andriod_frontend;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.ListFragment;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.net.http.HttpResponseCache;
import android.os.Bundle;

import butterknife.BindView;
import butterknife.ButterKnife;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.dyf.andriod_frontend.moments.MomentsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity  {

    @BindView(R.id.bottomNavigationView)
    public BottomNavigationView bottomNavigationView;

    public static Handler msgHandler;
    private HttpResponseCache MultiDex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

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

        // 消息处理
        msgHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Toast.makeText(MainActivity.this, msg.obj.toString(), Toast.LENGTH_LONG).show();
            }
        };

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