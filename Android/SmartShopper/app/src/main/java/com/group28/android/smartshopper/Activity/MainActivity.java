package com.group28.android.smartshopper.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.group28.android.smartshopper.Database.DBHelper;
import com.group28.android.smartshopper.Model.User;
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

public class MainActivity extends AppCompatActivity  implements
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,
        View.OnClickListener {

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public  class Register extends AsyncTask<Object, Void, Object> {
        protected Boolean doInBackground(Object... param) {
            HttpResponse response = null;
            HttpClient httpClient= (HttpClient)param[0];
            HttpPost httpPost = (HttpPost)param[1];
            try {
                response = httpClient.execute(httpPost);
                int statusCode = response.getStatusLine().getStatusCode();
                final String responseBody = EntityUtils.toString(response.getEntity());
                Log.i(TAG, "Signed in as: " + responseBody + "StatucCode: " + statusCode);
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


    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;
    private TextView mStatusTextView;
    private ProgressDialog mProgressDialog;

    public final static String EXTRA_MESSAGE = "com.group28.android.smartshopper.HOMEMESSAGE";

    private static DBHelper dbHelper;
    private User dbUser;
    private static final String USERTABLE = "user";
    private static final String MEMOTABLE = "memo";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Views
        mStatusTextView = (TextView) findViewById(R.id.status);

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.disconnect_button).setOnClickListener(this);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // [END configure_signin]

        // Customize sign-in button. The sign-in button can be displayed in
// multiple sizes and color schemes. It can also be contextually
// rendered based on the requested scopes. For example. a red button may
// be displayed when Google+ scopes are requested, but a white button
// may be displayed when only basic profile is requested. Try adding the
// Scopes.PLUS_LOGIN scope to the GoogleSignInOptions to see the
// difference.
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());

        mGoogleApiClient = new GoogleApiClient.Builder(this)
              //  .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        Log.i(TAG,mGoogleApiClient.isConnected() + "");
       // App.getInstance().setClient(mGoogleApiClient);

        try {
            // Get Instance of DB

            dbHelper = dbHelper.getInstance(this.getApplicationContext());
            dbHelper.onCreateUserTable(USERTABLE);
            dbHelper.onCreateMemoTable(MEMOTABLE);
            dbUser = new User();
        }
        catch(IOException e){
            Log.e("error","DBHelper getInstance error");
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        final GoogleSignInResult result;
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            result = opr.get();
          //  handleSignInResult(result);
            loadHomePage(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                   handleSignInResult(googleSignInResult);
                 //   loadHomePage(googleSignInResult);
                }

            });
       }

        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
            if(result.isSuccess()) {
                loadHomePage(result);
            }
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            //String idToken = acct.getIdToken();
            /* Send User ID to Server
            * user's email address with getEmail,
            * the user's Google ID (for client-side use) with getId,
             * and an ID token for the user with with getIdToken
            * */
            String userName = acct.getDisplayName();
            String email = acct.getEmail();
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.server_client_id))
                    .build();
            Log.d(TAG,acct.toString());

            final HttpClient httpClient = new DefaultHttpClient();
            final HttpPost httpPost = new HttpPost("http://smartshop-raredev.rhcloud.com/register");

            try {
                /*List nameValuePairs = new ArrayList(1);
                nameValuePairs.add(new BasicNameValuePair("username", userName));
                nameValuePairs.add(new BasicNameValuePair("token", userName));
                nameValuePairs.add(new BasicNameValuePair("email", email));
*/
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("username", userName);
                jsonObj.put("token", userName);
                jsonObj.put("email", email);
                StringEntity entity = new StringEntity(jsonObj.toString(), HTTP.UTF_8);
                entity.setContentType("application/json");
                httpPost.setEntity(entity);
                //httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                new Register().execute(httpClient,httpPost);

                // Store User details in Local DB
                dbUser.setUserName(userName);
                dbUser.setEmail(email);
                dbUser.setToken(userName);
                dbHelper.insertUser(dbUser,USERTABLE);
            /*
                // Creating DUMMY data
                Memo dummyMemo = new Memo();
                dummyMemo.setCategory("Grocery");
                dummyMemo.setContent("Milk, Eggs");
                dummyMemo.setUserId(dbHelper.getUserID(email));
                dummyMemo.setStatus("Active");
                dbHelper.insertMemo(dummyMemo, MEMOTABLE);
                */
            }  catch (IOException e) {
                Log.e(TAG, "Error sending ID token to backend.", e);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }

    public void loadHomePage(GoogleSignInResult googleSignInResult){
        Intent homeIntent = new Intent(this, HomeActivity.class);
     //   homeIntent.putExtra(EXTRA_MESSAGE, googleSignInResult.getSignInAccount().getDisplayName()); // Adding message to invoke HomeActivity
        homeIntent.putExtra("userName",googleSignInResult.getSignInAccount().getDisplayName());
        homeIntent.putExtra("userEmail",googleSignInResult.getSignInAccount().getEmail());

       //Log.i("adsadasd",googleSignInResult.getSignInAccount().getPhotoUrl().toString());
        //homeIntent.putExtra("userPhoto",googleSignInResult.getSignInAccount().getPhotoUrl().toString());

        startActivity(homeIntent);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    // [START signIn]
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
// [END signIn]

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }

    // [START revokeAccess]
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
// [END revokeAccess]

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }



    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
        } else {
            mStatusTextView.setText(R.string.signed_out);

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.sign_out_button:
                signOut();
                break;
            case R.id.disconnect_button:
                revokeAccess();
                break;
        }
    }
}
