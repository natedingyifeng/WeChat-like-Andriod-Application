package com.dyf.andriod_frontend.moments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dyf.andriod_frontend.R;
import com.dyf.andriod_frontend.user.User;
import com.dyf.andriod_frontend.utils.HttpRequest;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.LinkedList;

public class MomentsAdapter extends RecyclerView.Adapter<MomentsAdapter.MomentsViewHolder> {
    private LinkedList<Moment> moments;
    private Context context;
    private String username;
    private String nickname;
    private MomentsCommentAdapter mca;

    public MomentsAdapter(LinkedList<Moment> moments, Context context) {this.moments = moments; this.context = context;}

    @NonNull
    @Override
    public MomentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        context = parent.getContext();
        View view;
        switch(viewType){
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
        holder.setIsRecyclable(false);// 禁止复用
        Moment moment = moments.get(position);
        int type = getItemViewType(position);
        Glide.with(context).load(moment.getMomentsOwner().getAvatarUrl()).into(holder.avatarImage);
        holder.nickname.setText(moment.getMomentsOwner().getNickname());
        holder.time.setText(moment.getCreatedAt());
        holder.content.setText(moment.getContent());
        holder.likedUserText.setText(getLikedUserString(position));

        ArrayList<String> images = moment.getImagesUrl();
        for(int i = 0; i < type; i ++){
            Glide
                    .with(context)
                    .load(images.get(i))
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(holder.imageViews[i]);
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.store), Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username","Ifreet");
        nickname = sharedPreferences.getString("nickname", "九月的南瓜");

        if(hasLiked(position))
            holder.starBtn.setImageResource(android.R.drawable.star_big_on);

        LinkedList<MomentsComment> comments = moment.getComments();
        mca = new MomentsCommentAdapter(comments, context);
        mca.setHasStableIds(true);

        holder.commentsRecyclerView.setAdapter(mca);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        holder.commentsRecyclerView.setLayoutManager(layoutManager);


        // 设置监听器
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
        }
        else {
            holder.starBtn.setImageResource(android.R.drawable.star_big_on);
            ArrayList<User> likedUser = moments.get(position).getLikedUsers();
            likedUser.add(new User(username, nickname));
            moments.get(position).setLikedUsers(likedUser);
        }
        holder.likedUserText.setText(getLikedUserString(position));
        // TODO 发送请求

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
//                mca.addData(comment);
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
}
