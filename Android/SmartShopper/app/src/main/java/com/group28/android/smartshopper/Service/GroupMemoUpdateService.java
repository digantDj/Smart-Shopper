package com.group28.android.smartshopper.Service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.group28.android.smartshopper.Activity.MainActivity;
import com.group28.android.smartshopper.Database.DBHelper;
import com.group28.android.smartshopper.Model.Memo;
import com.group28.android.smartshopper.Model.Participant;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


/**
 * Created by deepika on 11/6/2016.
 */

public class GroupMemoUpdateService extends IntentService {

    //Class constructor
    public GroupMemoUpdateService() {
        super("");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        updateGroupMemo();
    }

    private void updateGroupMemo() {
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://smartshop-raredev.rhcloud.com/participant_details");
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("email", getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE).getString("email",""));
            StringEntity entity = new StringEntity(jsonObj.toString(), HTTP.UTF_8);
            entity.setContentType("application/json");
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost);
            String json = EntityUtils.toString(response.getEntity());
            if(null == json)
                return;
            JSONArray jsonArray = new JSONArray(json);
            if(null == jsonArray)
                return;
            //DBHelper.getInstance(this).deleteParticipants(jsonArray.getJSONObject(0).getInt("memoid"));
            for(int i=0;i<jsonArray.length();++i) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if(!(DBHelper.getInstance(this).isParticipantPresent(jsonObject.getInt("memoid"),jsonObject.getString("email")))){
                    Participant participant = new Participant();
                    participant.setMemoId(jsonObject.getInt("memoid"));
                    participant.setEmail(jsonObject.getString("email"));
                    DBHelper.getInstance(this).insertParticipants(participant,"participants");
                }
                httpPost = new HttpPost("http://smartshop-raredev.rhcloud.com/memo_details");
                jsonObj = new JSONObject();
                jsonObj.put("groupmemoid", jsonObject.getInt("memoid"));
                entity = new StringEntity(jsonObj.toString(), HTTP.UTF_8);
                entity.setContentType("application/json");
                httpPost.setEntity(entity);
                response = httpClient.execute(httpPost);
                String memoJson = EntityUtils.toString(response.getEntity());
                if(null == memoJson)
                    return;
                //JSONArray jsonMemoArray = new JSONArray(memoJson);
                //if(null == jsonMemoArray)
                  //  return;
                //for(int j=0;j<jsonMemoArray.length();++j) {
                    //JSONObject jsonMemoObject = jsonMemoArray.getJSONObject(j);
                    JSONObject jsonMemoObject = new JSONObject(memoJson);
                    Memo memo;
                    memo = DBHelper.getInstance(this).getMemoWithMemoId(jsonMemoObject.getInt("groupmemoid"),"GROUP");
                    if(null != memo && null != memo.getCategory()){
                        memo.setUserId(DBHelper.getInstance(this).getUserID(getSharedPreferences(MainActivity.MyPREFERENCES,Context.MODE_PRIVATE).getString("email","")));
                        memo.setGroupMemoId(jsonMemoObject.getInt("groupmemoid"));
                        memo.setType("GROUP");
                        memo.setCategory(jsonMemoObject.getString("category"));
                        memo.setContent(jsonMemoObject.getString("content"));
                        memo.setStatus("Active");
                        DBHelper.getInstance(this).updateMemo(memo,"memo","GROUP");
                    }else{
                        memo = new Memo();
                        memo.setUserId(DBHelper.getInstance(this).getUserID(getSharedPreferences(MainActivity.MyPREFERENCES,Context.MODE_PRIVATE).getString("email","")));
                        memo.setGroupMemoId(jsonMemoObject.getInt("groupmemoid"));
                        memo.setType("GROUP");
                        memo.setCategory(jsonMemoObject.getString("category"));
                        memo.setContent(jsonMemoObject.getString("content"));
                        memo.setStatus("Active");
                        DBHelper.getInstance(this).insertMemo(memo,"memo");
                    }


            }

        } catch (IOException e) {
            e.printStackTrace();
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
}