package com.dyf.andriod_frontend.contact;


public class Contact {
    private String nickname; // 昵称
    private int avatarIcon; // 头像
    public int check_type;
    private String id;

    public Contact(String nickname, int avatarIcon, String id) {
        this.nickname = nickname;
        this.avatarIcon = avatarIcon;
        this.check_type = 0;
        this.id = id;
    }

    public Contact(String nickname, int avatarIcon, int type, String id) {
        this.nickname = nickname;
        this.avatarIcon = avatarIcon;
        this.check_type = type;
        this.id = id;
    }

    public int getAvatarIcon() {
        return avatarIcon;
    }

    public String getNickname() {
        return nickname;
    }

    public int getType() { return check_type; }

    public String getId() {return id;}
}
