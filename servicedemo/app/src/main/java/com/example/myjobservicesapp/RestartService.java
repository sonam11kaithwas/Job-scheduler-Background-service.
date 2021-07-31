package com.example.myjobservicesapp;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

public class RestartService {
    public static void restartService(Context context) {
        ComponentName componentName = new ComponentName(context, MyIntentService.class);
        JobInfo jobInfo = new JobInfo.Builder(101, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_CELLULAR)//if net off than service is stop
                .setPeriodic(16 * 60 * 1000)//set time interval but default service time is 15 min
                .setRequiresCharging(false)//if set true than charging required/  false not required charging
                .setPersisted(true)
                .build();


        JobScheduler jobScheduler = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            jobScheduler = context.getSystemService(JobScheduler.class);
        }
        jobScheduler.schedule(jobInfo);
    }

}

