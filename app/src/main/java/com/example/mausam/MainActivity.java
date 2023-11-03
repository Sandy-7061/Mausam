package com.example.mausam;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


import com.example.mausam.databinding.ActivityMainBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fetchWeatherData("Indore");
        Searchcity();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    private void Searchcity() {
        SearchView searchView = binding.searchView;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchWeatherData(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
    }

    public void fetchWeatherData(String cityname) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIinterface apiInterface = retrofit.create(APIinterface.class);

        Call<weather> responseCall = apiInterface.getWeatherData(cityname, "9ec358d5aa7cbb7e56875e3be7b7c68b", "metric");

        responseCall.enqueue(new Callback<weather>() {
            @Override
            public void onResponse(Call<weather> call, Response<weather> response) {
                if (response.isSuccessful()) {
                    weather responsebody = response.body();
                    if (responsebody != null) {
                        String temperature = Double.toString(responsebody.getMain().getTemp());
                        String humidity = Double.toString(responsebody.getMain().getHumidity());
                        String windspeed = Double.toString(responsebody.getWind().getSpeed());
                        long sunrise = Long.parseLong(Long.toString(responsebody.getSys().getSunrise()));
                        String sunset = Double.toString(responsebody.getSys().getSunset());
                        String visibility = Double.toString(responsebody.getVisibility());
                        String condition = responsebody.getWeather().isEmpty() ? "unknown" : responsebody.getWeather().get(0).getMain();
                        String Clouds= Double.toString(responsebody.getClouds().getAll());
                        String time = Double.toString(responsebody.getCod());

                        binding.temperature.setText(temperature + "°C");
                        binding.weatherCondition.setText(condition);
                        binding.condation.setText(condition);
                        binding.sunrise.setText(time(sunrise));
                        binding.sunsettxt.setText(sunset);
                        binding.humiditytxt.setText(humidity + " %");
                        binding.maxTempTxt.setText("Clouds : " + Clouds);
                        binding.minTempTxtOrignal.setText("COD : "+time);
                        binding.windspeed.setText(windspeed + "m/s");
                        binding.sealeveltxt.setText(visibility);
                        binding.citytxt.setText(cityname);
                        binding.txtday.setText(dayName(System.currentTimeMillis()));
                        binding.txtdate.setText(date());

                        changeImagsAccordingToWeaterCondtion(condition);
                        Log.d("sandeep", "onresponse : " + temperature);
                    } else {
                        Log.d("sachin", "Response body is null");
                    }
                } else {
                    Log.d("sachin", "Response is not successful");
                }
            }

            @Override
            public void onFailure(Call<weather> call, Throwable t) {
                Log.d("failer", "onFailure: " + t.getMessage());
            }
        });
    }

    private void changeImagsAccordingToWeaterCondtion(String conditions) {
        switch (conditions) {
            case "Clear Sky":
            case "Sunny":
            case "Clear":
                binding.getRoot().setBackgroundResource(R.drawable.sunny_background);
                binding.lottieAnimationView.setAnimation(R.raw.sun);
                binding.lottieAnimationView.playAnimation();
                break;

            case "Partly Clouds":
            case "Clouds":
            case "Overcast":
            case "Mist":
            case "Foggy":
                binding.getRoot().setBackgroundResource(R.drawable.colud_background);
                binding.lottieAnimationView.setAnimation(R.raw.cloud);
                binding.lottieAnimationView.playAnimation();

                break;

            case "Light Rain":
            case "Drizzle":
            case "Moderate Rain":
            case "Showers":
            case "Heavy Rain":
                binding.getRoot().setBackgroundResource(R.drawable.rain_background);
                binding.lottieAnimationView.setAnimation(R.raw.rain);
                binding.lottieAnimationView.playAnimation();
                break;

            case "Light Snow":
            case "Moderate Snow":
            case "Heavy Snow":
            case "Blizzard":
                binding.getRoot().setBackgroundResource(R.drawable.snow_background);
                binding.lottieAnimationView.setAnimation(R.raw.snow);
                binding.lottieAnimationView.playAnimation();
                break;

            default:
                binding.getRoot().setBackgroundResource(R.drawable.sunny_background);
                binding.lottieAnimationView.setAnimation(R.raw.sun);
                binding.lottieAnimationView.playAnimation();
                break;
        }
    }


    public String time(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp * 1000));
    }
    public String date() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM YYYY", Locale.getDefault());
        return sdf.format(new Date());
    }

    public String dayName(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
        Date date = new Date(timestamp);
        return sdf.format(date);
    }
}


