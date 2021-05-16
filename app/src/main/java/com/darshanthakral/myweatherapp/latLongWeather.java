package com.darshanthakral.myweatherapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class latLongWeather extends AppCompatActivity {

    private final int PERMISSION_CODE = 1;

    private EditText latitude, longitude;
    private TextView result2;
    private RequestQueue requestQueue;

    String data1, data2, data3, data4;

    //https://api.openweathermap.org/data/2.5/weather?lat=20.93&lon=77.75&appid=1f9728b131dff119730259b857dbbc2b

    String baseURL = "https://api.openweathermap.org/data/2.5/weather?";

    String API = "&appid=1f9728b131dff119730259b857dbbc2b";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_latlong_weather);
        Objects.requireNonNull(getSupportActionBar()).setTitle("LatLongWeather");

        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        result2 = findViewById(R.id.result2);

        requestQueue = Singleton.getInstance(this).getRequestQueue();


        findViewById(R.id.fetchData).setOnClickListener(v -> fetchData());

        findViewById(R.id.fetchLocation).setOnClickListener(v -> fetchLocation());


    }

    public void fetchData() {

        String lat = latitude.getText().toString();

        if (TextUtils.isEmpty(lat)) {
            latitude.setError("Enter latitude value");
        }

        String lon = longitude.getText().toString();

        if (TextUtils.isEmpty(lon)) {
            longitude.setError("Enter longitude value");
        }

        String myURL = baseURL + "lat=" + lat + "&" + "lon=" + lon + API;
        // Log.i("URL", "URL: " + myURL);


        @SuppressLint("SetTextI18n") JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, myURL, null,

                response -> {
                    //  Log.i("JSON", "JSON:" + response);
                    try {

                        //1 - )
                        String coord = response.getString("coord");

                        JSONObject jsonObject1 = new JSONObject(coord);

                        data1 = "Lat: " + jsonObject1.getString("lat") + "\n" + "Long: " + jsonObject1.getString("lon");

                        //2 - )
                        String weather = response.getString("weather");

                        JSONArray jsonArray = new JSONArray(weather);
                        //because there is array weather in json file
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                            //because there are objects inside an array weather.

                            data2 = "\nMain: " + jsonObject2.getString("main") + "\n" + "Description: " + jsonObject2.getString("description");

                            // textView.append(name + ", " + courseCount + ", " + email + "\n");
                        }

                        //3 - )
                        String main = response.getString("main");

                        JSONObject jsonObject3 = new JSONObject(main);

                        double buffer1 = Double.parseDouble(jsonObject3.getString("temp"));
                        double buffer1_result = buffer1 - 273.15;
                        double buffer2 = Double.parseDouble(jsonObject3.getString("feels_like"));
                        double buffer2_result = buffer2 - 273.15;

                        DecimalFormat decimalFormat = new DecimalFormat("#.00");

                        data3 = "\nTemp: " + decimalFormat.format(buffer1_result) + " degrees\n" + "Feels Like: " + decimalFormat.format(buffer2_result) + " degrees";

                        //4 - )
                        data4 = response.getString("name");
                        data4 = "City: " + data4 + "\n";


                        //Display fetched data
                        result2.setText(data4 + data1 + data2 + data3);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {

            Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_LONG).show();

        });

        //add request onto the queue
        requestQueue.add(jsonObjectRequest);

    }

    public void fetchLocation() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        } else {
            requestPermission();
        }

    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {

            Toasty.info(getApplicationContext(), "Please wait while we are fetching data. It may take some SECONDS.", Toast.LENGTH_LONG, true).show();
            latitude.setText("");
            longitude.setText("");

            Log.i("Coord", "Latitude: " + location.getLatitude() + ", Longitude: " + location.getLongitude());
            latitude.setText(String.valueOf(location.getLatitude()));
            longitude.setText(String.valueOf(location.getLongitude()));




        }


        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {

        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
            Toast.makeText(getApplicationContext(), "Please Enable Location Permission & Internet", Toast.LENGTH_SHORT).show();
        }
    };

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION) ||
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission Needed!")
                    .setMessage("This permission is important")
                    .setPositiveButton("Ok", (dialog, which) -> {
                        //Main for getting request
                        ActivityCompat.requestPermissions(latLongWeather.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_CODE);
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .create().show();

        } else {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_CODE);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}