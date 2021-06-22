package com.dyf.andriod_frontend.chat;

public class Chat {

    private final String nickname; // 昵称
    private String lastSpeak; //最后聊天内容
    private final int avatarIcon; // 头像
    private String lastSpeakTime; //最后联络时间
    private final int chatType;

    public Chat(String nickname, int avatarIcon, String lastSpeak, String lastSpeakTime) {
        this.nickname = nickname;
        this.avatarIcon = avatarIcon;
        this.lastSpeak = lastSpeak;
        this.lastSpeakTime = lastSpeakTime;
        this.chatType = 0;
    }

    public Chat(String nickname, int avatarIcon, String lastSpeak, String lastSpeakTime, int chatType) {
        this.nickname = nickname;
        this.avatarIcon = avatarIcon;
        this.lastSpeak = lastSpeak;
        this.lastSpeakTime = lastSpeakTime;
        this.chatType = chatType;
    }

    public int getAvatarIcon() {
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
}
