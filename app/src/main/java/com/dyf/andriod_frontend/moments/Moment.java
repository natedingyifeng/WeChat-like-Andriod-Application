package com.dyf.andriod_frontend.moments;

import com.dyf.andriod_frontend.user.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.LinkedList;

public class Moment {
    private String content;
    private LinkedList<MomentsComment> comments;
    private String createdAt;
    private String id;
    private String lastModifiedAt;
    private ArrayList<User> likedUsers;
    private User momentsOwner;
    private String postType;
    private ArrayList<String> imagesUrl;

    public Moment(String content, LinkedList<MomentsComment> comments, String createdAt, String id,
                  String lastModifiedAt, ArrayList<User> likedUsers, User momentsOwner, String postType, ArrayList<String> imagesUrl){
        this.content = content;
        this.comments = comments;
        this.createdAt = createdAt;
        this.id = id;
        this.lastModifiedAt = lastModifiedAt;
        this.likedUsers = likedUsers;
        this.momentsOwner = momentsOwner;
        this.postType = postType;
        this.imagesUrl = imagesUrl;
    }

    public Moment(JSONObject jsonObject) throws JSONException {
        // TODO 根据后端提供的http回复来构造Moment
        this.content = jsonObject.getString("content");
        this.comments = null;
        this.createdAt = jsonObject.getString("createdAt");
        this.id = jsonObject.getString("id");
        this.lastModifiedAt = jsonObject.getString("lastModifiedAt");
        this.momentsOwner = new User(jsonObject.getJSONObject("postOwner"));
        this.postType = jsonObject.getString("postType");
        this.likedUsers = new ArrayList<User>();
        JSONArray jsonArray = jsonObject.getJSONArray("likedUsers");
        for(int i = 0; i < jsonArray.length(); i++){
            likedUsers.add(new User(jsonArray.getJSONObject(i)));
        }
    }

    public int getImageCount(){
        return imagesUrl.size();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LinkedList<MomentsComment> getComments() {
        return comments;
    }

    public void setComments(LinkedList<MomentsComment> comments) {
        this.comments = comments;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastModifiedAt() {
        return lastModifiedAt;
    }

    public void setLastModifiedAt(String lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt;
    }

    public ArrayList<User> getLikedUsers() {
        return likedUsers;
    }

    public void setLikedUsers(ArrayList<User> likedUsers) {
        this.likedUsers = likedUsers;
    }

    public User getMomentsOwner() {
        return momentsOwner;
    }

    public void setMomentsOwner(User momentsOwner) {
        this.momentsOwner = momentsOwner;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public ArrayList<String> getImagesUrl() {
        return imagesUrl;
    }

    public void setImagesUrl(ArrayList<String> imagesUrl) {
        this.imagesUrl = imagesUrl;
    }
}
