package com.example.sangbn.weatherapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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

    final String KEY = "df5eebc5e5eae9f90ff11af8105ee718";
    public String cityID = "707860";
    OkHttpClient client = new OkHttpClient();

    TextView txtString;
    ImageView imageView;

    //    public String url = "https://api.openweathermap.org/data/2.5/weather?id=" + cityID + "&appid=" + KEY;
    String urlImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

//        get(cityID);

//        imageView = (ImageView) findViewById(R.id.imageView);
//        URL url2 = null;
//        try {
//            url2 = new URL("https://openweathermap.org/img/w/01d.png");
//            System.out.println("URL " + url2);
//            Bitmap bmp = BitmapFactory.decodeStream(url2.openConnection().getInputStream());
//            System.out.println("BIT " + bmp);
//            imageView.setImageBitmap(bmp);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Bitmap bm = getBitmapFromURL("https://cdn1.iconfinder.com/data/icons/ninja-things-1/1772/ninja-simple-512.png");
//        System.out.println("BM" + bm);


//        Picasso.get().load(R.drawable.ic_launcher_background).into(imageView);

        LoadBitmaps bm = new LoadBitmaps();
        bm.doInBackground();
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
            System.out.print("EEEEEEE "+ e);
            // Log exception
            return null;
        }
    }

    public void addCard(JSONObject result) throws JSONException {
//        String lon = (String) result.getJSONObject("coord").get("lon");
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
//            System.out.println("OK");
        CardView cv1 = new CardView(this);
        cv1.setLayoutParams(params);

        CardView cv2 = new CardView(this);
        cv2.setLayoutParams(params);
        CardView cv3 = new CardView(this);
        cv3.setLayoutParams(params);
        TextView textView2 = new TextView(this);
        textView2.setLayoutParams(params);
        textView2.setText("ACB");
        cv2.addView(textView2);
        cv1.addView(cv2);
        cv1.addView(cv3);

        linearLayout.addView(cv1);


    }

    public void get(String id) {
        String url = "https://api.openweathermap.org/data/2.5/weather?id=" + id + "&appid=" + KEY;
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
                            addCard(result);
                            System.out.println("RESULT " + result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
}

class LoadBitmaps extends AsyncTask<String, Void, Void> {


    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
        // do something  // show some progress of loading images
    }

    @Override
    protected Void doInBackground(String... str) {

        try {
            URL url = new URL("https://cdn1.iconfinder.com/data/icons/ninja-things-1/1772/ninja-simple-512.png");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);

        } catch (IOException e) {
            e.printStackTrace();

        }
        return null;
    }


    @Override
    protected void onPostExecute(Void v) {
        // do something
    }


}
