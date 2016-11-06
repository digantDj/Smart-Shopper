package com.group28.android.smartshopper.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;

import com.group28.android.smartshopper.Activity.HomeActivity;
import com.group28.android.smartshopper.Activity.MemoUpdateActivity;
import com.group28.android.smartshopper.Adapter.ListViewAdapter;
import com.group28.android.smartshopper.Database.DBHelper;
import com.group28.android.smartshopper.Model.Memo;
import com.group28.android.smartshopper.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.group28.android.smartshopper.R.id.inputSearch;

/**
 * Created by digantjagtap on 11/4/16.
 */
public class FragmentOne extends Fragment implements SearchView.OnQueryTextListener {

    private ListView listView;
    TypedArray allMemos;
    DBHelper dbHelper;
    ArrayList<Memo> memosList;
    String currentUserEmail;
    ListViewAdapter listViewAdapter;
    private SearchView mSearchView;

    // Memo Search
    EditText memoSearch;

    public FragmentOne() {

    }

    @SuppressLint("ValidFragment")
    public FragmentOne(DBHelper dbHelper, String currentUserEmail) {
        this.dbHelper = dbHelper;
        this.currentUserEmail = currentUserEmail;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_one, null);
        getAllWidgets(rootView);

        setAdapter();
        //setMemoSearch();
        return rootView;
    }

    private void getAllWidgets(View view) {
        listView = (ListView) view.findViewById(R.id.listFragmentOne);

      //  allMemos = getResources().obtainTypedArray(R.array.all_contacts);

    }

    private void setAdapter()
    {
        /*
        for (int i = 0; i < allMemos.length(); i++) {
            allContactNames.add(allMemos.getString(i));
        }
        */
        // Getting Memos
        try {
            memosList = getMemos();
        }
        catch(NullPointerException e){
            e.printStackTrace();
        }


        listViewAdapter= new ListViewAdapter(HomeActivity.getInstance(), memosList);
        listView.setAdapter(listViewAdapter);
        listView.setTextFilterEnabled(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), MemoUpdateActivity.class);
                intent.putExtra("memo", memosList.get(position));
                //intent.putExtra("memoId", memosList.get(position).getMemoId());
                intent.putExtra("userEmail", HomeActivity.getInstance().getIntent().getStringExtra("userEmail"));
                // listView.getItemAtPosition(position)
                startActivity(intent);
            }
        });

        setupSearchView();

    }

    private void setupSearchView()
    {
        mSearchView=(SearchView) HomeActivity.getInstance().findViewById(R.id.searchView1);
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setQueryHint("Search Memos");
    }

    /*
    private void setMemoSearch() {
        memoSearch = (EditText) HomeActivity.getInstance().findViewById(R.id.memoSearch);

        memoSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                listViewAdapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

            }
        }
    }
    */

    // Function to retrieve Memos from DB
    private ArrayList<Memo> getMemos(){
        // Querying Memos
        ArrayList<Memo> tempMemosList = new ArrayList<Memo>();
        try {

            List<Memo> memos = dbHelper.getMemos(dbHelper.getUserID(currentUserEmail), "PERSONAL");

            int i=0;
            for(Memo memo : memos){
                //tempMemosList.add(memo.getMemoId()+" "+memo.getCategory());
                tempMemosList.add(memo);
            }
            return tempMemosList;
        } catch(NullPointerException e) {
            e.printStackTrace();
        }
        return tempMemosList;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            listView.clearTextFilter();
        } else {
            listView.setFilterText(newText);
        }
        return true;
    }
}
