package com.dyf.andriod_frontend.friendrequest;

import com.dyf.andriod_frontend.user.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FriendRequest {


    private User sentUser;

    public FriendRequest(JSONObject jsonObject) throws JSONException {
        this.sentUser = new User(jsonObject.getJSONObject("sentUser"));
    }

    public User getSentUser() {
        return sentUser;
    }

    public void setSentUser(User sentUser) {
        this.sentUser = sentUser;
    }
}
