package com.jonkimbel.servicetest;

import android.os.Bundle;
import android.widget.TextView;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

/**
 * Renders a timer for a view. Timer is persisted across app stops/restarts.
 *
 * <p>Only one {@link Timer} should be used for each unique view.
 */
public class Timer implements HasStateToSave {
    private static final String START_TIME_KEY = "timer_startTimeMillis";

    private final TextView view;

    // Persisted to disk IFF the timer is currently started.
    private long startTimeMillis;

    private boolean started;

    private Timer(TextView view, @Nullable Bundle savedInstanceState) {
        this.view = view;

        if (savedInstanceState != null) {
            startTimeMillis = savedInstanceState.getLong(START_TIME_KEY + view.getId(), -1);
            if (startTimeMillis > 0) {
                started = true;
            }
        }

        if (started) {
            view.setText("Running...");
        }
    }

    public static Timer newInstance(TextView view, @Nullable Bundle savedInstanceState, Map<HasStateToSave, Boolean> statefulObjects) {
        Timer timer = new Timer(view, savedInstanceState);
        statefulObjects.put(timer, true);
        return timer;
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        if (started) {
            bundle.putLong(START_TIME_KEY + view.getId(), startTimeMillis);
        }
    }

    public void start() {
        startTimeMillis = System.currentTimeMillis();
        started = true;
        view.setText("Running...");
    }

    public void stop() {
        long elapsedTimeMillis = System.currentTimeMillis() - startTimeMillis;
        float elapsedTimeSeconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTimeMillis);
        view.setText(String.format("%.2f", elapsedTimeSeconds));
        started = false;
    }
}
