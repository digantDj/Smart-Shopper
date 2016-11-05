package com.group28.android.smartshopper.Model;

/**
 * Created by deepika on 11/3/2016.
 */

public class Participant {
    private int memoId;
    //private int groupId;
    private String email;

    public int getMemoId() {
        return memoId;
    }

    public void setMemoId(int memoId) {
        this.memoId = memoId;
    }

    /*public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
*/
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
