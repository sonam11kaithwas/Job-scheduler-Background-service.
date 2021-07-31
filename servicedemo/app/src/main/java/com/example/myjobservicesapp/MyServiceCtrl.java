package com.example.myjobservicesapp;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class MyServiceCtrl extends Service implements LocationListener {
    public static String STARTFOREGROUND_ACTION = "com.myjobservicesapp.action.startforeground";
    public static String STOPFOREGROUND_ACTION = "com.myjobservicesapp.action.stopforeground";
    private final double latitude = 0.0;
    private final double longitude = 0.0;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationRequest locationRequest;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(STARTFOREGROUND_ACTION)) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            locationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_LOW_POWER).setInterval(2000);


            //get current location update callback
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        if (location != null) {
                            Log.e("Latituted=", +location.getLatitude() + "Longituted=" + location.getLongitude());

                        }
                    }
                }
            };

            startLocationUpdateing();

        } else {
            stopMyService();
        }


        return START_STICKY;

    }

    private void stopMyService() {
        if (mFusedLocationClient != null && mLocationCallback != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            stopSelf();
        }
    }

    private void startLocationUpdateing() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, null);
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.e("Location---", +location.getLatitude() + " " + location.getLongitude());
    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }
}
