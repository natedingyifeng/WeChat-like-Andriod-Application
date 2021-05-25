package com.dyf.andriod_frontend.moments;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.dyf.andriod_frontend.R;

import java.util.LinkedList;

public class MomentsCommentAdapter extends RecyclerView.Adapter<MomentsCommentAdapter.MomentsCommentViewHolder> {

    private LinkedList<MomentsComment> data;
    private Context context;

    public MomentsCommentAdapter(LinkedList<MomentsComment> data, Context context){
        this.data = data;
        this.context = context;
    }


    @NonNull
    @Override
    public MomentsCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_moments_comment,parent,false);
        MomentsCommentAdapter.MomentsCommentViewHolder holder = new MomentsCommentViewHolder(view);
        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull MomentsCommentViewHolder holder, int position) {
        MomentsComment comment = data.get(position);
//        holder.setIsRecyclable(false); // 禁止复用

        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(context.getColor(R.color.blue_name));
        ForegroundColorSpan colorSpan2 = new ForegroundColorSpan(context.getColor(R.color.blue_name));

        if (comment.hasTalkToUser()) {
            ssb.append(comment.getUser().getNickname() + " 回复 " + comment.getTalkToUser().getNickname());
            ssb.setSpan(colorSpan,0, comment.getUser().getNickname().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.setSpan(colorSpan2, comment.getUser().getNickname().length() + 4, comment.getUser().getNickname().length() + 4 + comment.getTalkToUser().getNickname().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }else{
            ssb.append(comment.getUser().getNickname());
            ssb.setSpan(colorSpan,0, comment.getUser().getNickname().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        ssb.append(" : " + comment.getContent());
        holder.contentView.setText(ssb);
    }

    public void addData(MomentsComment comment){
        data.add(comment);
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class MomentsCommentViewHolder extends RecyclerView.ViewHolder{
        TextView contentView;

        public MomentsCommentViewHolder(@NonNull View itemView) {
            super(itemView);
            contentView = itemView.findViewById(R.id.moments_comment_text_view);
        }
    }

}
