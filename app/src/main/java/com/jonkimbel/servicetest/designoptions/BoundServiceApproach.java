package com.jonkimbel.servicetest.designoptions;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.jonkimbel.servicetest.R;
import com.jonkimbel.servicetest.api.ActionCardViewModel;
import com.jonkimbel.servicetest.api.HasState;
import com.jonkimbel.servicetest.ui.ActionStateController;

import java.util.Map;

public class BoundServiceApproach implements ActionCardViewModel, ServiceConnection, HasState {
    private final static String TAG = "BoundServiceApproach";

    private final ActionStateController actionStateController;
    private final Context applicationContext;

    private ResultFetchingService service;
    private Runnable dataChangedCallback = () -> {
    };

    private BoundServiceApproach(Context applicationContext, ActionStateController actionStateController) {
        this.applicationContext = applicationContext;
        this.actionStateController = actionStateController;
        this.actionStateController.startWaiting();
    }

    public static BoundServiceApproach newInstance(Context applicationContext, Map<HasState, Boolean> statefulObjects, Bundle savedInstanceState) {
        ActionStateController actionStateController = new ActionStateController(TAG, savedInstanceState);
        BoundServiceApproach approach = new BoundServiceApproach(applicationContext, actionStateController);

        statefulObjects.put(actionStateController, true);
        statefulObjects.put(approach, true);

        return approach;
    }

    @Override
    public int getTitleText() {
        return R.string.serviceTitle;
    }

    @Override
    public int getDescriptionText() {
        return R.string.serviceDescription;
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
        service.startTask(TAG);

        actionStateController.startWaiting();
        dataChangedCallback.run();
    }

    @Override
    public void setDataChangedCallback(Runnable callback) {
        dataChangedCallback = callback;
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        service = ((ResultFetchingService.ResultFetchingServiceBinder) iBinder).getService();
        service.registerCallback(() -> {
            String result = service.consumeResult();
            if (TAG.equals(result)) {
                actionStateController.completeAction();
            } else {
                actionStateController.failAction();
            }
            dataChangedCallback.run();
        });

        if (!service.running()) {
            actionStateController.stopWaiting();
        }

        // Check for any results that might have come in while the service was running without an
        // activity.
        String result = service.consumeResult();
        if (result != null) {
            if (TAG.equals(result)) {
                actionStateController.completeAction();
            } else {
                actionStateController.failAction();
            }
        }
        dataChangedCallback.run();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        service = null;
        actionStateController.startWaiting();
        dataChangedCallback.run();
    }

    @Override
    public void onStart() {
        Intent serviceIntent = new Intent(applicationContext, ResultFetchingService.class);
        applicationContext.startService(serviceIntent);
        applicationContext.bindService(serviceIntent, this, 0);
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {

    }

    @Override
    public void onStop() {
        applicationContext.unbindService(this);
    }
}
