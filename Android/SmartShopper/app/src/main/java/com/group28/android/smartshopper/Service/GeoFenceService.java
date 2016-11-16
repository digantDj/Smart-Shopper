package com.group28.android.smartshopper.Service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.group28.android.smartshopper.AppController;
import com.group28.android.smartshopper.Model.Place;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mihir on 11/12/2016.
 */

public class GeoFenceService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<Status> {

    public static final String GOOGLE_BROWSER_API_KEY =
            "AIzaSyDGuARLd7MSkgY6V4PdLqcKqj6KRigoTqk";
    public static final int PROXIMITY_RADIUS = 5000;
    public static final int MAXIMUM_GEOFENCES = 98;
    public static final String STATUS = "status";
    public static final String OK = "OK";
    public static final float GEOFENCE_RADIUS_IN_METERS = 500;
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = 24*60*60*1000;
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

    private double latitude;
    private double longitude;
    private List<Place> places;


    private static final String TAG = "Awareness";
    private GoogleApiClient mGoogleApiClient;
    //private GoogleApiClient mGoogleApiClientSnapShot;
    //private GoogleApiClient mGoogleApiClientAwareness;
    //private GoogleApiClient mGoogleApiClientPlace;
    protected Geofence mUserGeofence;
    protected List<Geofence> mPlaceGeofenceList;


    private static final int PERMISSION_REQUEST_CODE = 100;
    ListView lstPlaces;


