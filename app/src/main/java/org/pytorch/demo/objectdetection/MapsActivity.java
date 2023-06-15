package org.pytorch.demo.objectdetection;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
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

    Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        send = findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendLocationDataToServer();
            }
        });
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

                        // Fetch and display the user's posts and other users' posts within 24 hours
                        fetchAndDisplayPosts(currentLocation);
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

    private void fetchAndDisplayPosts(LatLng currentLocation) {
        // TODO: Implement the logic to fetch the posts from the server within a 24-hour period
        // You can use an API call or database query to retrieve the posts

        // Example code to add a sample post marker
        LatLng postLocation = new LatLng(37.7749, -122.4194);
        mMap.addMarker(new MarkerOptions()
                .position(postLocation)
                .title("Post Title")
                .snippet("Post Description"));

        // Example code to add a sample user post marker
        LatLng userPostLocation = new LatLng(37.7831, -122.4039);
        mMap.addMarker(new MarkerOptions()
                .position(userPostLocation)
                .title("User Post Title")
                .snippet("User Post Description")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        // Example code to add a circle to represent the 24-hour radius
        CircleOptions circleOptions = new CircleOptions()
                .center(currentLocation)
                .radius(1000) // 1000 meters (1 km) radius
                .strokeColor(Color.RED)
                .strokeWidth(2)
                .fillColor(Color.parseColor("#80FF0000")); // Translucent red
        mMap.addCircle(circleOptions);
    }



     void sendLocationDataToServer() {
        // Check if location permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Location permission is granted, proceed with sending location data
            sendLocationData();
        } else {
            // Location permission is not granted, request the permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    public void sendLocationData() {
        // Get the current location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission should have been granted at this point, but it's always good to check again
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    // Get the latitude and longitude
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    // Get the current date and time
                    Calendar calendar = Calendar.getInstance();
                    Date currentDate = calendar.getTime();

                    // Create a JSON object to hold the location and date
                    JSONObject locationData = new JSONObject();
                    try {
                        locationData.put("latitude", latitude);
                        locationData.put("longitude", longitude);
                        locationData.put("date", currentDate.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // Create a request queue using Volley
                    RequestQueue requestQueue = Volley.newRequestQueue(MapsActivity.this);

                    // Define the server URL where you want to send the data
                    String serverUrl = "http://212.101.137.119:4000/traffic-alerts";

                    // Create a request body with the location data
                    final String requestBody = locationData.toString();

                    // Create a POST request
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, serverUrl,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    // Handle the response from the server
                                    Toast.makeText(MapsActivity.this, "Location data sent successfully", Toast.LENGTH_SHORT).show();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // Handle the error
                                    Toast.makeText(MapsActivity.this, "Failed to send location data", Toast.LENGTH_SHORT).show();
                                }
                            }) {
                        @Override
                        public byte[] getBody() throws AuthFailureError {
                            try {
                                return requestBody.getBytes("utf-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                                return null;
                            }
                        }

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            // Add the token to the request headers
                            SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                            String token = sharedPreferences.getString("authToken", "");
                            Map<String, String> headers = new HashMap<>();
                            headers.put("Content-Type", "application/json");
                            headers.put("Authorization", "Bearer " + token);
                            return headers;
                        }
                    };

                    // Add the request to the request queue
                    requestQueue.add(stringRequest);
                } else {
                    Toast.makeText(MapsActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }





}

