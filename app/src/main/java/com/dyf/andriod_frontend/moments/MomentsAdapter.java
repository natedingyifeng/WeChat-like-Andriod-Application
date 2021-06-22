package com.dyf.andriod_frontend.moments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.dyf.andriod_frontend.R;
import com.dyf.andriod_frontend.user.User;
import com.dyf.andriod_frontend.user.UserInfoActivity;
import com.dyf.andriod_frontend.utils.HttpRequest;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Comment;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;
import okhttp3.Call;
import okhttp3.Response;

public class MomentsAdapter extends RecyclerView.Adapter<MomentsAdapter.MomentsViewHolder> {
    private LinkedList<Moment> moments;
    private Context context;
    private Window activityWindow;
    private String username;
    private String nickname;
    private MomentsCommentAdapter mca;

    private Dialog imageDialog;

    public MomentsAdapter(LinkedList<Moment> moments, Context context) {this.moments = moments; this.context = context;}
    public MomentsAdapter(LinkedList<Moment> moments, Context context, Window activityWindow) {this.moments = moments; this.context = context; this.activityWindow = activityWindow;}

    @NonNull
    @Override
    public MomentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        context = parent.getContext();
        View view;
        switch(viewType){
            case -1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_moments_video,parent,false);
                break;
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_moments_1_image,parent,false);
                break;
            case 2:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_moments_2_image,parent,false);
                break;
            case 3:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_moments_3_image,parent,false);
                break;
            case 4:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_moments_4_image,parent,false);
                break;
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_moments,parent,false);
        }
        MomentsAdapter.MomentsViewHolder holder = new MomentsViewHolder(view, viewType);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MomentsViewHolder holder, int position) {
//        holder.setIsRecyclable(false);// 禁止复用
        Moment moment = moments.get(position);
        int type = getItemViewType(position);
        Glide.with(context)
                .load(moment.getMomentsOwner().getAvatarUrl())
                .thumbnail( 0.1f )
                .into(holder.avatarImage);
        holder.nickname.setText(moment.getMomentsOwner().getNickname());
        holder.time.setText(getTime(Long.parseLong(moment.getCreatedAt())));
        holder.content.setText(moment.getContent());
        holder.likedUserText.setText(getLikedUserString(position));

        ArrayList<String> images = moment.getImagesUrl();
        for(int i = 0; i < type; i ++){
            Glide
                    .with(context)
                    .load(images.get(i))
//                    .skipMemoryCache(true)
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .thumbnail(0.2f)
                    .override(200,200)
                    .into(holder.imageViews[i]);
        }

        if(type == -1){

            holder.videoView.setUp(images.get(0), "视频", Jzvd.SCREEN_NORMAL);
            Glide.with(context)
                    .load(images.get(0))
                    .into(holder.videoView.posterImageView);

        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.store), Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username","你");
        nickname = sharedPreferences.getString("nickname", "你");

        if(hasLiked(position))
            holder.starBtn.setImageResource(android.R.drawable.star_big_on);

        LinkedList<MomentsComment> comments = moment.getComments();
        mca = new MomentsCommentAdapter(comments, context);
        mca.setHasStableIds(true);

        holder.commentsRecyclerView.setAdapter(mca);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        holder.commentsRecyclerView.setLayoutManager(layoutManager);


        // 设置监听器
        holder.avatarImage.setClickable(true);
        holder.avatarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserInfoActivity.class);
                intent.putExtra("username", moments.get(position).getMomentsOwner().getUsername());
                context.startActivity(intent);
            }
        });

        holder.starBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                like(holder, position);
            }
        });
        holder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment(holder, position);
            }
        });

        if(moments.get(position).getPostType().equals("PHOTO")){
              for(int i = 0; i < moments.get(position).getImageCount(); i++) {
                  int finalI = i;
                  holder.imageViews[i].setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          showBigImage(finalI, position);
                          imageDialog.show();
                      }
                  });
              }
        }
    }

    private void showBigImage(int index, int position){
        imageDialog = new Dialog(context, R.style.FullActivity);

        WindowManager.LayoutParams params = activityWindow.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        imageDialog.getWindow().setAttributes(params);

        String imageUrl = moments.get(position).getImagesUrl().get(index);
        ImageView imageView = getDialogImageView(imageUrl);
        imageDialog.setContentView(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageDialog.dismiss();
            }
        });
    }

    private ImageView getDialogImageView(String imageUrl){
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        Glide.with(context)
                .load(imageUrl)
                .into(imageView);
        return imageView;
    }

    @Override
    public int getItemCount() {
        return moments.size();
    }

    @Override
    public int getItemViewType(int position) {
        Moment moment = moments.get(position);
        return moment.getImageCount();
    }

    private void like(MomentsViewHolder holder, int position){
        if(hasLiked(position)) {
            holder.starBtn.setImageResource(android.R.drawable.star_big_off);
            ArrayList<User> likedUser = moments.get(position).getLikedUsers();
            for(int i = 0; i < likedUser.size(); i++){
                if(likedUser.get(i).getUsername().equals(username)){
                    likedUser.remove(i);
                    break;
                }
            }
            moments.get(position).setLikedUsers(likedUser);
            HashMap<String, String> params = new HashMap<>();
            params.put("postId", moments.get(position).getId());
            HttpRequest.sendOkHttpPostRequest("post/unlike", new okhttp3.Callback() {
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
                            Looper.prepare();
                            Toast.makeText(context, "取消点赞成功", Toast.LENGTH_SHORT).show();
                        }else{
                            Looper.prepare();
                            Toast.makeText(context,"取消点赞失败", Toast.LENGTH_SHORT).show();
                        }
                        Looper.loop();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Looper.prepare();
                        Toast.makeText(context,R.string.json_parse_error, Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }
            }, params);
        }
        else {
            holder.starBtn.setImageResource(android.R.drawable.star_big_on);
            ArrayList<User> likedUser = moments.get(position).getLikedUsers();
            likedUser.add(new User(username, nickname));
            moments.get(position).setLikedUsers(likedUser);
            HashMap<String, String> params = new HashMap<>();
            params.put("postId", moments.get(position).getId());
            HttpRequest.sendOkHttpPostRequest("post/like", new okhttp3.Callback() {
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
                            Looper.prepare();
                            Toast.makeText(context, "点赞成功", Toast.LENGTH_SHORT).show();
                        }else{
                            Looper.prepare();
                            Toast.makeText(context,"点赞失败", Toast.LENGTH_SHORT).show();
                        }
                        Looper.loop();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Looper.prepare();
                        Toast.makeText(context,R.string.json_parse_error, Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }
            }, params);
        }
        holder.likedUserText.setText(getLikedUserString(position));



    }

    private String getLikedUserString(int position){
        StringBuilder sb = new StringBuilder();
        for(User user : moments.get(position).getLikedUsers()){
            sb.append(user.getNickname());
            sb.append(",");
        }
        if(sb.length() != 0) {
            sb.delete(sb.length() - 1, sb.length());
            sb.append(" 赞了");
        }
        return sb.toString();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void comment(MomentsViewHolder holder, int position){
        // TODO 调用输入框，进行评论
        final EditText inputServer = new EditText(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("回复 " + moments.get(position).getMomentsOwner().getNickname()).setIcon(android.R.drawable.sym_action_chat).setView(inputServer)
                .setNegativeButton("取消", null);
        builder.setPositiveButton("发送", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                inputServer.getText().toString();
                MomentsComment comment = new MomentsComment(moments.get(position).getId(),inputServer.getText().toString(), new User(username, nickname), null);
                moments.get(position).getComments().add(comment);
                mca.notifyDataSetChanged();
                notifyDataSetChanged();

                HashMap<String, String> params = new HashMap<>();
                params.put("content", inputServer.getText().toString());
                params.put("postId", moments.get(position).getId());
//                params.put("talkToUserId", moments.get(position).getMomentsOwner().getId());
                HttpRequest.sendOkHttpPostRequest("post/comment", new okhttp3.Callback() {
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
                                Looper.prepare();
                                Toast.makeText(context, "评论成功", Toast.LENGTH_SHORT).show();
                            }else{
                                Looper.prepare();
                                Toast.makeText(context,"评论失败", Toast.LENGTH_SHORT).show();
                            }
                            Looper.loop();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Looper.prepare();
                            Toast.makeText(context,R.string.json_parse_error, Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                }, params);
            }
        });
        builder.show();



    }

    private boolean hasLiked(int position){
        for(User user: moments.get(position).getLikedUsers()){
            if (user.getUsername().equals(username)){
                return true;
            }
        }
        return false;
    }

    public static class MomentsViewHolder extends RecyclerView.ViewHolder{
        public int imageCount;
        public ImageView avatarImage;
        public TextView nickname;
        public TextView content;
        public TextView time;
        public ImageView[] imageViews = new ImageView[9];
        public JzvdStd videoView;
        public RecyclerView commentsRecyclerView;
        public ImageButton starBtn;
        public ImageButton commentBtn;
        public TextView likedUserText;

        public MomentsViewHolder(@NonNull View itemView, int imageCount) {
            super(itemView);
            this.imageCount = imageCount;
            avatarImage = itemView.findViewById(R.id.moments_avatar_image_view);
            nickname = itemView.findViewById(R.id.moments_nickname_text_view);
            content = itemView.findViewById(R.id.moments_content_text_view);
            time = itemView.findViewById(R.id.moments_time_text_view);
            commentsRecyclerView = itemView.findViewById(R.id.moments_comment_list_view);
            starBtn = itemView.findViewById(R.id.moments_star_btn);
            commentBtn = itemView.findViewById(R.id.moments_comment_btn);
            likedUserText = itemView.findViewById(R.id.moments_like_users_text_view);
            switch (imageCount){
                case -1:
                    videoView = (JzvdStd) itemView.findViewById(R.id.moments_video_view);
                    break;
                case 4:
                    imageViews[3] = itemView.findViewById(R.id.moments_image_4_view);
                case 3:
                    imageViews[2] = itemView.findViewById(R.id.moments_image_3_view);
                case 2:
                    imageViews[1] = itemView.findViewById(R.id.moments_image_2_view);
                case 1:
                    imageViews[0] = itemView.findViewById(R.id.moments_image_1_view);
            }
        }
    }

    public static String getTime(Long timeStamp){
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH时mm分");
        String time = sdf.format(new Date(Long.parseLong(String.valueOf(timeStamp))));
        return time;
    }
}
