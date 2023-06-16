package org.pytorch.demo.objectdetection;

import android.content.Intent;
import android.content.SharedPreferences;
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
                        String token = getToken(response);

                        if(token != null) {
                            // Store the authentication token in session or storage
                            // For example, using SharedPreferences:
                            SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                            sharedPreferences.edit().putString("authToken", token).apply();
                            Log.i("Token", token);

                        }
                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                            final Intent intent = new Intent(LoginActivity.this, ObjectDetectionActivity.class);
                            startActivity(intent);
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

    private String getToken(Response response) {
        Headers headers = response.headers();
        if (headers != null) {
            String tokenHeader = headers.get("Set-Cookie");
            String[] parts = tokenHeader.split(";");

            // Extract the token value from the first part
            String tokenValue = parts[0].split("=")[1];

            // Trim any leading or trailing spaces
            tokenValue = tokenValue.trim();
            return tokenValue;


        }
        return null;
    }

}