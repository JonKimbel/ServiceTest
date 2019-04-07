package com.jonkimbel.servicetest.ui;

import android.os.Bundle;

import com.jonkimbel.servicetest.R;
import com.jonkimbel.servicetest.api.HasState;

public class ActionStateController implements HasState {
    private final String actionCompletedKey;
    private final String actionRunningKey;

    // Persisted after the Activity stops.
    private boolean actionCompleted;
    private boolean actionRunning;
    private boolean actionFailed;

    public ActionStateController(String classTag, Bundle savedInstanceState) {
        actionCompletedKey = classTag + "actionCompleted";
        actionRunningKey = classTag + "actionRunning";
        if (savedInstanceState != null) {
            actionCompleted = savedInstanceState.getBoolean(actionCompletedKey, false);
            actionRunning = savedInstanceState.getBoolean(actionRunningKey, false);
        }
    }

    public void startWaiting() {
        actionRunning = true;
        actionFailed = false;
    }

    public void completeAction() {
        actionRunning = false;
        actionCompleted = true;
    }

    public void failAction() {
        actionRunning = false;
        actionFailed = true;
    }

    public boolean canStartNewAction() {
        return !actionRunning;
    }

    public int getIcon() {
        if (actionRunning) {
            return R.drawable.ic_baseline_timer_24px;
        } else if (actionFailed) {
            return R.drawable.ic_baseline_cancel_24px;
        } else if (actionCompleted) {
            return R.drawable.ic_baseline_check_circle_24px;
        } else {
            return R.drawable.ic_baseline_play_circle_filled_24px;
        }
    }

    public int getText() {
        if (actionRunning) {
            return R.string.waitingApproachButtonText;
        } else {
            return R.string.startApproachButtonText;
        }
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putBoolean(actionCompletedKey, actionCompleted);
        bundle.putBoolean(actionRunningKey, actionRunning);
    }

    @Override
    public void onStop() {
    }
}
