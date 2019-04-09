package com.jonkimbel.servicetest.designoptions;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.jonkimbel.servicetest.R;
import com.jonkimbel.servicetest.api.ActionCardViewModel;
import com.jonkimbel.servicetest.api.HasState;

import java.util.Map;

public class BoundServiceApproach implements ActionCardViewModel, ServiceConnection, HasState {
    private final static String TAG = "BoundServiceApproach";
    private final static String LAST_RUN_RESULT_KEY = TAG + "lastRunResult";

    private final Context applicationContext;

    private ResultFetchingService service;
    private Runnable dataChangedCallback = () -> {
    };
    private boolean activityStarted;
    private LastRunResult lastRunResult = LastRunResult.NEVER_RUN;

    private BoundServiceApproach(Context applicationContext, Bundle savedInstanceState) {
        this.applicationContext = applicationContext;
        if (savedInstanceState != null) {
            lastRunResult = LastRunResult.values()[savedInstanceState.getInt(LAST_RUN_RESULT_KEY, LastRunResult.NEVER_RUN.ordinal())];
        }
    }

    public static BoundServiceApproach newInstance(Context applicationContext, Map<HasState, Boolean> statefulObjects, Bundle savedInstanceState) {
        BoundServiceApproach approach = new BoundServiceApproach(applicationContext, savedInstanceState);
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
        if (service == null) {
            return R.string.serviceNotBoundButtonText;
        } else if (service.running()) {
            return R.string.waitingApproachButtonText;
        } else {
            return R.string.startApproachButtonText;
        }
    }

    @Override
    public boolean isButtonEnabled() {
        return service != null && !service.running();
    }

    @Override
    public int getButtonIcon() {
        if (service == null || service.running()) {
            return R.drawable.ic_baseline_timer_24px;
        }

        switch (lastRunResult) {
            case FAILED:
                return R.drawable.ic_baseline_cancel_24px;
            case COMPLETED:
                return R.drawable.ic_baseline_check_circle_24px;
            case NEVER_RUN:
                return R.drawable.ic_baseline_play_circle_filled_24px;
        }
        throw new RuntimeException("LastRunResult enum changed after compilation");
    }

    @Override
    public boolean isSpecialCard() {
        return false;
    }

    @Override
    public void onClick() {
        service.startTask(TAG);
        dataChangedCallback.run();
    }

    @Override
    public void setDataChangedCallback(Runnable callback) {
        dataChangedCallback = callback;
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        Log.e(TAG, "REKT onServiceConnected");
        service = ((ResultFetchingService.ResultFetchingServiceBinder) iBinder).getService();
        service.registerCallback(() -> {
            Log.e(TAG, "REKT service callback lambda");
            if (!activityStarted) {
                return;
            }
            if (applyNewResult()) {
                dataChangedCallback.run();
            }
        });

        // Check for any results that might have come in while the service was unbound. We don't
        // care what it returns, since we always need to update the UI when the service is re-bound.
        applyNewResult();
        dataChangedCallback.run();
    }

    /**
     * Checks to see if the service has a new result. If it does, the result will be consumed and
     * applied to {@link #lastRunResult} and this method will return true.
     */
    private boolean applyNewResult() {
        String result = service.consumeResult();
        Log.e(TAG, String.format("REKT applyNewResult, %s new result", result == null ? "did not get" : "got"));
        if (result != null) {
            lastRunResult = TAG.equals(result) ? LastRunResult.COMPLETED : LastRunResult.FAILED;
            return true;
        }
        return false;
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        service = null;
        dataChangedCallback.run();
    }

    @Override
    public void onStart() {
        Log.e(TAG, "REKT onStart");
        Intent serviceIntent = new Intent(applicationContext, ResultFetchingService.class);
        applicationContext.startService(serviceIntent);
        applicationContext.bindService(serviceIntent, this, 0);
        activityStarted = true;
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putInt(LAST_RUN_RESULT_KEY, lastRunResult.ordinal());
    }

    @Override
    public void onStop() {
        Log.e(TAG, "REKT onStop");
        activityStarted = false;
        applicationContext.unbindService(this);
    }

    private enum LastRunResult {
        NEVER_RUN,
        COMPLETED,
        FAILED,
    }
}