    @Override
    public void onCreate(){
        Log.d("GeoFenceService", "onCreate");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(Awareness.API)
                .build();

       /* mGoogleApiClientSnapShot = new GoogleApiClient.Builder(this)
                .addApi(Awareness.API)
                .build();

        mGoogleApiClientAwareness =  new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClientPlace = new GoogleApiClient.Builder(this)
                .addApi(Places.PLACE_DETECTION_API)
                .build();*/
        //.enableAutoManage(this, 0, this)

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("GeoFenceService", "OnStartCommand");
        if (!mGoogleApiClient.isConnecting() || !mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
        /*if (!mGoogleApiClientAwareness.isConnecting() || !mGoogleApiClientAwareness.isConnected()) {
            mGoogleApiClientAwareness.connect();
        }

        if (!mGoogleApiClientSnapShot.isConnecting() || !mGoogleApiClientSnapShot.isConnected()) {
            mGoogleApiClientSnapShot.connect();
        }

        if (!mGoogleApiClientPlace.isConnecting() || !mGoogleApiClientPlace.isConnected()) {
            mGoogleApiClientPlace.connect();
        }*/

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("GeoFenceService", "initShapshot " + latitude + "  " + longitude);
        initShapshot();
        /*Log.d("GeoFenceService", "initUserGeoFence" +  latitude + "  " + longitude);
        initUserGeoFence();
        Log.d("GeoFenceService", "loadNearByPlaces" + latitude + "  " + longitude);
        loadNearByPlaces();
        Log.d("GeoFenceService", "done loadNearByPlaces " + latitude + "  " + longitude);*/
        //createPlaceFences();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(@NonNull Status status) {
        if (status.isSuccess()) {
            Log.i("GeoFence", "User Fence Added");
        } else {
            Log.i("GeoFence", "Error");
            // Get the status code for the error and log it using a user-friendly message.
            //String errorMessage = GeofenceErrorMessages.getErrorString(this,
             //      status.getStatusCode());
        }
    }

    public void initShapshot(){
        Awareness.SnapshotApi.getLocation(mGoogleApiClient)
                .setResultCallback(new ResultCallback<LocationResult>() {
                    @Override
                    public void onResult(@NonNull LocationResult locationResult) {
                        if (!locationResult.getStatus().isSuccess()) {
                            Log.e("SnapShot", "Could not get location.");
                            return;
                        }
                        Location location = locationResult.getLocation();
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        Log.i("SnapShot", "Lat: " + latitude + ", Lon: " + longitude);

                        Log.d("GeoFenceService", "initUserGeoFence" +  latitude + "  " + longitude);
                        initUserGeoFence();
                        Log.d("GeoFenceService", "loadNearByPlaces" + latitude + "  " + longitude);
                        loadNearByPlaces();
                        Log.d("GeoFenceService", "done loadNearByPlaces " + latitude + "  " + longitude);
                        // createPlaceFences();

                        //Toast.makeText(MainActivity.this, "Lat: " + latitude + ", Lon: " + longitude, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void createPlaceFences(){
        Log.d("GeoFenceSerice", "inside CreatePlaceFences");
        mPlaceGeofenceList = new ArrayList<>();
        int count = 0;
        for (Place place : places) {
            count++;
            if (count == MAXIMUM_GEOFENCES) {
                break;
            }
            mPlaceGeofenceList.add(new Geofence.Builder()
                    .setRequestId(place.getName())
                    .setCircularRegion(
                            place.getLatlng().latitude,
                            place.getLatlng().longitude,
                            GEOFENCE_RADIUS_IN_METERS
                    )
                    .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());

        }
        if (!mGoogleApiClient.isConnected()) {
            Log.e("PlaceGeoFence", "Google API Client not connected!");
            //Toast.makeText(this, "Google API Client not connected!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().

        } catch (SecurityException securityException) {
            Log.e("UserGeoFence", securityException.toString());
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
        }
    }

    public void initUserGeoFence(){
        mUserGeofence = new Geofence.Builder()
                .setRequestId("UserGeoFence")
                .setCircularRegion(
                        latitude,
                        longitude,
                        GEOFENCE_RADIUS_IN_METERS
                )
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();

        if (!mGoogleApiClient.isConnected()) {
            Log.e("UserGeoFence", "Google API Client not connected!");
            //Toast.makeText(this, "Google API Client not connected!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().

        } catch (SecurityException securityException) {
            Log.e("UserGeoFence", securityException.toString());
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
        }
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofence(mUserGeofence);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(this, GeofenceTransitionsReceiver.class);
        intent.setAction("GeofenceFilter");
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling addgeoFences()
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void loadNearByPlaces() {

        String type = "grocery_or_supermarket";
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

                        Log.i(TAG, "onResponse: Result= " + result.toString());
                        parseLocationResult(result);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: Error= " + error);
                        Log.e(TAG, "onErrorResponse: Error= " + error.getMessage());
                    }
                });

        AppController.getInstance().addToRequestQueue(request);
    }

    private void parseLocationResult(JSONObject result) {
        Log.d("GeoFenceService", "inside parseLocationResult");
        double latitude, longitude;

        try {
            JSONArray jsonArray = result.getJSONArray(RESULT);
            places = new ArrayList<>();
            //ArrayAdapter myAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1);
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


                    /*if (!place.isNull(VICINITY)) {
                        vicinity = place.getString(VICINITY);
                    }

                    //reference = jsonPlace.getString(REFERENCE);
                    //rate = jsonPlace.getString("rating");

                    //myAdapter.add(placeName + "    Rating ( " + rate + " ) ");
                    //MarkerOptions markerOptions = new MarkerOptions();

                    //markerOptions.position(latLng);
                    //markerOptions.title(placeName + " : " + vicinity);

                    // mMap.addMarker(markerOptions);


                    */


                }
                Log.i("Near By Places", " Supermarkets found!");
                //Toast.makeText(getBaseContext(), jsonArray.length() + " Supermarkets found!",
                        //Toast.LENGTH_LONG).show();
            } else if (result.getString(STATUS).equalsIgnoreCase(ZERO_RESULTS)) {
                Log.i("Near By Places", "No Supermarket found in 5KM radius!!!");
                //Toast.makeText(getBaseContext(), "No Supermarket found in 5KM radius!!!",
                  //      Toast.LENGTH_LONG).show();
            }
            createPlaceFences();

        } catch (JSONException e) {

            e.printStackTrace();
            Log.e(TAG, "parseLocationResult: Error=" + e.getMessage());
        }
    }
}
