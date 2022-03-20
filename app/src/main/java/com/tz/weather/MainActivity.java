package com.tz.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private String targetUrl;
    private final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&appid=9ad44b0c121776b58879fb126eea524a";
    private EditText editTextCityOrZip;
    private TextView textViewCityWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextCityOrZip = findViewById(R.id.editTextCityOrZip);
        textViewCityWeather = findViewById(R.id.textViewCityWeather);
    }

    public void onClickGo(View view) {
        String editTextString = editTextCityOrZip.getText().toString().trim();
        if (!editTextString.isEmpty()) {
            if (isNumeric(editTextString)) {
                targetUrl = "https://api.openweathermap.org/data/2.5/weather?id=" + editTextString + "&units=metric&appid=9ad44b0c121776b58879fb126eea524a";
            } else {
                targetUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + editTextString + "&units=metric&appid=9ad44b0c121776b58879fb126eea524a";
            }
        } else {
            Toast.makeText(this, getString(R.string.toastAttention), Toast.LENGTH_SHORT).show();
        }

        DownloadJSONTask task = new DownloadJSONTask();
        String result = null;
        try {
            result = task.execute(targetUrl).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonObject = new JSONObject(result);
            String name = jsonObject.getString("name");
//                Log.i("Result", name);
            JSONObject main = jsonObject.getJSONObject("main");
            String temp = main.getString("temp");
//                Log.i("Result", "Temperature: " + temp);
            JSONArray jsonArray = jsonObject.getJSONArray("weather");
            JSONObject weather = jsonArray.getJSONObject(0);
            String mainStr = weather.getString("main");
            String descriptionStr = weather.getString("description");
//                Log.i("Result", "Main: " + mainStr);
            textViewCityWeather.setText(name + "\nTemperature: " + temp + "\n" + mainStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static class DownloadJSONTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            StringBuilder result = new StringBuilder();
            URL url = null;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                BufferedReader bufferedReader = new BufferedReader(reader);
                String line = bufferedReader.readLine();
                while (line != null) {
                    result.append(line);
                    line = bufferedReader.readLine();
                }
                return result.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }

//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            try {
//                JSONObject jsonObject = new JSONObject(s);
//                String name = jsonObject.getString("name");
////                Log.i("Result", name);
//                JSONObject main = jsonObject.getJSONObject("main");
//                String temp = main.getString("temp");
////                Log.i("Result", "Temperature: " + temp);
//                JSONArray jsonArray = jsonObject.getJSONArray("weather");
//                JSONObject weather = jsonArray.getJSONObject(0);
//                String mainStr = weather.getString("main");
//                String descriptionStr = weather.getString("description");
////                Log.i("Result", "Main: " + mainStr);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
    }
}