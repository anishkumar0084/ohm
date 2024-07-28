package com.ohmshantiapps.Chats;

import android.app.Application;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.ohmshantiapps.Chats.CleanupWorker;

import java.util.concurrent.TimeUnit;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Schedule the CleanupWorker to run every hour
        PeriodicWorkRequest cleanupRequest = new PeriodicWorkRequest.Builder(
                CleanupWorker.class,
                1,
                TimeUnit.HOURS
        ).build();

        WorkManager.getInstance(this).enqueue(cleanupRequest);

    }
}
