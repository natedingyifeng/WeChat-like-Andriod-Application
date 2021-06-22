package com.dyf.andriod_frontend.user;

import com.dyf.andriod_frontend.utils.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    private String id;
    private String nickname;
    private String username;
    private String password;
    private String phoneNumber;
    private String slogan;
    private String userType;
    private String avatarUrl;

    public User(String username, String nickname){
        this.username = username;
        this.nickname = nickname;
    }

    public User(String id, String nickname, String username, String password, String phoneNumber, String slogan, String userType, String avatarUrl){
        this.id = id;
        this.nickname = nickname;
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.slogan = slogan;
        this.userType = userType;
        this.avatarUrl = avatarUrl;
    }

    public User(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getString("id");
        this.nickname = jsonObject.getString("nickname");
        this.username = jsonObject.getString("username");
        this.avatarUrl = HttpRequest.media_url + jsonObject.getString("avatarUrl");

        if(jsonObject.has("password"))
            this.password = jsonObject.getString("password");
        if(jsonObject.has("phoneNumber"))
            this.phoneNumber = jsonObject.getString("phoneNumber");
        if(jsonObject.has("slogan"))
            this.slogan = jsonObject.getString("slogan");
        if(jsonObject.has("userType"))
            this.userType = jsonObject.getString("userType");

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

}
