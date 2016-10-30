package com.group28.android.smartshopper.Model;

/**
 * Created by Mihir on 10/26/2016.
 */

public class User {
    private int userId;
    private String userName;
    private String email;
    private String token;
    private int groupId;

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserId(){
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

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getGroupId(){
        return groupId;
    }

}
