package com.react_capacitor_background_service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.*;
import androidx.lifecycle.MutableLiveData;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;


public class LocationService extends Service {

    private FusedLocationProviderClient fusedLocationClient;
    private Handler handler;
    private static final long INTERVAL = 15 * 1000; // Intervalo de 15 segundos
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private final Context context = this;

    private static final MutableLiveData<Location> locationLiveData = new MutableLiveData<>();

    public static MutableLiveData<Location> getLocationLiveData() {
        return locationLiveData;
    }

    @Override
    public void onCreate() {
        Log.i("debug", "LocationService onCreate");
        super.onCreate();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getLastLocation();
                handler.postDelayed(this, INTERVAL);
            }
        }, INTERVAL);
    }

    private void getLastLocation() {
        Log.i("debug", "getLastLocation");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            Log.i("debug", "getLastLocation onSuccess");
                            if (location != null) {
                                Log.d("debug", "LocationService, onSuccess, Lat: " + location.getLatitude() + ", Lon: " + location.getLongitude());
                                locationLiveData.postValue(location);

                            }
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("debug", "getLastLocation onFailure", e);
                        }
                    });
        } else {
            Log.i("debug", "getLastLocation else");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("debug", "LocationService onStartCommand");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null); // Detiene el handler cuando el servicio se destruye
    }
}