package org.pytorch.demo.objectdetection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class FunFactActivity extends AppCompatActivity {

    private GestureDetectorCompat gestureDetectorCompat;
    ImageView image;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fun_fact);



        gestureDetectorCompat = new GestureDetectorCompat(this, new MyGestureListener());

        String text [] = {"Did you know that the human brain is more active at night than during the day?",
        "Did you know that cats have a specialized collarbone that allows them to always land on their feet when they fall?",
        "Did you know that puppies are born deaf?", "Did you know that only 6% of truck drivers are women?", "Did you know that the first stop signs were actually black and white?",
        "Did you know that the world’s first traffic light was installed in London in 1868?", "Did you know that belting past the police officer at a scary 8mph, a motorist by " +
                "the name of Walter Arnold was the first person to get a speeding ticket?", "Did you know that the first constructed bike was almost entirely made of wood?"};


        String receivedMessage = getIntent().getStringExtra("Detection");
        Log.d("DETECTION - FUN FACT", receivedMessage);

        image = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);

        switch (receivedMessage) {
            case "Person":
                image.setImageResource(R.drawable.person1);
                textView.setText(text[0]);
                break;
            case "Cat":
                image.setImageResource(R.drawable.cat1);
                textView.setText(text[1]);
                break;
            case "Dog":
                image.setImageResource(R.drawable.dog1);
                textView.setText(text[2]);
                break;
            case "Truck":
                image.setImageResource(R.drawable.truck1);
                textView.setText(text[3]);
                break;
            case "Stop sign":
                image.setImageResource(R.drawable.stop_sign1);
                textView.setText(text[4]);
                break;
            case "Traffic light":
                image.setImageResource(R.drawable.traffic_light1);
                textView.setText(text[5]);
                break;
            case "Car":
                image.setImageResource(R.drawable.car1);
                textView.setText(text[6]);
                break;
            case "Bicycle":
                image.setImageResource(R.drawable.bicycle1);
                textView.setText(text[7]);
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Pass the touch event to the GestureDetectorCompat
        gestureDetectorCompat.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffX = e2.getX() - e1.getX();
            float diffY = e2.getY() - e1.getY();

            if (Math.abs(diffX) > Math.abs(diffY) && Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX < 0) {
                    // Swipe from right to left, transition back to MainActivity
                    finish();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    return true;
                }
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }
}