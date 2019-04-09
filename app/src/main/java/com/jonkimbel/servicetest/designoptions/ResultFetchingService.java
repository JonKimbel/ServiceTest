package com.jonkimbel.servicetest.designoptions;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.jonkimbel.servicetest.SlowOperation;

import java.lang.ref.WeakReference;

import androidx.annotation.GuardedBy;

public class ResultFetchingService extends Service {
    private final IBinder binder = new ResultFetchingServiceBinder();
    private final Object taskResultMutex = new Object();
    private final Object runningMutex = new Object();
    private volatile WeakReference<Runnable> callback = new WeakReference<>(() -> {
    });
    @GuardedBy("runningMutex")
    private boolean running;
    @GuardedBy("taskResultMutex")
    private String taskResult;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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
            if (runnable != null) {
                runnable.run();
            }
        }).start();
    }

    public void registerCallback(Runnable callback) {
        this.callback = new WeakReference<>(callback);
    }

    public String consumeResult() {
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