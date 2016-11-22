package com.group28.android.smartshopper.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.group28.android.smartshopper.Database.DBHelper;
import com.group28.android.smartshopper.Model.Memo;
import com.group28.android.smartshopper.Model.Participant;
import com.group28.android.smartshopper.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static com.google.android.gms.analytics.internal.zzy.j;


public class GroupMemoActivity extends Activity implements AdapterView.OnItemSelectedListener {

    public class MemoId extends AsyncTask<Object, Void, Object>{

        @Override
        protected Object doInBackground(Object... param) {
            HttpResponse response = null;
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://smartshop-raredev.rhcloud.com/max_memoid");
            try {
                response = httpClient.execute(httpPost);
                final String responseBody = EntityUtils.toString(response.getEntity());
                return responseBody;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }


    public  class CreateMemo extends AsyncTask<Object, Void, Object> {
        protected Boolean doInBackground(Object... param) {
            HttpResponse response = null;
            HttpClient httpClient= (HttpClient)param[0];
            HttpPost httpPost = (HttpPost)param[1];
            try {
                response = httpClient.execute(httpPost);
                int statusCode = response.getStatusLine().getStatusCode();
                final String responseBody = EntityUtils.toString(response.getEntity());
                Log.i(TAG, "Created memo: " + responseBody + "StatucCode: " + statusCode);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        protected void onProgressUpdate()
        {
            //setProgressPercent(progress[0]);
        }

        protected void onPostExecute(boolean result)
        {
            //showDialog("Downloaded " + result + " bytes");
        }
    }

    private static final String TAG = "GroupMemoActivity";
    DBHelper dbHelper;
    String category;
    String content;
    Button save;
    Memo memo;
    EditText editText;
    EditText participantText;
    String[] participants;
    Participant participant;
    int maxMemoId = 0;

    public static final String MyPREFERENCES = "SmartShopper" ;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_memo);
        save = (Button) findViewById(R.id.button);
        editText = (EditText)findViewById(R.id.editText);
        participantText = (EditText)findViewById(R.id.editText2);
        memo = new Memo();
        participant = new Participant();

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        try {
            dbHelper = DBHelper.getInstance(this);
            dbHelper.onCreateMemoTable("memo");
            dbHelper.onCreateParticipantsTable("participants");
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.menu_items, R.layout.activity_memospinnerview);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);



        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content = editText.getText().toString();
                participants = participantText.getText().toString().split(",");
                MemoId memoTask = new MemoId();
                try {
                    maxMemoId = Integer.parseInt(memoTask.execute().get().toString());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (NullPointerException ne){
                    maxMemoId = 0;
                }
                int memoId = maxMemoId+1;
                memo.setGroupMemoId(memoId);
                memo.setCategory(category);
              //  memo.setUserId(dbHelper.getUserID(getIntent().getStringExtra("userEmail")));
                memo.setUserId(dbHelper.getUserID(sharedpreferences.getString("email","")));
                memo.setContent(content);
                memo.setType("GROUP");
                memo.setStatus("ACTIVE");

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost("http://smartshop-raredev.rhcloud.com/create_memo");
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("userid", dbHelper.getUserID(sharedpreferences.getString("email","")));
                    jsonObj.put("memoid", 0);
                    jsonObj.put("groupmemoid", memoId);
                    jsonObj.put("category", category);
                    jsonObj.put("content", content);
                    jsonObj.put("type", "GROUP");
                    jsonObj.put("status", "ACTIVE");
                    StringEntity entity = new StringEntity(jsonObj.toString(), HTTP.UTF_8);
                    entity.setContentType("application/json");
                    httpPost.setEntity(entity);
                    new GroupMemoActivity.CreateMemo().execute(httpClient,httpPost);

                    httpClient = new DefaultHttpClient();
                    httpPost = new HttpPost("http://smartshop-raredev.rhcloud.com/create_participants");
                    jsonObj = new JSONObject();
                    jsonObj.put("memoid", memoId);
                    jsonObj.put("email", sharedpreferences.getString("email","")+","+participantText.getText().toString().trim());
                    entity = new StringEntity(jsonObj.toString(), HTTP.UTF_8);
                    entity.setContentType("application/json");
                    httpPost.setEntity(entity);
                    new GroupMemoActivity.CreateMemo().execute(httpClient, httpPost);




                   //send notification to participants
                    httpClient = new DefaultHttpClient();
                    httpPost = new HttpPost("http://smartshop-raredev.rhcloud.com/send_push");
                    for(String email:participants){
                        jsonObj = new JSONObject();
                        jsonObj.put("type", "GroupMemo");
                        jsonObj.put("email", email);
                        jsonObj.put("title", "Group Memo");
                        jsonObj.put("message", sharedpreferences.getString("email","") + " updated a Group Memo - "+ category + ":" + content);
                        entity = new StringEntity(jsonObj.toString(), HTTP.UTF_8);
                        entity.setContentType("application/json");
                        httpPost.setEntity(entity);
                        new GroupMemoActivity.CreateMemo().execute(httpClient, httpPost);
                    }

                }catch (IOException ioe){

                }catch(JSONException je){

                }
                participant.setMemoId(memoId);
               // participant.setEmail(getIntent().getStringExtra("userEmail"));
                participant.setEmail(sharedpreferences.getString("email",""));
                dbHelper.insertParticipants(participant,"participants");
                for(String email:participants){
                    participant.setMemoId(memoId);
                    participant.setEmail(email);
                    dbHelper.insertParticipants(participant,"participants");
                }
                if (dbHelper.insertMemo(memo, "memo")) {
                    Intent i = new Intent(GroupMemoActivity.this, HomeActivity.class);
                    startActivity(i);
                    finish();
                }

            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        category = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), category, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(parent.getContext(), "Select a category", Toast.LENGTH_LONG).show();
    }
}
