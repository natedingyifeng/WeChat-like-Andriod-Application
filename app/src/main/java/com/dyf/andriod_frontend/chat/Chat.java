package com.dyf.andriod_frontend.chat;

public class Chat {

    private final String nickname; // 昵称
    private String lastSpeak; //最后聊天内容
    private String avatarIcon; // 头像
    private String lastSpeakTime; //最后联络时间
    private final int chatType;
    private String id;

    public Chat(String nickname, String avatarIcon, String lastSpeak, String lastSpeakTime, String id) {
        this.nickname = nickname;
        this.avatarIcon = avatarIcon;
        this.lastSpeak = lastSpeak;
        this.lastSpeakTime = lastSpeakTime;
        this.chatType = 0;
        this.id = id;
    }

    public Chat(String nickname, String avatarIcon, String lastSpeak, String lastSpeakTime, int chatType, String id) {
        this.nickname = nickname;
        this.avatarIcon = avatarIcon;
        this.lastSpeak = lastSpeak;
        this.lastSpeakTime = lastSpeakTime;
        this.chatType = chatType;
        this.id = id;
    }

    public String getAvatarIcon() {
        return avatarIcon;
    }

    public String getLastSpeak() {
        return lastSpeak;
    }

    public String getLastSpeakTime() {
        return lastSpeakTime;
    }

    public String getNickname() {
        return nickname;
    }

    public int getChatType() { return chatType; }

    public void setLastSpeak(String sen) { this.lastSpeak = sen; }

    public void setLastSpeakTime(String sen) { this.lastSpeakTime = sen; }

    public String getId() { return this.id; }
}
