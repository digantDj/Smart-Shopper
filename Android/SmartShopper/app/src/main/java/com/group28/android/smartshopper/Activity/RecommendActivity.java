package com.group28.android.smartshopper.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.group28.android.smartshopper.Database.DBHelper;
import com.group28.android.smartshopper.Model.Preference;
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
import java.util.ArrayList;

import static android.R.attr.category;
import static android.content.ContentValues.TAG;
import static com.group28.android.smartshopper.R.id.spinner;

public class RecommendActivity extends Activity implements AdapterView.OnItemSelectedListener {

    DBHelper dbHelper;
    public static final String MyPREFERENCES = "SmartShopper" ;
    public static final String preferenceTableName = "preferences";
    SharedPreferences sharedpreferences;

    // UI Elements
    TextView place;
    Button recommendButton;
    Spinner spinner;

    Boolean isNotSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        // Set options for Spinner Dropdown
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.menu_items, R.layout.activity_memospinnerview);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner = (Spinner) findViewById(R.id.categorySpinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        // Retrieve email and username using Shared Preferences
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        // DB Helper
        try {
            dbHelper = DBHelper.getInstance(this);
            //dbHelper.onCreatePreferences(preferenceTableName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Initialize UI Elements
        place = (TextView) findViewById(R.id.recommendationText);
        recommendButton = (Button) findViewById(R.id.buttonShare);

        isNotSet = true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        isNotSet = true;
        ArrayList<Preference> temp = dbHelper.getPreferences(dbHelper.getUserID(sharedpreferences.getString("email","")));
//        Log.i("a",((TextView) view).getText().toString());
        if((temp.size() > 0)){
            for (Preference p: temp) {
                if(p.getCategory().toString().equals(spinner.getSelectedItem().toString())) {
                    System.out.print(p.getCategory().toString() + "\n   ");
                    place.setText(p.getShoppingPreference().toString());
                    isNotSet = false;
                    break;
                }
                else{
                    place.setText("");
                }
            }
            //place.setText("asd");
        }
        else{
            place.setText("");
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    public void ButtonOnClick(View v) {
        switch (v.getId()) {
            case R.id.buttonShare:
                sendRecommendation();
                break;
        }
    }

    // Function to actually send recommendation
    private void sendRecommendation(){
        Preference preference = new Preference();
        preference.setCategory(spinner.getSelectedItem().toString());
        preference.setUserId(dbHelper.getUserID(sharedpreferences.getString("email","")));
        preference.setShoppingPreference(place.getText().toString());
        boolean queryResult;
        if(isNotSet){
            // Called when preference is not set
            queryResult = dbHelper.insertPreferences(preference,preferenceTableName);
        }
        else{
            // Called when preference already set
            queryResult = dbHelper.updatePreference(preference,preferenceTableName);
        }

        if(queryResult){

            try {
                // Send Push Notification to Reciever
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://smartshop-raredev.rhcloud.com/send_push");
                EditText email = (EditText) findViewById(R.id.shareEmail);
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("email", email.getText());
                jsonObj.put("title", "Recommendation from a friend");
                jsonObj.put("message", sharedpreferences.getString("email", "") + " recommends you to try out " + place.getText().toString() + " for " + spinner.getSelectedItem().toString());
                jsonObj.put("category", spinner.getSelectedItem().toString());
                jsonObj.put("place", place.getText().toString());
                StringEntity entity = new StringEntity(jsonObj.toString(), HTTP.UTF_8);
                entity.setContentType("application/json");
                httpPost.setEntity(entity);
                new RecommendTaskService().execute(httpClient, httpPost);
            }
            catch(JSONException j){

            }catch(IOException e){

            }
            Intent homeIntent = new Intent(this, HomeActivity.class);
            startActivity(homeIntent);
        }
        else{
            Toast.makeText(this, "Error processing recommendation in DB. Try again later.", Toast.LENGTH_SHORT).show();
        }
    }

    public  class RecommendTaskService extends AsyncTask<Object, Void, Object> {
        protected Boolean doInBackground(Object... param) {
            HttpResponse response = null;
            HttpClient httpClient= (HttpClient)param[0];
            HttpPost httpPost = (HttpPost)param[1];
            try {
                response = httpClient.execute(httpPost);
                int statusCode = response.getStatusLine().getStatusCode();
                final String responseBody = EntityUtils.toString(response.getEntity());
                Log.i(TAG, "Sent recommendation: " + responseBody + "StatucCode: " + statusCode);
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

}
