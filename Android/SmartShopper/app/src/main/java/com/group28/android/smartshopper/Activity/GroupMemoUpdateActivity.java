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


public class GroupMemoUpdateActivity extends Activity implements AdapterView.OnItemSelectedListener {

    public  class UpdateMemo extends AsyncTask<Object, Void, Object> {
        protected Boolean doInBackground(Object... param) {
            HttpResponse response = null;
            HttpClient httpClient= (HttpClient)param[0];
            HttpPost httpPost = (HttpPost)param[1];
            try {
                response = httpClient.execute(httpPost);
                int statusCode = response.getStatusLine().getStatusCode();
                final String responseBody = EntityUtils.toString(response.getEntity());
                Log.i(TAG, "Updated memo: " + responseBody + "StatucCode: " + statusCode);
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

    private static final String TAG = "GroupUpdateMemoActivity";
    DBHelper dbHelper;
    String category;
    String content;
    Button save;
    Memo memo;
    EditText editText;
    EditText participantText;
    String participants;
    String[] participantNames;
    Participant participant;
    Intent intent;

    // For recieving logged-in user's email and userName
    public static final String MyPREFERENCES = "SmartShopper" ;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_memo_update);
        save = (Button) findViewById(R.id.button);
        editText = (EditText)findViewById(R.id.editText);
        participantText = (EditText)findViewById(R.id.editText2);
        intent = getIntent();
        participant = new Participant();
        // String[] itemDetails = intent.getStringExtra("memoId").toString().split(" ");
        // final int memoId = Integer.parseInt(itemDetails[0]);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        Bundle data = getIntent().getExtras();
        Memo memoIntent = data.getParcelable("memo");
        final int memoId = memoIntent.getGroupMemoId();

        try {
            dbHelper = DBHelper.getInstance(this);
            dbHelper.onCreateMemoTable("memo");
            dbHelper.onCreateParticipantsTable("participants");
        } catch (IOException e) {
            e.printStackTrace();
        }
        // memo = dbHelper.getMemoWithMemoId(memoId,"GROUP");
        memo = memoIntent;
        participants = dbHelper.getParticipantsWithMemoId(memoId);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.menu_items, R.layout.activity_memospinnerview);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(adapter.getPosition(memo.getCategory()));
        editText.setText(memo.getContent());
        participantText.setText(participants);


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //       Toast.makeText(MemoActivity.this, content, Toast.LENGTH_LONG).show();
                //       Toast.makeText(MemoActivity.this, category, Toast.LENGTH_SHORT).show();
                content = editText.getText().toString();
                // memo.setMemoId(UUID.randomUUID().toString());
                participantNames = participantText.getText().toString().trim().split(",");
                memo.setCategory(category);
                //memo.setUserId(dbHelper.getUserID(getIntent().getStringExtra("userEmail")));
                memo.setContent(content);
/*              memo.setType("GROUP");
                memo.setStatus("ACTIVE");*/

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost("http://smartshop-raredev.rhcloud.com/update_memo");

                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("groupmemoid", memoId);
                    jsonObj.put("category", category);
                    jsonObj.put("content", content);
                    StringEntity entity = new StringEntity(jsonObj.toString(), HTTP.UTF_8);
                    entity.setContentType("application/json");
                    httpPost.setEntity(entity);
                    new GroupMemoUpdateActivity.UpdateMemo().execute(httpClient,httpPost);

                    httpClient = new DefaultHttpClient();
                    httpPost = new HttpPost("http://smartshop-raredev.rhcloud.com/edit_participants");
                    jsonObj = new JSONObject();
                    jsonObj.put("memoid", memoId);
                    jsonObj.put("email", participantText.getText().toString().trim());
                    //jsonObj.put("email", sharedpreferences.getString("email",""));
                    entity = new StringEntity(jsonObj.toString(), HTTP.UTF_8);
                    entity.setContentType("application/json");
                    httpPost.setEntity(entity);
                    new GroupMemoUpdateActivity.UpdateMemo().execute(httpClient, httpPost);

                }catch (IOException ioe){
                    ioe.printStackTrace();
                }catch(JSONException je){
                    je.printStackTrace();
                }
                if(!dbHelper.deleteParticipants(memoId)){
                    Log.i(TAG,"Error updating participants");
                    return;
                }
                for(String email:participantNames){
                    participant.setMemoId(memoId);
                    participant.setEmail(email);
                    dbHelper.insertParticipants(participant,"participants");
                }
                if (dbHelper.updateMemo(memo, "memo","GROUP")) {
                    Intent i = new Intent(GroupMemoUpdateActivity.this, HomeActivity.class);
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
