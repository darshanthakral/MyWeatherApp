package com.darshanthakral.myweatherapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.cityNameWeather).setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), cityNameWeather.class);
            startActivity(intent);
        });

        findViewById(R.id.latLonWeather).setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), latLongWeather.class);
            startActivity(intent);
        });


    }
}