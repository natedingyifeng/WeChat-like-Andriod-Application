package com.dyf.andriod_frontend.contact;


public class Contact {
    private String nickname; // 昵称
    private int avatarIcon; // 头像
    public int check_type;

    public Contact(String nickname, int avatarIcon) {
        this.nickname = nickname;
        this.avatarIcon = avatarIcon;
        this.check_type = 0;
    }

    public Contact(String nickname, int avatarIcon, int type) {
        this.nickname = nickname;
        this.avatarIcon = avatarIcon;
        this.check_type = type;
    }

    public int getAvatarIcon() {
        return avatarIcon;
    }

    public String getNickname() {
        return nickname;
    }

    public int getType() { return check_type; }
}
