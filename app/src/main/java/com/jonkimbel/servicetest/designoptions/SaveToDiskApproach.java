package com.jonkimbel.servicetest.designoptions;

import android.content.Context;
import android.os.Bundle;

import com.jonkimbel.servicetest.R;
import com.jonkimbel.servicetest.SlowOperation;
import com.jonkimbel.servicetest.api.ActionCardViewModel;
import com.jonkimbel.servicetest.api.HasState;
import com.jonkimbel.servicetest.ui.ActionStateController;

import java.util.Map;

public class SaveToDiskApproach implements ActionCardViewModel, HasState {
    private static final String TAG = "SaveToDiskApproach";

    private final ActionStateController actionStateController;

    private final ResultSaver resultSaver;
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
        return actionStateController.canStartNewAction();
    }

    @Override
    public int getButtonIcon() {
        return actionStateController.getIcon();
    }

    @Override
    public void onClick() {
        new SlowOperation(TAG, result -> {
            if (activityStarted) {
                processResult(result);
            } else {
                resultSaver.save(result);
            }
        }).start();
        actionStateController.startWaiting();
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
        String result = resultSaver.load();
        if (result != null) {
            processResult(result);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
    }

    @Override
    public void onStop() {
        activityStarted = false;
    }

    private void processResult(String result) {
        if (TAG.equals(result)) {
            actionStateController.completeAction();
        } else {
            actionStateController.failAction();
        }
        dataChangedCallback.run();
    }
}
