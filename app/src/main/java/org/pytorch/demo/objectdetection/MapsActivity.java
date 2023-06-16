package org.pytorch.demo.objectdetection;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;



public class MapsActivity extends BottomBarActivity implements OnMapReadyCallback {

    private GoogleMap mMap;


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;

    Button b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        displayPoints();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        setBottomBar();
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Check if location permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true); // Enable the "My Location" button

            // Get the last known location of the device
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.addMarker(new MarkerOptions()
                                .position(currentLocation)
                                .title("My Location"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f));


                    } else {
                        Toast.makeText(MapsActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void displayDetections(int detections [], double coordinates []) {

        String names [] = {"Person", "Car", "Bicycle", "Cat", "Dog", "Truck", "Stop sign", "Fire hydrant", "Traffic light"};
        boolean greater_0 [] = {false, false, false, false, false, false, false, false, false};
        float[] colors = {BitmapDescriptorFactory.HUE_AZURE, BitmapDescriptorFactory.HUE_GREEN, BitmapDescriptorFactory.HUE_YELLOW,
                BitmapDescriptorFactory.HUE_BLUE, BitmapDescriptorFactory.HUE_ORANGE, BitmapDescriptorFactory.HUE_CYAN,
                BitmapDescriptorFactory.HUE_VIOLET, BitmapDescriptorFactory.HUE_ROSE, BitmapDescriptorFactory.HUE_MAGENTA};
        for(int i=0; i<detections.length; i++){
            if (detections[i] >0){
                greater_0[i] = true;
            }
        }



        for(int i=0; i<detections.length; i++){
            LatLng markerPosition = new LatLng(coordinates[1] + getRandomOffset(), coordinates[0] + getRandomOffset());
            if (greater_0[i]) {
                mMap.addMarker(new MarkerOptions()
                        .position(markerPosition)
                        .title(names[i])
                        .icon(BitmapDescriptorFactory.defaultMarker(colors[i])));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPosition, 15f));
            }
        }


    }

    private double getRandomOffset() {
        // Generate a random number between -0.0001 and 0.0001
        double min = -0.0001;
        double max = 0.0001;
        return min + Math.random() * (max - min);
    }


    void displayPoints(){

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("authToken", "");
        TrafficAlertApiClient.getTrafficAlerts(token, new TrafficAlertApiClient.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {
                // Handle the successful response here
                Log.d("API", "Response: " + response);

                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        JSONArray data = jsonResponse.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject alert = data.getJSONObject(i);
                            JSONArray detections = alert.getJSONArray("detections");

                            // Extract the detection values
                            // Extract the detection values
                            int[] detectionsArray = new int[detections.length()];
                            for (int j = 0; j < detections.length(); j++) {
                                detectionsArray[j] = detections.getInt(j);
                            }

                            double latitude = alert.getJSONArray("coordinates").getDouble(0);
                            double longitude = alert.getJSONArray("coordinates").getDouble(1);

                            double coordinates [] = {latitude, longitude};
                            displayDetections(detectionsArray, coordinates);

                            // Use the detection values to display markers on the map with different colors
                            // ...
                        }
                    } else {
                        // Handle the case when success is false

                    }
                } catch (JSONException e) {
                    Log.e("EXCEPTION", e.toString());
                }

            }

            @Override
            public void onFailure(String errorMessage) {
                // Handle the failure here
                Log.e("API", "Error: " + errorMessage);
            }
        });

    }




}

