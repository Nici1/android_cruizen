package org.pytorch.demo.objectdetection;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;

public class BottomBarActivity extends AppCompatActivity {

    public void setBottomBar() {
        BottomNavigationItemView button1 = findViewById(R.id.login_button);
        BottomNavigationItemView button2 = findViewById(R.id.camera_button);
        BottomNavigationItemView button3 = findViewById(R.id.map_button);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(BottomBarActivity.this, infoActivity.class);
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
