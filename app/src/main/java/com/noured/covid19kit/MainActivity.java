package com.noured.covid19kit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button mButtonContactUs;
    private Button mButtonStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButtonContactUs = (Button) findViewById(R.id.main_activity_contact_us);
        mButtonStart = (Button) findViewById(R.id.main_activity_start);

        mButtonContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // User Click on the Plant Button
                Intent contactActivityIntent = new Intent(MainActivity.this, ContactActivity.class);
                startActivity(contactActivityIntent);
            }
        });

        mButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // User Click on the Plant Button
                Intent transitionActivityIntent = new Intent(MainActivity.this, TransitionActivity.class);
                startActivity(transitionActivityIntent);
            }
        });
    }
}