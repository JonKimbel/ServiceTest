package com.jonkimbel.servicetest.approaches;

import android.content.Context;
import android.os.Bundle;

import com.google.common.base.Optional;
import com.jonkimbel.servicetest.R;
import com.jonkimbel.servicetest.SlowOperation;
import com.jonkimbel.servicetest.api.ActionCardViewModel;
import com.jonkimbel.servicetest.api.HasState;
import com.jonkimbel.servicetest.ui.ActionStateController;

import java.util.Map;

public class SaveToDiskApproach implements ActionCardViewModel, HasState {
    private static final String TAG = "SaveToDiskApproach";

    private final ActionStateController actionStateController;

    private ResultSaver resultSaver;
    private Runnable dataChangedCallback = () -> {
    };
    private boolean activityStarted;

    private SaveToDiskApproach(Context applicationContext, ActionStateController actionStateController) {
        resultSaver = new ResultSaver(applicationContext);
        this.actionStateController = actionStateController;
    }

    public static SaveToDiskApproach newInstance(Context applicationContext, Map<HasState, Boolean> statefulObjects, Bundle savedInstanceState) {
        ActionStateController actionStateController = new ActionStateController(TAG, savedInstanceState);
        SaveToDiskApproach approach = new SaveToDiskApproach(applicationContext, actionStateController);
        statefulObjects.put(actionStateController, true);
        statefulObjects.put(approach, true);
        return approach;
    }

    @Override
    public int getTitleText() {
        return R.string.saveToDiskTitle;
    }

    @Override
    public int getDescriptionText() {
        return R.string.saveToDiskDescription;
    }

    @Override
    public int getButtonText() {
        return actionStateController.getText();
    }

    @Override
    public boolean isButtonEnabled() {
        return !actionStateController.isActionRunning();
    }

    @Override
    public int getButtonIcon() {
        return actionStateController.getIcon();
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
        actionStateController.startAction();
        dataChangedCallback.run();
    }

    @Override
    public void setDataChangedCallback(Runnable callback) {
        dataChangedCallback = callback;
    }

    @Override
    public boolean isSpecialCard() {
        return false;
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
        actionStateController.completeAction();
        dataChangedCallback.run();
    }
}
