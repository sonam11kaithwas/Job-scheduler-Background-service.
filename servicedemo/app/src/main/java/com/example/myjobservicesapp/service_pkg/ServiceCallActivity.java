package com.example.myjobservicesapp.service_pkg;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.myjobservicesapp.MyServiceCtrl;
import com.example.myjobservicesapp.R;

import static android.os.Build.VERSION_CODES.M;

public class ServiceCallActivity extends AppCompatActivity {
    private static final int LOCATION_REQUEST_CODE = 100;
    private Intent myServiceCtrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_call);


        ask_User_RunTime_PerMission();
    }


    private void ask_User_RunTime_PerMission() {
        if (Build.VERSION.SDK_INT >= M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        LOCATION_REQUEST_CODE);
            } else {
                Toast.makeText(this, "Allow location permission for background location", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("PerMission Granted", "----->>>>>");
                    Toast.makeText(this, "PerMission Granted.......", Toast.LENGTH_SHORT).show();


                } else {
                    Log.e("PerMission Denied", "----->>>>>");
                    Toast.makeText(this, "Location permission granted required for latituted & longituted", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_service_btn:
                Intent myServiceCtrl = new Intent(this, MyServiceCtrl.class);
                myServiceCtrl.setAction(MyServiceCtrl.STARTFOREGROUND_ACTION);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    this.startService(myServiceCtrl);
                } else {
                    this.startService(new Intent(myServiceCtrl));
                }
                break;
            case R.id.stop_service_btn:
                Intent myServiceStop = new Intent(this, MyServiceCtrl.class);
                myServiceStop.setAction(MyServiceCtrl.STOPFOREGROUND_ACTION);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    this.startService(myServiceStop);
                } else {
                    this.startService(new Intent(myServiceStop));
                }
                break;
        }

    }
}