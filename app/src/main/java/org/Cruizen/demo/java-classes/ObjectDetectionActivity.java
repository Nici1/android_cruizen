package org.pytorch.demo.objectdetection;

import static org.pytorch.demo.objectdetection.PrePostProcessor.mClasses;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.camera.core.ImageProxy;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pytorch.IValue;
import org.pytorch.LiteModuleLoader;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

public class ObjectDetectionActivity extends AbstractCameraXActivity<ObjectDetectionActivity.AnalysisResult>  {
    private Module mModule = null;
    private ResultView mResultView;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;


    private Handler handler;
    private Timer timer;
    private int minuteCount;

    int car=0, bicycle=0, dog=0, truck=0, stop_sign=0, fire_hydrant=0, traffic_light=0, person=0, cat=0;



    static class AnalysisResult {
        private final ArrayList<Result> mResults;

        public AnalysisResult(ArrayList<Result> results) {
            mResults = results;
        }
    }

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_object_detection;
    }

    @Override
    protected TextureView getCameraPreviewTextureView() {
        mResultView = findViewById(R.id.resultView);
        return ((ViewStub) findViewById(R.id.object_detection_texture_view_stub))
                .inflate()
                .findViewById(R.id.object_detection_texture_view);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupButtonListeners();
        startDatabaseUpdateTimer();
        Log.i("Person", String.valueOf(person));
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    protected void applyToUiAnalyzeImageResult(AnalysisResult result) {
        mResultView.setResults(result.mResults);
        mResultView.invalidate();
    }

    private Bitmap imgToBitmap(Image image) {
        Image.Plane[] planes = image.getPlanes();
        ByteBuffer yBuffer = planes[0].getBuffer();
        ByteBuffer uBuffer = planes[1].getBuffer();
        ByteBuffer vBuffer = planes[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        byte[] nv21 = new byte[ySize + uSize + vSize];
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 75, out);

        byte[] imageBytes = out.toByteArray();
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    @Override
    @WorkerThread
    @Nullable
    protected AnalysisResult analyzeImage(ImageProxy image, int rotationDegrees) {
        try {
            if (mModule == null) {
                mModule = LiteModuleLoader.load(MainActivity.assetFilePath(getApplicationContext(), "yolov5s.torchscript.ptl"));
            }
        } catch (IOException e) {
            Log.e("Object Detection", "Error reading assets", e);
            return null;


        }


        Bitmap bitmap = imgToBitmap(image.getImage());
        Matrix matrix = new Matrix();
        matrix.postRotate(90.0f);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, PrePostProcessor.mInputWidth, PrePostProcessor.mInputHeight, true);

        final Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(resizedBitmap, PrePostProcessor.NO_MEAN_RGB, PrePostProcessor.NO_STD_RGB);
        IValue[] outputTuple = mModule.forward(IValue.from(inputTensor)).toTuple();
        final Tensor outputTensor = outputTuple[0].toTensor();
        final float[] outputs = outputTensor.getDataAsFloatArray();

        float imgScaleX = (float)bitmap.getWidth() / PrePostProcessor.mInputWidth;
        float imgScaleY = (float)bitmap.getHeight() / PrePostProcessor.mInputHeight;
        float ivScaleX = (float)mResultView.getWidth() / bitmap.getWidth();
        float ivScaleY = (float)mResultView.getHeight() / bitmap.getHeight();

        final ArrayList<Result> results = PrePostProcessor.outputsToNMSPredictions(outputs, imgScaleX, imgScaleY, ivScaleX, ivScaleY, 0, 0);

        Iterator<Result> iterator = results.iterator();
        while (iterator.hasNext()) {
            switch(mClasses[iterator.next().classIndex]) {
                case "cat":
                    cat++;
                    break;
                case "dog":
                    dog++;
                    break;
                case "car":
                    car++;
                    break;
                case "truck":
                    truck++;
                    break;
                case "person":
                    person++;
                    break;
                case "traffic light":
                    traffic_light++;
                    break;
                case "fire hydrant":
                    fire_hydrant++;
                    break;
                case "stop sign":
                    stop_sign++;
                    break;
                case "bicycle":
                    bicycle++;
                    break;
                default:
                    // code block
            }
        }
        Log.i("Person", String.valueOf(person));

        return new AnalysisResult(results);
    }



    private void startDatabaseUpdateTimer() {
        handler = new Handler();
        timer = new Timer();
        minuteCount = 0;

        // Načrtovanje opravila posodabljanja podatkovne zbirke, ki se bo izvajalo vsako minuto
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.i("Person", String.valueOf(person));

                // Get the current counts
                int currentPersonCount = person;
                int currentCarCount = car;
                int currentBicycleCount = bicycle;
                int currentCatCount = cat;
                int currentDogCount = dog;
                int currentTruckCount = truck;
                int currentStopSignCount = stop_sign;
                int currentTrafficLightCount = traffic_light;
                int currentFireHydrantCount = fire_hydrant;

                // Ponastavitev spremenljivk na 0
                car = 0;
                bicycle = 0;
                cat = 0;
                dog = 0;
                truck = 0;
                stop_sign = 0;
                traffic_light = 0;
                fire_hydrant = 0;
                person = 0;



                if (currentPersonCount !=0 ||  currentCarCount !=0 || currentBicycleCount !=0 || currentCatCount !=0 ||
                        currentDogCount !=0 ||currentTruckCount !=0 || currentStopSignCount !=0 || currentTrafficLightCount !=0 ) {
                    sendLocationDataToServer(currentPersonCount, currentCarCount, currentBicycleCount, currentCatCount, currentDogCount, currentTruckCount, currentStopSignCount, currentTrafficLightCount, currentFireHydrantCount);

                }

                minuteCount++;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Update UI if needed
                    }
                });
            }
        }, 60 * 1000, 60 * 1000); // Zakasnitev: 1 minuta, Obdobje: 1 minuta
    }





    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Prekliči timer, ko je dejavnost uničena.
        timer.cancel();
    }

    void sendLocationDataToServer(int currentPersonCount, int currentCarCount, int currentBicycleCount, int currentCatCount, int currentDogCount, int currentTruckCount, int currentStopSignCount, int currentTrafficLightCount, int currentFireHydrantCount) {
        // Check if location permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Location permission is granted, proceed with sending location data
            sendLocationData( currentPersonCount,  currentCarCount,  currentBicycleCount, currentCatCount, currentDogCount, currentTruckCount, currentStopSignCount, currentTrafficLightCount, currentFireHydrantCount);
        } else {
            // Location permission is not granted, request the permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    public void sendLocationData(int currentPersonCount, int currentCarCount, int currentBicycleCount, int currentCatCount, int currentDogCount, int currentTruckCount, int currentStopSignCount, int currentTrafficLightCount, int currentFireHydrantCount) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    // Pridobite zemljepisno širino in dolžino
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    // Pridobite trenutni datum in čas
                    Calendar calendar = Calendar.getInstance();
                    Date currentDate = calendar.getTime();

                    // Ustvarite objekt JSON, v katerem bosta shranjena lokacija in datum
                    JSONObject locationData = new JSONObject();
                    try {
                        locationData.put("latitude", latitude);
                        locationData.put("longitude", longitude);
                        locationData.put("date", currentDate.toString());

                        Log.i("Person from inside", String.valueOf(person));

                        locationData.put("person", currentPersonCount);
                        locationData.put("car", currentCarCount);
                        locationData.put("bicycle", currentBicycleCount);
                        locationData.put("cat", currentCatCount);
                        locationData.put("dog", currentDogCount);
                        locationData.put("truck", currentStopSignCount);
                        locationData.put("stop_sign", currentStopSignCount);
                        locationData.put("fire_hydrant", currentFireHydrantCount);
                        locationData.put("traffic_light", currentTrafficLightCount);




                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    RequestQueue requestQueue = Volley.newRequestQueue(ObjectDetectionActivity.this);


                    String serverUrl = "http://212.101.137.119:4000/traffic-alerts";


                    final String requestBody = locationData.toString();

                    // Create a POST request
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, serverUrl,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    // Handle the response from the server
                                    Toast.makeText(ObjectDetectionActivity.this, "Location data sent successfully", Toast.LENGTH_SHORT).show();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // Handle the error
                                    Toast.makeText(ObjectDetectionActivity.this, "Failed to send location data", Toast.LENGTH_SHORT).show();
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

                    requestQueue.add(stringRequest);
                } else {
                    Toast.makeText(ObjectDetectionActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }




    private void setupButtonListeners() {
        BottomNavigationItemView mapButton = findViewById(R.id.map_button);
        BottomNavigationItemView loginButton = findViewById(R.id.login_button);
        BottomNavigationItemView cameraButton = findViewById(R.id.camera_button);

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ObjectDetectionActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(ObjectDetectionActivity.this, infoActivity.class);
                startActivity(intent);
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(ObjectDetectionActivity.this, ObjectDetectionActivity.class);
                startActivity(intent);
            }
        });
    }



}
