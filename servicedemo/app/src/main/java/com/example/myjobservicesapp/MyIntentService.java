package com.example.myjobservicesapp;


import android.Manifest;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.Random;

public class MyIntentService extends JobService implements LocationListener {

    private static final String TAG = MyIntentService.class.getSimpleName();
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 15; // 1 minute
    public static boolean STOPLOCATIONTARC = false;
    private final int MIN = 0;
    private final int MAX = 50;
    LocationRequest mLocationRequest;
    JobParameters jobParameters;
    private FusedLocationProviderClient mFusedLocationClient;
    private double latitude = 0.0;
    private double longitude = 0.0;
    private LocationCallback mLocationCallback;
    private int mRandomNumber;
    private boolean mIsRandomGeneratorOn;

    /***never get call but update location get in LocationCallback() ******/
    @Override
    public void onLocationChanged(Location location) {
        Log.e("Location----", +location.getLatitude() + "");
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("", "");
        mLocationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_LOW_POWER).setInterval(5000);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        /*****get current location update callback*/
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (STOPLOCATIONTARC) {
                    stopLocationTracking();
                } else {
                    if (locationResult == null) {
                        return;
                    } else {
                        for (Location location : locationResult.getLocations()) {
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                Log.e("Location----", +location.getLatitude() + " " + location.getLongitude());
                                /********/
                            }
                        }
                    }
                }
            }
        };

    }

    /**
     * Return FALSE when this jpb is of short duration
     * and needs to be executed for very small time.
     * By default everything here runs on UI thread. If you don't
     * want to block the  Stop JobUI thread with long running work, then use thread.
     * Return TRUE whenever you are running long running tasks. So when you are
     * using a thread to do long running task, return true.
     *
     * @param jobParameters
     * @return
     */
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        this.jobParameters = jobParameters;
        Log.i(getString(R.string.service_demo_tag), "onStartJob");
        doBackgroundWork();
        //  RestartService.restartService(this);

        return false;
    }


    private void doBackgroundWork() {
        new Thread(new Runnable() {
            @Override
            public void run() {


                /*****start job & when stop job from mainactivity than stop this fuction calling*****/
                startRandomNumberGenerator();
            }
        }).start();


        if (ActivityCompat.checkSelfPermission(MyIntentService.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MyIntentService.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(MyIntentService.this, "Location access permission deney.", Toast.LENGTH_SHORT).show();
            return;
        }


        if (mLocationCallback != null && mLocationRequest != null)
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);


    }

    /**
     * This method gets called when job gets cancelled.
     * Return True if you want to restart the job automatically when a condition is met (WIFI - ON)
     * Return false if you want don't want to restart the job automatically even when the condition is met (WIFI - OFF)
     *
     * @param jobParameters
     * @return
     */
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.i(getString(R.string.service_demo_tag), "onStopJob");
        RestartService.restartService(this);
        return false;
    }

    private void startRandomNumberGenerator() {
        while (mIsRandomGeneratorOn) {
            try {
                Thread.sleep(1000);
                if (mIsRandomGeneratorOn) {
                    mRandomNumber = new Random().nextInt(MAX) + MIN;
                    Log.i(getString(R.string.service_demo_tag), "Thread id: " + Thread.currentThread().getId() + ", Random Number: " + mRandomNumber);
                }
            } catch (InterruptedException e) {
                Log.i(getString(R.string.service_demo_tag), "Thread Interrupted");
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(getString(R.string.service_demo_tag), getString(R.string.string_stopservice) + ", thread Id: " + Thread.currentThread().getId());
        mIsRandomGeneratorOn = false;

    }

    private void stopLocationTracking() {
        if (mFusedLocationClient != null)
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

}

