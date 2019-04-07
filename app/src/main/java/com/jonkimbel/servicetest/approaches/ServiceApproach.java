package com.jonkimbel.servicetest.approaches;

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

public class ServiceApproach implements ActionCardViewModel, ServiceConnection, HasState {
    private final static String TAG = "ServiceApproach";

    private final ActionStateController actionStateController;
    private final Context applicationContext;

    private ResultFetchingService service;
    private Runnable dataChangedCallback = () -> {
    };

    private ServiceApproach(Context applicationContext, ActionStateController actionStateController) {
        this.applicationContext = applicationContext;
        this.actionStateController = actionStateController;
        this.actionStateController.startWaiting();
    }

    public static ServiceApproach newInstance(Context applicationContext, Map<HasState, Boolean> statefulObjects, Bundle savedInstanceState) {
        ActionStateController actionStateController = new ActionStateController(TAG, savedInstanceState);
        ServiceApproach approach = new ServiceApproach(applicationContext, actionStateController);

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
        service.startTask();

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
            service.consumeResult();
            actionStateController.completeAction();
            dataChangedCallback.run();
        });

        if (!service.running()) {
            actionStateController.stopWaiting();
        }

        // Check for any results that might have come in while the service was running without an
        // activity.
        Integer result = service.consumeResult();
        if (result != null) {
            actionStateController.completeAction();
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
