package com.example.myjobservicesapp;

import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.myjobservicesapp.service_pkg.ServiceCallActivity;

import static android.os.Build.VERSION_CODES.M;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    JobScheduler jobScheduler;
    private Button buttonStart, buttonStop;
    private TextView textViewthreadCount;
    private RelativeLayout layout1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout1 = findViewById(R.id.layout1);

        jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);

        Log.i(getString(R.string.service_demo_tag), "MainActivity thread id: " + Thread.currentThread().getId());

        buttonStart = findViewById(R.id.buttonThreadStarter);
        buttonStop = findViewById(R.id.buttonStopthread);

        textViewthreadCount = findViewById(R.id.textViewthreadCount);

        buttonStart.setOnClickListener(this);
        buttonStop.setOnClickListener(this);


        /*****ask user permission for locationn access*/
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
                        100);
            }
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonJobScheduler:
                layout1.setVisibility(View.VISIBLE);
                break;
            case R.id.buttonServicer:
                stopJob();
                layout1.setVisibility(View.GONE);
                Intent intent = new Intent(this, ServiceCallActivity.class);
                startActivity(intent);
                this.finish();
                break;
            case R.id.buttonThreadStarter:
                startJob();
                break;
            case R.id.buttonStopthread:
                stopJob();
                break;
        }
    }

    private void startJob() {
        ComponentName componentName = new ComponentName(this, MyIntentService.class);
        JobInfo jobInfo = new JobInfo.Builder(101, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_CELLULAR)//if net off than service is stop
                .setPeriodic(16 * 60 * 1000)//set time interval but default service time is 15 min
                .setRequiresCharging(false)//if set true than charging required/  false not required charging
                .setPersisted(true)
                //   .setMinimuMyIntentServicemLatency(000)
                .build();

        if (jobScheduler.schedule(jobInfo) == JobScheduler.RESULT_SUCCESS) {
            Log.i(getString(R.string.service_demo_tag), "MainActivity thread id: " + Thread.currentThread().getId() + ", job successfully scheduled");
        } else {
            Log.i(getString(R.string.service_demo_tag), "MainActivity thread id: " + Thread.currentThread().getId() + ", job could not be scheduled");
        }

    }

    private void stopJob() {
        if (jobScheduler != null) {
            jobScheduler.cancel(101);
            MyIntentService.STOPLOCATIONTARC = true;
        }
    }

}