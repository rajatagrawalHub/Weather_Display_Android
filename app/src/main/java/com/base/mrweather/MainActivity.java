package com.base.mrweather;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private OkHttpClient client = new OkHttpClient();

    private EditText editText;
    private Button button;
    private TextView temperatureTextView;
    private TextView humidityTextView;

    private TextView temperatureTextView_C;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        button = findViewById(R.id.button);
        temperatureTextView = findViewById(R.id.temperatureTextView);
        humidityTextView = findViewById(R.id.humidityTextView);
        temperatureTextView_C = findViewById(R.id.TempCelTextView);

        button.setOnClickListener(v -> {
            String cityName = editText.getText().toString();
            fetchWeatherData(cityName);
        });
    }

    private void fetchWeatherData(String cityName) {
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=b1b9a065af071caf9cd70a9d08537cb1";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("MainActivity", "Failed to fetch weather data: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                WeatherData weatherData = parseWeatherData(json);
                runOnUiThread(() -> displayWeatherData(json));
            }
        });
    }

    private WeatherData parseWeatherData(String json) {
        WeatherData weatherData = null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject mainObject = jsonObject.getJSONObject("main");
            double temperature = mainObject.getDouble("temp");
            int humidity = mainObject.getInt("humidity");
            weatherData = new WeatherData(temperature, humidity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return weatherData;
    }

    private void displayWeatherData(String json) {
        WeatherData weatherData = parseWeatherData(json);
        if (weatherData != null) {
            double temperature = weatherData.getTemperature();
            double humidity = weatherData.getHumidity();
            temperatureTextView.setText("Temperature: " + temperature + " K");
            humidityTextView.setText("Humidity: " + humidity + "%");
            temperatureTextView_C.setText("Temperature In Celcius: " + (temperature-273) +" C");
        } else {
            Log.e("Display Weatherdata","NULL Object Pointer Exception with respect to weatherdata");
        }
    }
}

class WeatherData {
    private double temperature;
    private int humidity;

    public WeatherData(double temperature, int humidity) {
        this.temperature = temperature;
        this.humidity = humidity;
    }

    public double getTemperature() {
        return temperature;
    }

    public int getHumidity() {
        return humidity;
    }
}
