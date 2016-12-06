package com.group28.android.smartshopper.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.group28.android.smartshopper.Database.DBHelper;
import com.group28.android.smartshopper.R;

import java.io.IOException;

import static android.R.attr.category;
import static com.group28.android.smartshopper.R.id.editText;

public class SmartSearch extends AppCompatActivity  implements AdapterView.OnItemSelectedListener {

    DBHelper dbHelper;

    private static final String TAG = "MemoActivity";
    String category;
    public static final String MyPREFERENCES = "SmartShopper" ;
    SharedPreferences sharedpreferences;
    Button search;
    String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_search);

        search = (Button) findViewById(R.id.button2);
        try {
            dbHelper = DBHelper.getInstance(this);

        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.menu_items, R.layout.activity_memospinnerview);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        Spinner spinner = (Spinner) findViewById(R.id.spinner2);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  content = editText.getText().toString();
                    Intent i = new Intent(SmartSearch.this, SmartSearchResult.class);
                    i.putExtra("category", category);
                    startActivity(i);
                    finish();
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
