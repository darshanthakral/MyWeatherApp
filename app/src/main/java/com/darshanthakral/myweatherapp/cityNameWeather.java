package com.darshanthakral.myweatherapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Objects;

public class cityNameWeather extends AppCompatActivity {

    private EditText editText1;
    private TextView result;
    private RequestQueue requestQueue;

    String data1, data2, data3, data4;

    //https://api.openweathermap.org/data/2.5/weather?q=nagpur,maharashtra,india&appid=1f9728b131dff119730259b857dbbc2b

    String baseURL = "https://api.openweathermap.org/data/2.5/weather?q=";
    String API = "&appid=1f9728b131dff119730259b857dbbc2b";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_name_weather);
        Objects.requireNonNull(getSupportActionBar()).setTitle("CityNameWeather");

        editText1 = findViewById(R.id.editText1);
        result = findViewById(R.id.result);
        requestQueue = Singleton.getInstance(this).getRequestQueue();

        findViewById(R.id.button).setOnClickListener(v -> getIt());
    }

    public void getIt() {

        String city = editText1.getText().toString();

        if (TextUtils.isEmpty(city)) {

            editText1.setError("Enter city");
            return;
        }

        String myURL = baseURL + city + API;
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

                        /*TODO:add images and display according to "main"*/



                        data4 = response.getString("name");
                        data4 = "City: " + data4 + "\n";

                        //Display fetched data
                        result.setText(data4 + data1 + data2 + data3);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
            builder.create();
            builder.setCancelable(true);
            builder.setTitle("Error");
            builder.setMessage("Something went wrong!");
            builder.show();

        });

        //add request onto the queue
        requestQueue.add(jsonObjectRequest);

    }
}