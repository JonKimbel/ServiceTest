package com.jonkimbel.servicetest.savetodisk;

import android.content.Context;
import android.os.Bundle;

import com.google.common.base.Optional;
import com.jonkimbel.servicetest.api.ActionCardViewModel;
import com.jonkimbel.servicetest.api.HasState;
import com.jonkimbel.servicetest.SlowOperation;

import java.util.Map;

public class SaveToDisk implements ActionCardViewModel, HasState {
    private static final String TAG = "SaveToDisk";
    private static final String checkMarkVisibleKey = TAG + "checkMarkVisible";
    private static final String waitingIconVisibleKey = TAG + "waitingIconVisible";

    private ResultSaver resultSaver;
    private Runnable dataChangedCallback = () -> { };
    private boolean activityStarted;

    // Persisted when Activity stops.
    private boolean checkMarkVisible;
    private boolean waitingIconVisible;

    private SaveToDisk(Context applicationContext, Map<HasState, Boolean> statefulObjects, Bundle savedInstanceState) {
        resultSaver = new ResultSaver(applicationContext);
    }

    public static SaveToDisk newInstance(Context applicationContext, Map<HasState, Boolean> statefulObjects, Bundle savedInstanceState) {
        SaveToDisk viewModel = new SaveToDisk(applicationContext, statefulObjects, savedInstanceState);
        statefulObjects.put(viewModel, true);
        if (savedInstanceState != null) {
            viewModel.checkMarkVisible = savedInstanceState.getBoolean(checkMarkVisibleKey, false);
            viewModel.waitingIconVisible = savedInstanceState.getBoolean(waitingIconVisibleKey, false);
        }
        return viewModel;
    }

    @Override
    public String getTitleText() {
        return "Save to disk";
    }

    @Override
    public String getDescriptionText() {
        return "This approach will run the callback if the activity is running, " +
                "but if it's not running it will save the result to disk (not the bundle!) so " +
                "the activity can read it in onStart().";
    }

    @Override
    public boolean getCheckMarkViewVisibility() {
        return checkMarkVisible;
    }

    @Override
    public void onClick() {
        new SlowOperation(result -> {
            if (activityStarted) {
                processResult(result);
            } else {
                resultSaver.save(result);
            }
        }).start();
        checkMarkVisible = false;
        waitingIconVisible = true;
        dataChangedCallback.run();
    }

    @Override
    public void setDataChangedCallback(Runnable callback) {
        dataChangedCallback = callback;
    }

    @Override
    public boolean getWaitingIconViewVisibility() {
        return waitingIconVisible;
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
        bundle.putBoolean(checkMarkVisibleKey, checkMarkVisible);
        bundle.putBoolean(waitingIconVisibleKey, waitingIconVisible);
    }

    @Override
    public void onStop() {
        activityStarted = false;
    }

    private void processResult(int result) {
        checkMarkVisible = true;
        waitingIconVisible = false;
        dataChangedCallback.run();
    }
}
