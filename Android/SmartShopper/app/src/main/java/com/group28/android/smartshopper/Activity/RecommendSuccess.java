package com.group28.android.smartshopper.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.group28.android.smartshopper.Database.DBHelper;
import com.group28.android.smartshopper.Model.Memo;
import com.group28.android.smartshopper.Model.Preference;
import com.group28.android.smartshopper.R;

import java.io.IOException;
import java.util.ArrayList;

public class RecommendSuccess extends AppCompatActivity {

    Preference preferenceObj;
    DBHelper dbHelper;
    TextView categoryTextView;

    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_success);

        try {
            dbHelper = DBHelper.getInstance(this.getApplicationContext());
            preferenceObj = new Preference();
            sharedPreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
            preferenceObj.setCategory(getIntent().getStringExtra("category"));
            preferenceObj.setShoppingPreference(getIntent().getStringExtra("place"));

            ArrayList<Preference> preferenceArrayList = dbHelper.getPreferencesFromCategory(dbHelper.getUserID(sharedPreferences.getString("email","")), getIntent().getStringExtra("category"));
            if(preferenceArrayList.size() > 0) {
                dbHelper.updatePreference(preferenceObj, RecommendActivity.preferenceTableName);
            }
            else{
                dbHelper.insertPreferences(preferenceObj, RecommendActivity.preferenceTableName);
            }
            categoryTextView = (TextView) findViewById(R.id.textView4);
            categoryTextView.setText(getIntent().getStringExtra("category") + "->" + getIntent().getStringExtra("place") );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
