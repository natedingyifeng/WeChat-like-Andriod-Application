package com.dyf.andriod_frontend.groupMemberIcons;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dyf.andriod_frontend.R;
import com.dyf.andriod_frontend.utils.HttpRequest;

import java.util.List;

public class IconsAdapter extends RecyclerView.Adapter<IconsAdapter.ViewHolder>{

    private List<Icons> mIconsList;
    private Context Context;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View iconsView;
        ImageView iconsImage;
        TextView iconName;

        public ViewHolder(View view) {
            super(view);
            iconsView = view;
            iconsImage = (ImageView) view.findViewById(R.id.member_icon);
            iconName = (TextView) view.findViewById(R.id.member_name);
        }
    }

    public IconsAdapter(List<Icons> iconsList, Context context) {
        mIconsList = iconsList;
        Context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(Context).inflate(R.layout.gruop_chat_info_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.iconsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Icons icon = mIconsList.get(position);
//                Toast.makeText(v.getContext(), "you clicked image " + fruit.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Icons icon = mIconsList.get(position);
//        holder.iconsImage.setImageResource(icon.getAvatarIcon());
        Glide
                .with(Context)
                .load(HttpRequest.media_url+icon.getAvatarIcon())
                .into(holder.iconsImage);
        holder.iconName.setText(icon.getName());
    }

    @Override
    public int getItemCount() {
        return mIconsList.size();
    }

}