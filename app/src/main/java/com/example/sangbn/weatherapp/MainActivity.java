package com.example.sangbn.weatherapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final int WRITE_REQUEST_CODE = 101;
    final String KEY = "df5eebc5e5eae9f90ff11af8105ee718";
    private static final String[] listCityID = {
            "1562414",
            "1581298",
            "1863289",
            "1851715",
            "1850147",
            "1566083",
            "5128638",
            "1572151"
    };

    DisplayMetrics displayMetrics = new DisplayMetrics();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        for (int i = 0; i < listCityID.length; i++) {
            getWeather(listCityID[i]);
        }

    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    public void addCard(JSONObject result, String id) throws JSONException {
        final String countryID = id;
        LinearLayout linearCity = (LinearLayout) findViewById(R.id.linear);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        CardView basic = new CardView(this);
        CardView temp = new CardView(this);
        CardView card = new CardView(this);

        params.setMargins(10, 10, 10, 10);
        card.setLayoutParams(params);
        card.setContentPadding(15, 15, 15, 15);
        card.setMaxCardElevation(15);
        card.setCardElevation(9);

        TextView textCity = new TextView(this);
        textCity.setLayoutParams(params);
        textCity.setText(result.get("name").toString());
        textCity.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        textCity.setTypeface(null, Typeface.BOLD);

        JSONObject weather = result.getJSONArray("weather").getJSONObject(0);
        ImageView img = new ImageView(this);

        img.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
        img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        String mDrawableName = "img" + weather.get("icon").toString();
        int resID = getResources().getIdentifier(mDrawableName, "drawable", getPackageName());
        img.setImageResource(resID);
        img.setX(250);

        TextView tv2 = new TextView(this);
        tv2.setLayoutParams(params);

        JSONObject main = result.getJSONObject("main");
        tv2.setText(weather.get("main").toString()
                + " | "
                + main.get("humidity").toString() + "% ");
        tv2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

        TextView tv3 = new TextView(this);
        tv3.setLayoutParams(params);

        tv3.setText(kelvinToCelcius(result.getJSONObject("main").get("temp_max").toString())
                + "°"
                + "/"
                + kelvinToCelcius(result.getJSONObject("main").get("temp_min").toString())
                + "°");

        tv3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        tv3.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);

        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        TextView tv4 = new TextView(this);
        tv4.setLayoutParams(params);
        tv4.setText(kelvinToCelcius(result.getJSONObject("main").get("temp").toString()) + "°C");
        tv4.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
        tv4.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);

        basic.addView(textCity);
        basic.addView(img);
        basic.addView(tv4);

        temp.addView(tv2);
        temp.addView(tv3);

        linearLayout.addView(basic);
        linearLayout.addView(temp);
        card.addView(linearLayout);

        card.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //showDialog();
                getFiveDayWeather(countryID);
            }
        });

        linearCity.addView(card);
    }

    public void getWeather(String id) {
        final String countryid = id;
        String url = "https://api.openweathermap.org/data/2.5/weather?id="
                + id
                + "&appid="
                + KEY;
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
                        JSONObject result = null;
                        try {
                            result = new JSONObject(myResponse);
                            addCard(result, countryid);
                            System.out.println("RESULT " + result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public String kelvinToCelcius(String kl) {
        double d = Double.parseDouble(kl);
        int k = (int) d;
        return String.valueOf(k - 273);
    }

    public void getFiveDayWeather(String id) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://api.openweathermap.org/data/2.5/forecast?id="
                + id
                + "&appid="
                + KEY;

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
                        try {
                            JSONObject json = new JSONObject(myResponse);
                            System.out.println("5 days " + json);
                            showDialog(json);
                            //addCard(json, countryid);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public void showDialog(JSONObject obj) throws JSONException {

        JSONObject city = obj.getJSONObject("city");
        JSONArray forecasts = obj.getJSONArray("list");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(city.get("name").toString());

        ScrollView sc = new ScrollView(this);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        LinearLayout ln = new LinearLayout(this);
        ln.setOrientation(LinearLayout.VERTICAL);

        for (int i = 0; i < forecasts.length(); i++) {

            JSONObject weather = forecasts.getJSONObject(i);

            CardView card = new CardView(this);

            card.setLayoutParams(params);
            card.setCardElevation(9);
            card.setMaxCardElevation(15);
            card.setContentPadding(20, 25, 20, 15);

            CardView basic = new CardView(this);
            basic.setLayoutParams(params);
            CardView temp = new CardView(this);
            temp.setLayoutParams(params);
            TextView day = new TextView(this);
            day.setLayoutParams(params);
            day.setText(weather.get("dt_txt").toString());
            day.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            //tv4.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            JSONObject weatherdetail = weather.getJSONArray("weather").getJSONObject(0);

            ImageView img = new ImageView(this);

            img.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

            String mDrawableName = "img" + weatherdetail.get("icon").toString();
            int resID = getResources().getIdentifier(mDrawableName, "drawable", getPackageName());
            img.setImageResource(resID);

            JSONObject main = weather.getJSONObject("main");
            TextView temp_av = new TextView(this);
            temp_av.setLayoutParams(params);
            temp_av.setText(kelvinToCelcius(main.get("temp").toString()) + "°C");
            temp_av.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            temp_av.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);

            LinearLayout ln2 = new LinearLayout(this);
            ln2.setOrientation(LinearLayout.VERTICAL);
            ln2.setLayoutParams(params);

            basic.addView(day);
            basic.addView(img);
            basic.addView(temp_av);
            basic.setContentPadding(15, 15, 15, 15);
            ln2.addView(basic);


            TextView tv2 = new TextView(this);
            tv2.setLayoutParams(params);
            tv2.setText(weatherdetail.get("main").toString()
                    + " | "
                    + main.get("humidity").toString() + "% ");
            tv2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);

            TextView tv3 = new TextView(this);
            tv3.setLayoutParams(params);

            tv3.setText(kelvinToCelcius(main.get("temp_min").toString())
                    + "/"
                    + kelvinToCelcius(main.get("temp_max").toString())
                    + "°C");
            tv3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            tv3.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);

            temp.addView(tv2);
            temp.addView(tv3);
            ln2.addView(temp);

            card.addView(ln2);

            ln.addView(card);
        }

        sc.addView(ln);

        builder.setView(sc);

        builder.setPositiveButton("BACK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

}

