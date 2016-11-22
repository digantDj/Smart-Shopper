package com.group28.android.smartshopper.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.group28.android.smartshopper.Database.DBHelper;
import com.group28.android.smartshopper.Fragments.FragmentOne;
import com.group28.android.smartshopper.Fragments.FragmentTwo;
import com.group28.android.smartshopper.Model.Memo;
import com.group28.android.smartshopper.R;
import com.group28.android.smartshopper.Service.GeoFenceService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.group28.android.smartshopper.R.id.textView;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,
        View.OnClickListener{

    // Creating Instance for Tab Fragments
    public static HomeActivity instance;

    private FragmentOne fragmentOne;
    private FragmentTwo fragmentTwo;
    private TabLayout allTabs;

    ArrayAdapter<String> adapter;
    // List view
    private ListView lv;
    // Search EditText
    EditText inputSearch;

    // Navigation Header
    TextView navUserName;
    TextView navEmail;
    ImageView navImage;

    GoogleApiClient mGoogleApiClient;
    DBHelper dbHelper;
    ArrayList<String> memosList;

    // Custom variable to store currentTab
    private String currentTab;

    // For recieving logged-in user's email and userName
    public static final String MyPREFERENCES = "SmartShopper" ;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        try {
            dbHelper = DBHelper.getInstance(this.getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                //  .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentTab != null) {
                    if(currentTab == "INDIVIDUAL MEMO") {
                        Intent createMemoIntent = new Intent(HomeActivity.this, MemoActivity.class);
                        //Intent createMemoIntent = new Intent(HomeActivity.this, GroupMemoActivity.class);
                      //  createMemoIntent.putExtra("userEmail", getIntent().getStringExtra("userEmail"));
                        startActivity(createMemoIntent);
                    }
                    else{
                        Intent createMemoIntent = new Intent(HomeActivity.this, GroupMemoActivity.class);
                        //Intent createMemoIntent = new Intent(HomeActivity.this, GroupMemoActivity.class);
                      //  createMemoIntent.putExtra("userEmail", getIntent().getStringExtra("userEmail"));
                        startActivity(createMemoIntent);
                    }
                }
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Setting UserName in Navigation Menu
        View headerLayout = navigationView.getHeaderView(0); // 0-index header
        navUserName = (TextView) headerLayout.findViewById(R.id.userName);

        // navUserName.setText("Hello " + getIntent().getStringExtra("userName"));
        navUserName.setText("Hello "+ sharedpreferences.getString("userName","user"));
        navEmail = (TextView) headerLayout.findViewById(textView);
        //navEmail.setText(getIntent().getStringExtra("userEmail"));
        navEmail.setText(sharedpreferences.getString("email",""));

  /*      try {
            memosList = getMemos();
        }
        catch(NullPointerException e){
            e.printStackTrace();
        }
*/

        // Bind to our new adapter.
        lv = (ListView)findViewById(R.id.listview);
        inputSearch = (EditText) findViewById(R.id.inputSearch);


        adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.product_name, memosList);
    // Commented previously working adapter temp
        //    lv.setAdapter(adapter);

/*        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), MemoUpdateActivity.class);
                intent.putExtra("memoId", lv.getItemAtPosition(position).toString());
                startActivity(intent);
            }
        });
*/


    /*    inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                HomeActivity.this.adapter.getFilter().filter(cs);
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
        }); */

        // Handling Tab Layout
        instance=this;

        getAllWidgets();
        bindWidgetsWithAnEvent();
        setupTabLayout();

        /***
         * NEED TO CHANGE
         */
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    HomeActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    12345
            );
        }

        Intent geoFenceServiceIntent = new Intent(this, GeoFenceService.class);
        startService(geoFenceServiceIntent);
    }

    public static HomeActivity getInstance() {
        return instance;
    }

    private void getAllWidgets() {
        allTabs = (TabLayout) findViewById(R.id.tabs);
    }

    private void setupTabLayout() {
        fragmentOne = new FragmentOne(dbHelper, sharedpreferences.getString("email",""));
        fragmentTwo = new FragmentTwo(dbHelper, sharedpreferences.getString("email",""));

        allTabs.addTab(allTabs.newTab().setText("INDIVIDUAL MEMO"),true);
        allTabs.addTab(allTabs.newTab().setText("GROUP MEMO"));
    }

    private void bindWidgetsWithAnEvent()
    {
        allTabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setCurrentTabFragment(tab.getPosition());
                currentTab = tab.getText().toString();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setCurrentTabFragment(int tabPosition)
    {
        switch (tabPosition)
        {
            case 0 :
                replaceFragment(fragmentOne);
                break;
            case 1 :
                replaceFragment(fragmentTwo);
                break;
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_container, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }





    private Cursor getContacts() {
        // Run query
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = new String[] { ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME };
        String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '"
                + ("1") + "'";
        String[] selectionArgs = null;
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME
                + " COLLATE LOCALIZED ASC";

        return managedQuery(uri, projection, selection, selectionArgs,
                sortOrder);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.action_logout) {
            signOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_manage) {
            // Handle the Snooze action

        } else if (id == R.id.nav_share) {
            // Share preference

            Intent recommendationIntent = new Intent(HomeActivity.this, RecommendActivity.class);
            startActivity(recommendationIntent);
        } else if (id == R.id.nav_logout) {
            // Signout from the application
            signOut();
        } /* else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } */

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOut() {

      /*  GoogleApiClient mclient = App.getInstance().getClient();
        mclient.reconnect();
        Log.i("asfdad",mclient.isConnected() + "");

            Auth.GoogleSignInApi.signOut(mclient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            // [START_EXCLUDE]
                            //  updateUI(false);
                            // [END_EXCLUDE]
                        }
                    });

        //this way you have can use object of mGoogleApiClient anywhere in the app.
*/
        Log.i("Logout",mGoogleApiClient.isConnected() + "");

        // Clearing sharedpreferences after logout
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.commit();

        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        //  updateUI(false);
                        loadMainPage();
                        // [END_EXCLUDE]
                    }
                });

       // mGoogleApiClient.disconnect();

    }

    // Function to retrieve Memos from DB
    private ArrayList<String> getMemos(String type){
        // Querying Memos
        ArrayList<String> tempMemosList = new ArrayList<String>();
        try {
            List<Memo> memos = dbHelper.getMemos(dbHelper.getUserID(sharedpreferences.getString("email","")), type);

            int i=0;
            for(Memo memo : memos){
                tempMemosList.add(memo.getMemoId()+" "+memo.getCategory());
            }
            return tempMemosList;
        } catch(NullPointerException e) {
            e.printStackTrace();
        }
        return tempMemosList;
    }

    // Function to update List View
    private void updateListViewData() {
        try {
            ArrayList<String> newMemoList = getMemos("PERSONAL");
           /* adapter.clear();
            adapter.addAll(newMemoList);
            adapter.notifyDataSetChanged();
            */
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        //Updating Memo
        updateListViewData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void loadMainPage(){
        Intent mainIntent = new Intent(this, MainActivity.class);
       // homeIntent.putExtra(EXTRA_MESSAGE, googleSignInResult.getSignInAccount().getDisplayName()); // Adding message to invoke HomeActivity
        startActivity(mainIntent);
    }
}
