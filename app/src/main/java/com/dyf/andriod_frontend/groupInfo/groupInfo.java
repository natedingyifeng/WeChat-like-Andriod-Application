package com.dyf.andriod_frontend.groupInfo;

import android.net.Uri;

public class groupInfo {

    private final String content;
    private String title;

    public groupInfo(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public groupInfo(String title) {
        this.title = title;
        this.content = "";
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
