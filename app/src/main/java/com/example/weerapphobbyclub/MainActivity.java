package com.example.weerapphobbyclub;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class MainActivity extends AppCompatActivity {

    private EditText etRegisterUsername;
    private EditText etRegisterPassword;
    private Button btnRegister;
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private EditText etCity;
    private Button btnFetchWeather;
    private TextView tvWeatherInfo;
    private TextView tvUsername;

    private static final String API_BASE_URL = "https://api.openweathermap.org/data/2.5/";
    private static final String API_KEY = "19dd586c3fbfbed6a31ff5a9ad23f72e";
    private static final String TAG = "MainActivity";

    private HashMap<String, String> userCredentials = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#522549"));
        }

        // Initialize UI components
        etRegisterUsername = findViewById(R.id.registerUsername);
        etRegisterPassword = findViewById(R.id.registerPassword);
        btnRegister = findViewById(R.id.registerButton);
        etUsername = findViewById(R.id.username);
        etPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.loginButton);
        etCity = findViewById(R.id.cityEditText);
        btnFetchWeather = findViewById(R.id.getWeatherButton);
        tvWeatherInfo = findViewById(R.id.weatherTextView);
        tvUsername = findViewById(R.id.nameTextView);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etRegisterUsername.getText().toString();
                String password = etRegisterPassword.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Vul alle velden in", Toast.LENGTH_SHORT).show();
                } else if (userCredentials.containsKey(username)) {
                    Toast.makeText(MainActivity.this, "Gebruikersnaam is al geregistreerd", Toast.LENGTH_SHORT).show();
                } else {
                    userCredentials.put(username, password);
                    Toast.makeText(MainActivity.this, "Registratie succesvol", Toast.LENGTH_SHORT).show();
                    etRegisterUsername.setVisibility(View.GONE);
                    etRegisterPassword.setVisibility(View.GONE);
                    btnRegister.setVisibility(View.GONE);

                    etUsername.setVisibility(View.VISIBLE);
                    etPassword.setVisibility(View.VISIBLE);
                    btnLogin.setVisibility(View.VISIBLE);
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Vul alle velden in", Toast.LENGTH_SHORT).show();
                } else if (!userCredentials.containsKey(username) || !userCredentials.get(username).equals(password)) {
                    Toast.makeText(MainActivity.this, "Onjuiste gebruikersnaam of wachtwoord", Toast.LENGTH_SHORT).show();
                } else {
                    etUsername.setVisibility(View.GONE);
                    etPassword.setVisibility(View.GONE);
                    btnLogin.setVisibility(View.GONE);

                    etCity.setVisibility(View.VISIBLE);
                    btnFetchWeather.setVisibility(View.VISIBLE);
                    tvWeatherInfo.setVisibility(View.VISIBLE);
                }
            }
        });

        btnFetchWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = etCity.getText().toString();
                if (!city.isEmpty()) {
                    fetchWeatherData(city);
                } else {
                    Toast.makeText(MainActivity.this, "Voer een stad in", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchWeatherData(String cityName) {
        Gson gson = new GsonBuilder().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        WeatherService weatherService = retrofit.create(WeatherService.class);
        Call<WeatherResponse> weatherCall = weatherService.getWeather(cityName, API_KEY);

        weatherCall.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherResponse = response.body();

                    // Convert temperature to Celsius and round to 1 decimal place
                    double tempCelsius = weatherResponse.main.temp - 273.15;
                    String formattedTemp = String.format("%.1f", tempCelsius);

                    String weatherDetails = "Temperatuur: " + formattedTemp + "°C\n" +
                            "Regen: " + (weatherResponse.weather[0].main.equals("Rain") ? "Ja" : "Nee") + "\n" +
                            "Wind Snelheid: " + weatherResponse.wind.speed + " m/s\n" +
                            "Wind Richting: " + convertDegreeToDirection(weatherResponse.wind.deg);
                    tvWeatherInfo.setText(weatherDetails);
                } else {
                    Toast.makeText(MainActivity.this, "Fout bij het ophalen van de gegevens", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Response code: " + response.code());
                    Log.e(TAG, "Response message: " + response.message());
                    try {
                        Log.e(TAG, "Response error body: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e(TAG, "Fout bij het lezen van de foutboodschap", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Netwerkfout: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Netwerkfout: ", t);
            }
        });
    }

    private String convertDegreeToDirection(float degree) {
        String[] directions = {"N", "NNO", "NO", "ONO", "O", "OZO", "ZO", "ZZO",
                "Z", "ZZW", "ZW", "WZW", "W", "WNW", "NW", "NNW"};
        int index = Math.round(degree / 22.5f) % 16;
        return directions[index];
    }

    interface WeatherService {
        @GET("weather")
        Call<WeatherResponse> getWeather(@Query("q") String city, @Query("appid") String apiKey);
    }

    public class WeatherResponse {
        Main main;
        Weather[] weather;
        Wind wind;

        public class Main {
            float temp;
            float humidity;
        }

        public class Weather {
            String main;
        }

        public class Wind {
            float speed;
            float deg;
        }
    }
}
