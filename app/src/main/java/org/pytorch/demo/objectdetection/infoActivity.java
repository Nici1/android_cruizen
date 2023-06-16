package org.pytorch.demo.objectdetection;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.common.collect.Maps;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class infoActivity extends BottomBarActivity implements View.OnClickListener {

    private ImageView bicycle, car, truck, cat, dog, traffic_light, stop_sign, person;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cats);

        // Get a reference to the ScrollView
        ScrollView scrollView = findViewById(R.id.scrollView);



        // Optionally, you can scroll to a specific position within the ScrollView
        // scrollView.scrollTo(x, y);

        // If you want to programmatically scroll to the top of the ScrollView, you can use:
        // scrollView.smoothScrollTo(0, 0);

        bicycle = findViewById(R.id.bicycle);
        truck = findViewById(R.id.truck);
        person = findViewById(R.id.person);
        traffic_light = findViewById(R.id.traffic_light);
        stop_sign = findViewById(R.id.stop_sign);
        car = findViewById(R.id.car);
        cat = findViewById(R.id.cat);
        dog = findViewById(R.id.dog);

        bicycle.setOnClickListener(this);
        person.setOnClickListener(this);
        stop_sign.setOnClickListener(this);
        traffic_light.setOnClickListener(this);
        truck.setOnClickListener(this);
        car.setOnClickListener(this);
        dog.setOnClickListener(this);
        cat.setOnClickListener(this);

        setBottomBar();
    }


    @Override
    public void onClick(View v) {
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

                        // Create a list to store the time values and detections for the selected image
                        ArrayList<String> timeList = new ArrayList<>();
                        ArrayList<Integer> detectionList = new ArrayList<>();

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject alert = data.getJSONObject(i);
                            JSONArray detections = alert.getJSONArray("detections");

                            // Extract the detection values
                            int[] detectionsArray = new int[detections.length()];
                            for (int j = 0; j < detections.length(); j++) {
                                detectionsArray[j] = detections.getInt(j);
                            }

                            String time = alert.getString("time");
                            Log.d("Time", time);

                            // Determine which ImageView triggered the intent
                            switch (v.getId()) {
                                case R.id.person:
                                    timeList.add(time);
                                    detectionList.add(detectionsArray[0]);
                                    break;
                                case R.id.car:
                                    timeList.add(time);
                                    detectionList.add(detectionsArray[1]);
                                    break;
                                case R.id.bicycle:
                                    timeList.add(time);
                                    detectionList.add(detectionsArray[2]);
                                    break;
                                case R.id.cat:
                                    timeList.add(time);
                                    detectionList.add(detectionsArray[3]);
                                    break;
                                case R.id.dog:
                                    timeList.add(time);
                                    detectionList.add(detectionsArray[4]);
                                    break;
                                case R.id.truck:
                                    timeList.add(time);
                                    detectionList.add(detectionsArray[5]);
                                    break;
                                case R.id.stop_sign:
                                    timeList.add(time);
                                    detectionList.add(detectionsArray[6]);
                                    break;
                                case R.id.traffic_light:
                                    timeList.add(time);
                                    detectionList.add(detectionsArray[8]);
                                    break;

                                default:
                                    break;
                            }
                        }

                        // Create an Intent and pass the time list and detection list to DataActivity
                        Intent intent = new Intent(infoActivity.this, DataActivity.class);
                        intent.putStringArrayListExtra("timeList", timeList);
                        intent.putIntegerArrayListExtra("detectionList", detectionList);
                        startActivity(intent);
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




