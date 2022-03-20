package com.tz.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&appid=9ad44b0c121776b58879fb126eea524a";
    private EditText editTextCityOrIndex;
    private TextView textViewCityWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextCityOrIndex = findViewById(R.id.editTextCityOrIndex);
        textViewCityWeather = findViewById(R.id.textViewCityWeather);
    }

    public void onClickShowWeather(View view) {
        String city = editTextCityOrIndex.getText().toString().trim();
        if (!city.isEmpty()) {
            DownloadWeatherTask task = new DownloadWeatherTask();
            String url = String.format(WEATHER_URL, city);
            task.execute(url);
        } else {
            Toast.makeText(this, getString(R.string.toastAttention), Toast.LENGTH_SHORT).show();
        }
    }

    private class DownloadWeatherTask extends AsyncTask<String, Void, String> {

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

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                String city = jsonObject.getString("name");
                String temp = jsonObject.getJSONObject("main").getString("temp");
                String description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                String weather = String.format("%s\nTemperature: %s\nOutdoors: %s", city, temp, description);
                textViewCityWeather.setText(weather);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}