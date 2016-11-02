package com.group28.android.smartshopper.Activity;

import android.app.Activity;
import android.content.Intent;
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

public class MemoUpdateActivity extends Activity implements AdapterView.OnItemSelectedListener {

    DBHelper dbHelper;
    String category;
    String content;
    Button update;
    Memo memo;
    EditText editText;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_update);
        update = (Button) findViewById(R.id.button);
        editText = (EditText)findViewById(R.id.editText);
        //memo = new Memo();
        intent = getIntent();
        String[] itemDetails = intent.getStringExtra("memoId").toString().split(" ");
        int memoId = Integer.parseInt(itemDetails[0]);

        try {
            dbHelper = DBHelper.getInstance(this);
            dbHelper.onCreateMemoTable("memo");
        } catch (IOException e) {
            e.printStackTrace();
        }

        memo = dbHelper.getMemoWithMemoId(memoId);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.menu_items, R.layout.activity_memospinnerview);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(adapter.getPosition(memo.getCategory()));

        editText.setText(memo.getContent());


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MemoUpdateActivity.this, content, Toast.LENGTH_LONG).show();
                Toast.makeText(MemoUpdateActivity.this, category, Toast.LENGTH_SHORT).show();
                content = editText.getText().toString();
               // memo.setMemoId(UUID.randomUUID().toString());
                memo.setCategory(category);
                memo.setContent(content);
                //memo.setType("PERSONAL");
                //memo.setStatus("ACTIVE");

                if (dbHelper.updateMemo(memo, "memo")) {
                        //Toast.makeText(MemoActivity.this, "Insert Successful", Toast.LENGTH_SHORT).show();
                        // Redirect User to Home Screen upon successful insertion
                        Intent i = new Intent(MemoUpdateActivity.this, MainActivity.class);
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
