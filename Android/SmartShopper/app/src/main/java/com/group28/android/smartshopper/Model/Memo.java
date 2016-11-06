package com.group28.android.smartshopper.Model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.StringBuilderPrinter;

/**
 * Created by Mihir on 10/26/2016.
 */

public class Memo implements Parcelable {
    private int memoId;
    private int userId;
    private int groupMemoId;
    private String category;
    private String content;
    private String status;
    private String type;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getGroupMemoId() {
        return groupMemoId;
    }

    public void setGroupMemoId(int groupId) {
        this.groupMemoId = groupId;
    }

    public void setMemoId(int memoId) {
        this.memoId = memoId;
    }

    public int getMemoId(){
         return memoId;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory(){
        return category;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent(){
        return content;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus(){
        return status;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType(){
        return type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {String.valueOf(this.memoId),
                this.category,
                this.status,
                this.content,
                this.type,
                String.valueOf(this.groupMemoId)});
    }

    public Memo(){

    }


    // Parcelling part
    public Memo(Parcel in){
        String[] data = new String[6];

        in.readStringArray(data);
        this.memoId = Integer.parseInt(data[0]);
        this.category = data[1];
        this.status = data[2];
        this.content = data[3];
        this.type = data[4];
        this.groupMemoId = Integer.parseInt(data[5]);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Memo createFromParcel(Parcel in) {
            return new Memo(in);
        }

        public Memo[] newArray(int size) {
            return new Memo[size];
        }
    };
}
