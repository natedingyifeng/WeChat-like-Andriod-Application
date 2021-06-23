package com.dyf.andriod_frontend.groupMemberIcons;

public class Icons {
    private final String avatarIcon; // 头像
    private final int iconType;
    private String name;

    public Icons(String name, String avatarIcon) {
        this.avatarIcon = avatarIcon;
        this.iconType = 0;
        this.name = name;
    }

    public Icons(String avatarIcon, int type) {
        this.avatarIcon = avatarIcon;
        this.iconType = type;
        this.name = "";
    }

    public String getAvatarIcon() {
        return avatarIcon;
    }

    public int getIconType() {
        return iconType;
    }

    public String getName() { return name; }
}
