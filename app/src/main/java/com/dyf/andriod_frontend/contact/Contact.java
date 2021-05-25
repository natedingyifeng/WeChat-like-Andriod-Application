package com.dyf.andriod_frontend.contact;


public class Contact {
    private String nickname; // 昵称
    private int avatarIcon; // 头像

    public Contact(String nickname, int avatarIcon) {
        this.nickname = nickname;
        this.avatarIcon = avatarIcon;
    }

    public int getAvatarIcon() {
        return avatarIcon;
    }

    public String getNickname() {
        return nickname;
    }
}
