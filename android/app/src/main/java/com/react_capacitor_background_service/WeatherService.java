package com.react_capacitor_background_service;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherService extends Service {
    private static final long INTERVAL = 15 * 1000; // Intervalo de 15 segundos

    private final Handler handler = new Handler();

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();


    WeatherApiService weatherApiService = retrofit.create(WeatherApiService.class);


    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "WeatherChannel";
            String description = "Channel for weather alert";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("weatherChannel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void showNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "weatherChannel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Weather Alert")
                .setContentText("Temperature is above 20 degrees")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            stopSelf();
            return;
        }
        notificationManager.notify(0, builder.build());
    }

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Call<WeatherResponse> call = weatherApiService.getWeather(40.4165, -3.7026, "temperature_2m");
            call.enqueue(new Callback<WeatherResponse>() {
                @Override
                public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                    if (response.isSuccessful()) {
                        WeatherResponse weatherResponse = response.body();
                        assert weatherResponse != null;
                        double temperature = weatherResponse.getCurrent().getTemp();
                        Log.i("debug", "WatherService, temperature: "+temperature);
                        if (temperature > 20) {
                            showNotification();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                    throw new RuntimeException(t);
                }
            });
            handler.postDelayed(this, INTERVAL);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.post(runnable);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
