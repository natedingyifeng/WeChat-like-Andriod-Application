package com.dyf.andriod_frontend.moments;

import com.dyf.andriod_frontend.user.User;

public class MomentsComment {
    private String postId;
    private String content;
    private User user;
    private User talkToUser;

    public MomentsComment(String postId, String content, User user, User talkToUser){
        this.postId = postId;
        this.user = user;
        this.content = content;
        this.talkToUser = talkToUser;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getTalkToUser() {
        return talkToUser;
    }

    public void setTalkToUser(User talkToUser) {
        this.talkToUser = talkToUser;
    }

    public boolean hasTalkToUser(){ return this.talkToUser != null; }

}
