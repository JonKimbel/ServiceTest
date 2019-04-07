package com.jonkimbel.servicetest.designoptions;

import android.os.Bundle;

import com.jonkimbel.servicetest.R;
import com.jonkimbel.servicetest.SlowOperation;
import com.jonkimbel.servicetest.api.ActionCardViewModel;
import com.jonkimbel.servicetest.api.HasState;
import com.jonkimbel.servicetest.ui.ActionStateController;

import java.util.Map;

public class SimpleCallbackApproach implements ActionCardViewModel {
    private static final String TAG = "SimpleCallbackApproach";

    private final ActionStateController actionStateController;

    private Runnable dataChangedCallback = () -> {
    };

    private SimpleCallbackApproach(ActionStateController actionStateController) {
        this.actionStateController = actionStateController;
    }

    public static SimpleCallbackApproach newInstance(Map<HasState, Boolean> statefulObjects, Bundle savedInstanceState) {
        ActionStateController actionStateController = new ActionStateController(TAG, savedInstanceState);
        statefulObjects.put(actionStateController, true);
        return new SimpleCallbackApproach(actionStateController);
    }

    @Override
    public int getTitleText() {
        return R.string.simpleCallbackTitle;
    }

    @Override
    public int getDescriptionText() {
        return R.string.simpleCallbackDescription;
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
    public boolean isSpecialCard() {
        return false;
    }

    @Override
    public void onClick() {
        new SlowOperation(TAG, result -> {
            if (TAG.equals(result)) {
                actionStateController.completeAction();
            } else {
                actionStateController.failAction();
            }
            dataChangedCallback.run();
        }).start();
        actionStateController.startWaiting();
        dataChangedCallback.run();
    }

    @Override
    public void setDataChangedCallback(Runnable callback) {
        dataChangedCallback = callback;
    }
}
