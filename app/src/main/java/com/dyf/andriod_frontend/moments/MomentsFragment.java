package com.dyf.andriod_frontend.moments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.dyf.andriod_frontend.R;
import com.dyf.andriod_frontend.utils.HttpRequest;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import okhttp3.Call;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MomentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MomentsFragment extends Fragment {

    private RecyclerView recyclerView;
    private static final int pageSize = 5;
    private int pageNum = 0;

    private LinkedList<Moment> moments;

    private Handler handler;

    LinearLayoutManager layoutManager;
    MomentsAdapter momentsAdapter;

    private boolean isFreshing = false;

    public MomentsFragment() {
        // Required empty public constructor
    }

    public static MomentsFragment newInstance(){
        MomentsFragment fragment = new MomentsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView title = getActivity().findViewById(R.id.title_text);
        title.setText(R.string.moments);
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // 右上角的弹出菜单
        ImageButton releaseBtn = view.findViewById(R.id.moments_release_btn);
        releaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getContext(), v);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.moments_popout_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()) {
                            case R.id.picture_moment_release_btn:
                                Intent intent = new Intent(getActivity(), MomentsReleaseActivity.class);
                                startActivity(intent);
                                break;
                            case R.id.video_moment_release_btn:

                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
                Intent intent = new Intent(getActivity(), MomentsReleaseActivity.class);
                startActivity(intent);
            }
        });

        recyclerView = view.findViewById(R.id.moments_recycler_view);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int last = layoutManager.findLastVisibleItemPosition();
                int first = layoutManager.findFirstVisibleItemPosition();
                int sum = momentsAdapter.getItemCount();
                if(newState == RecyclerView.SCROLL_STATE_SETTLING && last + 1 == sum){
                    if(!isFreshing){
                        isFreshing = true;
                        pageNum++;
                        try {
                            getMoments();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }else if(newState == RecyclerView.SCROLL_STATE_SETTLING && first == 0){
                    if(!isFreshing){
                        isFreshing = true;
                        pageNum = 0;
                        try {
                            getMoments();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
//        Context context = getActivity();
        moments = new LinkedList<>();

        handler = new Handler(){
            @SuppressLint("HandlerLeak")
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                if(pageNum == 0)
                    setData();
            }
        };

        try {
            getMoments();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void setData(){
        momentsAdapter = new MomentsAdapter(moments, getContext());
        momentsAdapter.setHasStableIds(true);
        recyclerView.setAdapter(momentsAdapter);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        isFreshing = false;
    }

    private void appendData(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_moments, container, false);
    }

    private void getMoments() throws IOException {
        HashMap<String, String> params = new HashMap<>();
        params.put("pageSize", Integer.toString(pageSize));
        params.put("pageNum", Integer.toString(pageNum));

        HttpRequest.sendOkHttpPostRequest("post/get", new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(getContext(),R.string.network_error, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resStr = response.body().string();
                Log.e("response", resStr);
                if(pageNum == 0){
                    moments.clear();
                }

                try {
                    JSONObject jsonObject = new JSONObject(resStr);
                    if (jsonObject.getBoolean("success")){
                        JSONArray posts = jsonObject.getJSONArray("posts");
                        for(int i = 0; i < posts.length(); i ++){
                            JSONObject item = posts.getJSONObject(i);
                            moments.add(new Moment(item));
                        }
                        handler.sendEmptyMessage(1);
                    }else{
                        Looper.prepare();
                        Toast.makeText(getContext(),R.string.username_or_password_error, Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(getContext(),R.string.json_parse_error, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }, params);

    }


}