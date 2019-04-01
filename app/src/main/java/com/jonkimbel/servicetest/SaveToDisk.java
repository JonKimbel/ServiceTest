package com.jonkimbel.servicetest;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.common.base.Optional;

import java.util.Map;

public class SaveToDisk implements ActionCardViewModel, HasState {
    private static final String TAG = "SaveToDisk";

    private Timer timer;
    private ResultSaver resultSaver;
    private String timerText = "";
    private boolean activityStarted;
    private boolean checkMarkVisible;

    private SaveToDisk(Context applicationContext, Map<HasState, Boolean> statefulObjects, Bundle savedInstanceState) {
        timer = Timer.newInstance(TAG, text -> timerText = text, savedInstanceState, statefulObjects);
        resultSaver = new ResultSaver(applicationContext);
    }

    public static SaveToDisk newInstance(Context applicationContext, Map<HasState, Boolean> statefulObjects, Bundle savedInstanceState) {
        SaveToDisk viewModel = new SaveToDisk(applicationContext, statefulObjects, savedInstanceState);
        statefulObjects.put(viewModel, true);
        return viewModel;
    }

    @Override
    public String getTitleText() {
        return "Save to disk";
    }

    @Override
    public String getDescriptionText() {
        return "This approach will run the callback if the activity is running, " +
                "but if it's not running it will save the result to disk so the activity can " +
                "read it in onStart().";
    }

    @Override
    public String getTimerText() {
        return timerText;
    }

    @Override
    public boolean getCheckMarkViewVisibility() {
        return checkMarkVisible;
    }

    @Override
    public void onClick() {
        timer.start();
        new SlowOperation(result -> {
            if (activityStarted) {
                processResult(result);
            } else {
                resultSaver.save(result);
            }
        }).start();
    }

    @Override
    public void onStart() {
        activityStarted = true;
        Optional<Integer> result = resultSaver.load();
        if (result.isPresent()) {
            processResult(result.get());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {

    }

    @Override
    public void onStop() {
        activityStarted = false;
    }

    private void processResult(int result) {
        Log.d(TAG, "REKT Operation completed, result = " + result);
        checkMarkVisible = true;
        timer.stop();
    }
}
