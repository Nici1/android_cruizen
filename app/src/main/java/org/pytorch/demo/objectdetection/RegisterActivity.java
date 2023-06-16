package org.pytorch.demo.objectdetection;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText mNameField;
    private EditText mEmailField;
    private EditText mPasswordField;
    private Button mRegisterButton;
    private TextView mRegisterLink;
    private OkHttpClient mOkHttpClient;
    private Gson mGson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        mNameField = findViewById(R.id.name_field);
        mEmailField = findViewById(R.id.email_field);
        mPasswordField = findViewById(R.id.password_field);
        mRegisterButton = findViewById(R.id.register_button);
        mRegisterLink = findViewById(R.id.login_link);

        mRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        mOkHttpClient = new OkHttpClient();
        mGson = new Gson();

        mRegisterButton.setOnClickListener(view -> {
            String name = mNameField.getText().toString().trim();
            String email = mEmailField.getText().toString().trim();
            String password = mPasswordField.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, String> data = new HashMap<>();
            data.put("name", name);
            data.put("email", email);
            data.put("password", password);

            RequestBody requestBody = RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"),
                    new JSONObject(data).toString()
            );

            Request request = new Request.Builder()
                    .url("http://212.101.137.119:4000/register")
                    .post(requestBody)
                    .header("Content-Type", "application/json")
                    .build();

            mOkHttpClient.newCall(request).enqueue(new Callback() {
                public void onFailure(Call call, IOException e) {
                    Log.e("RegisterActivity", "Error sending request", e);
                }

                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = response.body().string();
                    ApiResponse apiResponse = mGson.fromJson(responseBody, ApiResponse.class);
                    if (apiResponse.isSuccess()) {
                        runOnUiThread(() -> {
                            Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                            final Intent intent = new Intent(RegisterActivity.this, ProfileActivity.class);
                            startActivity(intent);
                        });
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(RegisterActivity.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            });
        });

    }

}