package com.group28.android.smartshopper.Model;

/**
 * Created by Mihir on 10/26/2016.
 */

public class Memo {
    private String memoId;
    private String category;
    private String content;
    private String status;
    private String type;

    public void setMemoId(String memoId) {
        this.memoId = memoId;
    }

    public String getMemoId(){
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

}
