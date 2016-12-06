package com.group28.android.smartshopper.Activity;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.group28.android.smartshopper.AppController;
import com.group28.android.smartshopper.Model.Place;
import com.group28.android.smartshopper.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.google.android.gms.analytics.internal.zzy.n;
import static com.group28.android.smartshopper.Service.GeoFenceService.GOOGLE_BROWSER_API_KEY;
import static com.group28.android.smartshopper.Service.GeoFenceService.OK;
import static com.group28.android.smartshopper.Service.GeoFenceService.PROXIMITY_RADIUS;
import static com.group28.android.smartshopper.Service.GeoFenceService.RESULT;
import static com.group28.android.smartshopper.Service.GeoFenceService.ZERO_RESULTS;

public class SmartSearchResult extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{


    private GoogleApiClient mGoogleApiClient;

    private static final String TAG = "SmartSearch";
    private double latitude;
    private double longitude;

    private List<Place> places;

    public static final String OK = "OK";

    public static final String GEOMETRY = "geometry";
    public static final String RESULT = "results";
    public static final String TYPE = "types";
    public static final String LOCATION = "location";
    public static final String LATITUDE = "lat";
    public static final String LONGITUDE = "lng";
    public static final String PLACE_ID = "place_id";
    public static final String SUPERMARKET_ID = "id";
    public static final String NAME = "name";
    public static final String ZERO_RESULTS = "ZERO_RESULTS";
    public static final String STATUS = "status";

    HashMap<String,String> typesMap = new HashMap<String,String>();

    TextView resultTv;

    String categoryRecieved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        typesMap.put("grocery","grocery_or_supermarket");
        typesMap.put("pharmacy","pharmacy");
        typesMap.put("garments","garments");
        typesMap.put("stationary","stationary");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_search_result);

        resultTv = (TextView)findViewById(R.id.textView6);

        Log.d(TAG + " Inside Smart Search", " " + latitude + "  " + longitude);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(Awareness.API)
                .build();

        categoryRecieved = getIntent().getStringExtra("category");

        if (!mGoogleApiClient.isConnecting() || !mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }

        // Read if preference set in DB Otherwise perform Cold Start
        initShapshot();
    }

    public void initShapshot(){
        Awareness.SnapshotApi.getLocation(mGoogleApiClient)
                .setResultCallback(new ResultCallback<LocationResult>() {
                    @Override
                    public void onResult(@NonNull LocationResult locationResult) {
                        if (!locationResult.getStatus().isSuccess()) {
                            Log.e(TAG + " SnapShot", "Could not get location.");
                            return;
                        }
                        Location location = locationResult.getLocation();
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        //initUserGeoFence();
                        loadNearByPlaces();
                    }
                });
    }

    private void loadNearByPlaces() {
     //   Log.d(TAG + " loadNearByPlaces", " " + latitude + "  " + longitude);
        String type = typesMap.get(categoryRecieved);
        StringBuilder googlePlacesUrl =
                new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=").append(latitude).append(",").append(longitude);
        googlePlacesUrl.append("&radius=").append(PROXIMITY_RADIUS);
        googlePlacesUrl.append("&types=").append(type);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + GOOGLE_BROWSER_API_KEY);

        JsonObjectRequest request = new JsonObjectRequest(googlePlacesUrl.toString(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject result) {

                       // Log.i(TAG + " loadNearByPlaces", "onResponse: Result= " + result.toString());
                        parseLocationResult(result);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG + " loadNearByPlaces", "onErrorResponse: Error= " + error);
                        Log.e(TAG + " loadNearByPlaces", "onErrorResponse: Error= " + error.getMessage());
                    }
                });

        AppController.getInstance().addToRequestQueue(request);
    }

    private void parseLocationResult(JSONObject result) {
        Log.d(TAG + " parseLocationResult", "loading near by places....");
        double latitude, longitude;

        try {
            JSONArray jsonArray = result.getJSONArray(RESULT);
            places = new ArrayList<>();

            if (result.getString(STATUS).equalsIgnoreCase(OK)) {

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonPlace = jsonArray.getJSONObject(i);
                    Place place = new Place();

                    place.setId(jsonPlace.getString(SUPERMARKET_ID));
                    place.setPlace_id(jsonPlace.getString(PLACE_ID));
                    if (!jsonPlace.isNull(NAME)) {
                        place.setName(jsonPlace.getString(NAME));
                    } else {
                        place.setName("UNNAMED");
                    }
                    JSONArray jsonTypes = jsonPlace.getJSONArray(TYPE);
                    List<String> types = new ArrayList<String>();
                    for (int j = 0;j < jsonTypes.length(); j++) {
                        types.add(jsonTypes.getString(j));
                    }
                    if (types != null) {
                        place.setType(types);
                    }
                    latitude = jsonPlace.getJSONObject(GEOMETRY).getJSONObject(LOCATION)
                            .getDouble(LATITUDE);
                    longitude = jsonPlace.getJSONObject(GEOMETRY).getJSONObject(LOCATION)
                            .getDouble(LONGITUDE);
                    place.setLatlng(new LatLng(latitude, longitude));

                    places.add(place);

                }
                int i = 0;
                resultTv.setText("Near by place for "+ categoryRecieved + " found!:\n");
                for (Place place: places){
                    i++;
                    resultTv.setText(resultTv.getText() + place.getName() + "\n");
                    Log.i(TAG, i + "   " + place.getName());
                }
                Log.i(TAG + " parseLocationResult", " Near by place: Supermarkets found!");

            } else if (result.getString(STATUS).equalsIgnoreCase(ZERO_RESULTS)) {
                Log.i(TAG + " parseLocationResult", " Near by place: No Supermarket found in 5KM radius!!!");

            }

        } catch (JSONException e) {

            e.printStackTrace();
            Log.e(TAG + " parseLocationResult", " Near by place: Error=" + e.getMessage());
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG , "onConnected");
        initShapshot();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
