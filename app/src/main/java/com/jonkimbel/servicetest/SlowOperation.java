package com.jonkimbel.servicetest;

import android.os.Handler;
import android.os.Looper;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class SlowOperation extends Thread {
    private final static int SECONDS_TO_WAIT = 10;

    public interface Listener {
        void onFinish(int result);
    }

    private final Listener callback;

    public SlowOperation(Listener callback) {
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
        int result = new Random().nextInt();

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> callback.onFinish(result));
    }
}
