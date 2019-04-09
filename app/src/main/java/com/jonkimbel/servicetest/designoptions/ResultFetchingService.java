package com.jonkimbel.servicetest.designoptions;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.jonkimbel.servicetest.SlowOperation;

import java.lang.ref.WeakReference;

import androidx.annotation.GuardedBy;

public class ResultFetchingService extends Service {
    private final static String TAG = "ResultFetchingService";

    private final IBinder binder = new ResultFetchingServiceBinder();
    private final Object taskResultMutex = new Object();
    private final Object runningMutex = new Object();
    private WeakReference<Runnable> callback = new WeakReference<>(() -> {
    });
    @GuardedBy("runningMutex")
    private boolean running;
    @GuardedBy("taskResultMutex")
    private String taskResult;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "REKT onStartCommand");
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public boolean running() {
        return running;
    }

    public void startTask(String data) {
        Log.e(TAG, "REKT startTask");
        synchronized (runningMutex) {
            if (running) {
                throw new IllegalStateException("Can't start task while already running");
            }
            running = true;
        }

        new SlowOperation(data, result -> {
            synchronized (taskResultMutex) {
                taskResult = result;
            }
            synchronized (runningMutex) {
                running = false;
            }
            Runnable runnable = callback.get();
            Log.e(TAG, String.format("REKT task finishing, runnable is %s", runnable == null ? "null" : "non-null"));
            if (runnable != null) {
                runnable.run();
            }
        }).start();
    }

    public void registerCallback(Runnable callback) {
        Log.e(TAG, "REKT registerCallback");
        this.callback = new WeakReference<>(callback);
    }

    public String consumeResult() {
        Log.e(TAG, "REKT consumeResult");
        String result;
        synchronized (taskResultMutex) {
            result = taskResult;
            taskResult = null;
        }
        return result;
    }

    class ResultFetchingServiceBinder extends Binder {
        ResultFetchingService getService() {
            return ResultFetchingService.this;
        }
    }
}