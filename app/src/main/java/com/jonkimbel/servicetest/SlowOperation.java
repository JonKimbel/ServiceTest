package com.jonkimbel.servicetest;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.TimeUnit;

public class SlowOperation extends Thread {
    private final static int SECONDS_TO_WAIT = 10;
    private final Listener callback;
    private final String data;

    public SlowOperation(String data, Listener callback) {
        this.data = data;
        this.callback = callback;
    }

    @Override
    public void run() {
        // Simulate some long-running task.
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(SECONDS_TO_WAIT));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> callback.onFinish(data));
    }

    public interface Listener {
        void onFinish(String result);
    }
}
