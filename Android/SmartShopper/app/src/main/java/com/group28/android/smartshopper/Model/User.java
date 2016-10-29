package com.group28.android.smartshopper.Model;

/**
 * Created by Mihir on 10/26/2016.
 */

public class User {
    private String userId;
    private String userName;
    private String email;
    private String token;
    private String groupId;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId(){
         return userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName(){
        return userName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail(){
        return email;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken(){
        return token;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupId(){
        return groupId;
    }

}
