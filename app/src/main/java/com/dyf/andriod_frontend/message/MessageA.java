package com.dyf.andriod_frontend.message;

import android.net.Uri;
import android.widget.ImageView;

public class MessageA {

    private final String content; //内容
    private final int avatarIcon; // 头像
    private final int componentType;
    private final Uri content_image;
    private final String video_path;

    public MessageA(String nickname, int avatarIcon, String content, int componentType) {
        this.avatarIcon = avatarIcon;
        this.content = content;
        this.componentType = componentType;
        this.content_image = null;
        this.video_path = null;
    }

    public MessageA(String nickname, int avatarIcon, int componentType, Uri content_image) {
        this.avatarIcon = avatarIcon;
        this.content = "";
        this.componentType = componentType;
        this.content_image = content_image;
        this.video_path = null;
    }

    public MessageA(String nickname, int avatarIcon, int componentType, String path) {
        this.avatarIcon = avatarIcon;
        this.content = "";
        this.componentType = componentType;
        this.content_image = null;
        this.video_path = path;
    }

    public int getAvatarIcon() {
        return avatarIcon;
    }

    public String getContent() {
        return content;
    }

    public int getComponentType() { return componentType; }

    public Uri getContentImage() { return content_image; }

    public String getVideoPath() { return video_path; }
}
