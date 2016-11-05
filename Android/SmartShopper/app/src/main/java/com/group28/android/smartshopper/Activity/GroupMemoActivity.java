package com.group28.android.smartshopper.Activity;

import android.app.Activity;
import android.content.Intent;
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


public class GroupMemoActivity extends Activity implements AdapterView.OnItemSelectedListener {


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_memo);
        save = (Button) findViewById(R.id.button);
        editText = (EditText)findViewById(R.id.editText);
        participantText = (EditText)findViewById(R.id.editText2);
        memo = new Memo();
        participant = new Participant();

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
                //       Toast.makeText(MemoActivity.this, content, Toast.LENGTH_LONG).show();
                //       Toast.makeText(MemoActivity.this, category, Toast.LENGTH_SHORT).show();
                content = editText.getText().toString();
                participants = participantText.getText().toString().split(",");
                // memo.setMemoId(UUID.randomUUID().toString());
                memo.setCategory(category);
                memo.setUserId(dbHelper.getUserID(getIntent().getStringExtra("userEmail")));
                memo.setContent(content);
                memo.setType("GROUP");
                memo.setStatus("ACTIVE");

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost("http://smartshop-raredev.rhcloud.com/create_memo");

                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("userid", dbHelper.getUserID(getIntent().getStringExtra("userEmail")));
                    jsonObj.put("memoid", 1);
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
                    for(String email:participants) {
                        jsonObj = new JSONObject();
                        jsonObj.put("memoid", 1);
                        jsonObj.put("email", email);
                        entity = new StringEntity(jsonObj.toString(), HTTP.UTF_8);
                        entity.setContentType("application/json");
                        httpPost.setEntity(entity);
                        new GroupMemoActivity.CreateMemo().execute(httpClient, httpPost);
                    }
                }catch (IOException ioe){

                }catch(JSONException je){

                }
                    for(String email:participants){
                    participant.setMemoId(1);
                    participant.setEmail(email);
                    dbHelper.insertParticipants(participant,"participants");
                }
                if (dbHelper.insertMemo(memo, "memo")) {
                    //Toast.makeText(MemoActivity.this, "Insert Successful", Toast.LENGTH_SHORT).show();
                    // Redirect User to Home Screen upon successful insertion
                    Intent i = new Intent(GroupMemoActivity.this, MainActivity.class);
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
