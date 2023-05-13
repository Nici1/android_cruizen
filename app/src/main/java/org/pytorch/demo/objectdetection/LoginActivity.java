package org.pytorch.demo.objectdetection;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;




import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;

import okhttp3.*;

public class LoginActivity extends BottomBarActivity {

    private EditText mEmailField;
    private EditText mPasswordField;
    private Button mLoginButton;
    private TextView mRegisterLink;
    private OkHttpClient mOkHttpClient;
    private Gson mGson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupButtonListeners();

        mEmailField = findViewById(R.id.email_field);
        mPasswordField = findViewById(R.id.password_field);
        mLoginButton = findViewById(R.id.login_button);
        mRegisterLink = findViewById(R.id.register_link);

        mRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        mOkHttpClient = new OkHttpClient();
        mGson = new Gson();

        mLoginButton.setOnClickListener(view -> {
            String email = mEmailField.getText().toString().trim();
            String password = mPasswordField.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, String> data = new HashMap<>();
            data.put("email", email);
            data.put("password", password);

            RequestBody requestBody = RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"),
                    new JSONObject(data).toString()
            );

            Request request = new Request.Builder()
                    .url("http://212.101.137.119:4000/login")
                    .post(requestBody)
                    .header("Content-Type", "application/json")
                    .build();

            mOkHttpClient.newCall(request).enqueue(new Callback() {
                public void onFailure(Call call, IOException e) {
                    Log.e("LoginActivity", "Error sending request", e);
                }

                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = response.body().string();
                    ApiResponse apiResponse = mGson.fromJson(responseBody, ApiResponse.class);
                    if (apiResponse.isSuccess()) {
                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                            // Start the main activity
                        });
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            });
        });

    }





    private void setupButtonListeners() {
        Button mapButton = findViewById(R.id.map_button);
        Button loginButton = findViewById(R.id.login_button);
        Button cameraButton = findViewById(R.id.camera_button);

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(LoginActivity.this, ObjectDetectionActivity.class);
                startActivity(intent);
            }
        });
    }
}