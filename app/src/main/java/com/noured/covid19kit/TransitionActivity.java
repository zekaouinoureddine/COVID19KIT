package com.noured.covid19kit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TransitionActivity extends AppCompatActivity {

    private Button mGallery;
    private Button mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition);

        mGallery = (Button) findViewById(R.id.transition_activity_gallery);
        mCamera = (Button) findViewById(R.id.transition_activity_camera);

        mGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // User Click on the Plant Button
                Intent galleryPredictionActivityIntent = new Intent(TransitionActivity.this, GalleryPredictionActivity.class);
                startActivity(galleryPredictionActivityIntent);
            }
        });

        mCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // User Click on the Plant Button
                Intent cameraPredictionActivityIntent = new Intent(TransitionActivity.this, CameraPredictionActivity.class);
                startActivity(cameraPredictionActivityIntent);
            }
        });
    }
}