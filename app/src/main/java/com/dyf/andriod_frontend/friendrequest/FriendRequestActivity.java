package com.dyf.andriod_frontend.friendrequest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dyf.andriod_frontend.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.LinkedList;

public class FriendRequestActivity extends AppCompatActivity {

    private LinkedList<FriendRequest> requests;
    private LinearLayoutManager layoutManager;
    private FriendRequestAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frient_request);
        // 隐藏标题栏
        if(getSupportActionBar()!= null){
            getSupportActionBar().hide();
        }
        TextView title = findViewById(R.id.title_text);
        title.setText("好友请求");
        Button titleBack2 = findViewById(R.id.title_back2);
        titleBack2.setVisibility(View.INVISIBLE);
        Button titleBack = (Button) findViewById(R.id.title_back);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FriendRequestActivity.this.finish();
            }
        });

        // 从SharePreference 获取请求

        SharedPreferences sp = getSharedPreferences(getString(R.string.store), MODE_PRIVATE);
        requests = new LinkedList<>();


        try {
            JSONArray jsonArray = new JSONArray(sp.getString("friendRequest", "[]"));
            for(int i = 0; i < jsonArray.length(); i++){
                FriendRequest request = new FriendRequest(jsonArray.getJSONObject(i));
                requests.add(request);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        recyclerView = findViewById(R.id.friend_request_recyclerview);
        adapter = new FriendRequestAdapter(requests, getApplicationContext());
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
    }


}