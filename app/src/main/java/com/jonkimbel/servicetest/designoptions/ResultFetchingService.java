package com.jonkimbel.servicetest.designoptions;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.jonkimbel.servicetest.SlowOperation;

import java.lang.ref.WeakReference;

public class ResultFetchingService extends Service {
    private final IBinder binder = new ResultFetchingServiceBinder();
    private String taskResult;
    private boolean running;
    private WeakReference<Runnable> callback = new WeakReference<>(() -> {});

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
        if (running) {
            throw new IllegalStateException("Can't start task while already running");
        }
        running = true;

        new SlowOperation(data, result -> {
            taskResult = result;
            running = false;
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
        String result = taskResult;
        taskResult = null;
        return result;
    }

    class ResultFetchingServiceBinder extends Binder {
        ResultFetchingService getService() {
            return ResultFetchingService.this;
        }
    }
}