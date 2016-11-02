package com.group28.android.smartshopper.Activity;

import android.app.Activity;
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
import java.util.UUID;

public class MemoActivity extends Activity implements AdapterView.OnItemSelectedListener {

    DBHelper dbHelper;
    String category;
    String content;
    Button save;
    Memo memo;
    EditText editText;

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



        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MemoActivity.this, content, Toast.LENGTH_LONG).show();
                Toast.makeText(MemoActivity.this, category, Toast.LENGTH_SHORT).show();
                content = editText.getText().toString();
               // memo.setMemoId(UUID.randomUUID().toString());
                memo.setCategory(category);
                memo.setUserId(0);
                memo.setContent(content);
                memo.setType("PERSONAL");
                memo.setStatus("ACTIVE");
                if (dbHelper.insertMemo(memo, "memo")) {
                    Toast.makeText(MemoActivity.this, "Insert Successful", Toast.LENGTH_SHORT).show();
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
