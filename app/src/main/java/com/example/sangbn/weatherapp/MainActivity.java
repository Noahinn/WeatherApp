package com.example.sangbn.weatherapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    final String KEY = "df5eebc5e5eae9f90ff11af8105ee718";
    public String cityID = "";
    OkHttpClient client = new OkHttpClient();

    TextView txtString;

    public String url = "http://api.openweathermap.org/data/2.5/forecast?id=" + cityID + "&APPID=" + KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Request request = new Request.Builder()
                .header("Authorization", "replace this text with your token")
                .url("your api url")
                .build();
    }

    void run() throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String myResponse = response.body().string();

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtString.setText(myResponse);
                    }
                });
            }
        });
    }
}
