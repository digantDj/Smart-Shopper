package com.group28.android.smartshopper.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.group28.android.smartshopper.Database.DBHelper;
import com.group28.android.smartshopper.Model.Memo;
import com.group28.android.smartshopper.R;

import java.io.IOException;

public class MemoActivity extends Activity implements AdapterView.OnItemSelectedListener {

    DBHelper dbHelper;
    String category;
    String content;
    Button save;
    Memo memo;
    EditText editText;
    private static final String TAG = "MemoActivity";
    private int maxMemoId = 0;
    public static final String MyPREFERENCES = "SmartShopper" ;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);
        save = (Button) findViewById(R.id.button);
        editText = (EditText)findViewById(R.id.editText);
        memo = new Memo();

        try {
            dbHelper = DBHelper.getInstance(this);
            dbHelper.onCreateMemoTable("memo");
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.menu_items, R.layout.activity_memospinnerview);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content = editText.getText().toString();
                maxMemoId = dbHelper.getMaxMemoId();
                int memoid = maxMemoId+1;
                memo.setMemoId(memoid);
                memo.setCategory(category);
                memo.setUserId(dbHelper.getUserID(sharedpreferences.getString("email","")));
                memo.setContent(content);
                memo.setType("PERSONAL");
                memo.setStatus("ACTIVE");
                if (dbHelper.insertMemo(memo, "memo")) {
                    Intent i = new Intent(MemoActivity.this, HomeActivity.class);
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
