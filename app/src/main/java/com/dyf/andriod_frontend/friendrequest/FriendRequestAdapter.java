package com.dyf.andriod_frontend.friendrequest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dyf.andriod_frontend.R;
import com.dyf.andriod_frontend.moments.MomentsComment;
import com.dyf.andriod_frontend.utils.HttpRequest;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.FriendRequestHolder> {
    LinkedList<FriendRequest> data;
    Context context;

    String LOG_TAG = "FriendRequest";

    Handler handler;

    public FriendRequestAdapter(LinkedList<FriendRequest> data, Context context){
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public FriendRequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_friend_request, parent,false);
        FriendRequestHolder holder = new FriendRequestHolder(view);
        return holder;
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void onBindViewHolder(@NonNull FriendRequestHolder holder, int position) {
        FriendRequest request = data.get(position);

        Glide
                .with(context)
                .load(request.getSentUser().getAvatarUrl())
                .into(holder.avatarView);
        holder.nicknameText.setText(request.getSentUser().getNickname());

        holder.agreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agreeFriendRequest(position);
            }
        });

        holder.rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rejectFriendRequest(position);
            }
        });

        handler = new Handler(){
            @SuppressLint("HandlerLeak")
            public void handleMessage(Message msg){
                removeFriendRequest(position);
            }
        };

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private void agreeFriendRequest(int position){
        // TODO
        HashMap<String, String> params = new HashMap<>();
        params.put("agree", "true");
        params.put("replyToUserId", data.get(position).getSentUser().getId());

        HttpRequest.sendOkHttpPostRequest("user/reply", new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(context,R.string.network_error, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resStr = response.body().string();
                Log.e("response", resStr);
                try {
                    JSONObject jsonObject = new JSONObject(resStr);
                    if (jsonObject.getBoolean("success")){
                        // 获取用户数据

                        handler.sendEmptyMessage(1);

                        Looper.prepare();
                        Toast.makeText(context,"同意成功", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }else{
                        Looper.prepare();
                        Toast.makeText(context,"同意失败", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("FriendRequestA Json", resStr);
                    Looper.prepare();
                    Toast.makeText(context,R.string.json_parse_error, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }, params);
    }

    private void rejectFriendRequest(int position){
        // TODO
        HashMap<String, String> params = new HashMap<>();
        params.put("agree", "false");
        params.put("replyToUserId", data.get(position).getSentUser().getId());

        HttpRequest.sendOkHttpPostRequest("user/reply", new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(context,R.string.network_error, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resStr = response.body().string();
                Log.e("response", resStr);
                try {
                    JSONObject jsonObject = new JSONObject(resStr);
                    if (jsonObject.getBoolean("success")){
                        // 获取用户数据

                        handler.sendEmptyMessage(1);

                        Looper.prepare();
                        Toast.makeText(context,"拒绝成功", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }else{
                        Looper.prepare();
                        Toast.makeText(context,"拒绝失败", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("FriendRequestA Json", resStr);
                    Looper.prepare();
                    Toast.makeText(context,R.string.json_parse_error, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }, params);
    }

    private void removeFriendRequest(int position){
        // TODO
        String username = data.get(position).getSentUser().getUsername();
        for(int i = 0; i < data.size(); i++) {
            if(data.get(i).getSentUser().getUsername().equals(username)){
                data.remove(i);
                i--;
            }
        }
        notifyDataSetChanged();

        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.store), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        List<FriendRequest> requests = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray();
            for(int i = 0; i < jsonArray.length(); i++){
                FriendRequest request = new FriendRequest(jsonArray.getJSONObject(i));
                if(!request.getSentUser().getUsername().equals(username))
                    requests.add(request);
            }
            editor.putString("friendRequest", jsonArray.toString());
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static class FriendRequestHolder extends RecyclerView.ViewHolder{

        ImageView avatarView;
        TextView nicknameText;
        ImageButton agreeButton;
        ImageButton rejectButton;


        public FriendRequestHolder(@NonNull View itemView) {
            super(itemView);
            avatarView = itemView.findViewById(R.id.friend_request_avatar_icon);
            nicknameText = itemView.findViewById(R.id.friend_request_nickname_text);
            agreeButton = itemView.findViewById(R.id.friend_request_agree_btn);
            rejectButton = itemView.findViewById(R.id.friend_request_reject_btn);
        }
    }
}
