package com.jonkimbel.servicetest;

import android.os.Bundle;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

/**
 * Renders a timer for a callback. Timer is persisted across app stops/restarts.
 *
 * <p>Only one {@link Timer} should be used for each unique callback.
 */
public class Timer implements HasState {
    public interface SetTextCallback {
        void setText(String text);
    }

    private static final String START_TIME_KEY = "timer_startTimeMillis";

    private final SetTextCallback callback;
    private final String uniqueKey;

    // Persisted to disk IFF the timer is started when onSaveInstanceState() is called.
    private long startTimeMillis;

    private boolean started;

    private Timer(String uniqueKey, SetTextCallback callback, @Nullable Bundle savedInstanceState) {
        this.callback = callback;
        this.uniqueKey = uniqueKey;

        if (savedInstanceState != null) {
            startTimeMillis = savedInstanceState.getLong(START_TIME_KEY + uniqueKey, -1);
            if (startTimeMillis > 0) {
                started = true;
            }
        }

        if (started) {
            callback.setText("Running...");
        }
    }

    public static Timer newInstance(String uniqueKey, SetTextCallback callback, @Nullable Bundle savedInstanceState, Map<HasState, Boolean> statefulObjects) {
        Timer timer = new Timer(uniqueKey, callback, savedInstanceState);
        statefulObjects.put(timer, true);
        return timer;
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        if (started) {
            bundle.putLong(START_TIME_KEY + uniqueKey, startTimeMillis);
        }
    }

    @Override
    public void onStop() {
    }

    public void start() {
        startTimeMillis = System.currentTimeMillis();
        started = true;
        callback.setText("Running...");
    }

    public void stop() {
        long elapsedTimeMillis = System.currentTimeMillis() - startTimeMillis;
        float elapsedTimeSeconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTimeMillis);
        callback.setText(String.format("%.2f", elapsedTimeSeconds));
        started = false;
    }
}
