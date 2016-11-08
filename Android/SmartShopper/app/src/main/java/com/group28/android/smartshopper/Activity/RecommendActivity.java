package com.group28.android.smartshopper.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.group28.android.smartshopper.Database.DBHelper;
import com.group28.android.smartshopper.Model.Preference;
import com.group28.android.smartshopper.R;

import java.io.IOException;
import java.util.ArrayList;

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
            dbHelper.onCreatePreferences(preferenceTableName);
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
        if(isNotSet){
            Preference preference = new Preference();
            preference.setCategory(spinner.getSelectedItem().toString());
            preference.setUserId(dbHelper.getUserID(sharedpreferences.getString("email","")));
            preference.setShoppingPreference(place.getText().toString());
            dbHelper.insertPreferences(preference,preferenceTableName);
        }
        else{
            // Called when preference already set

        }
        Intent homeIntent = new Intent(this, HomeActivity.class);
        startActivity(homeIntent);
    }

}
