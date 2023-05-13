package org.pytorch.demo.objectdetection;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class BottomBarActivity extends AppCompatActivity {

    public void setBottomBar() {
        Button button1 = findViewById(R.id.login_button);
        Button button2 = findViewById(R.id.camera_button);
        Button button3 = findViewById(R.id.map_button);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(BottomBarActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(BottomBarActivity.this, ObjectDetectionActivity.class);
                startActivity(intent);
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(BottomBarActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }

}
