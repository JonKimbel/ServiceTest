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
    }

    public void stopWaiting() {
        actionRunning = false;
    }

    public void completeAction() {
        actionRunning = false;
        actionCompleted = true;
    }

    public boolean canStartNewAction() {
        return !actionRunning;
    }

    public int getIcon() {
        if (actionRunning) {
            return R.drawable.ic_baseline_timer_24px;
        } else if (actionCompleted) {
            return R.drawable.ic_baseline_check_circle_24px;
        } else {
            return R.drawable.ic_baseline_play_circle_filled_white_48px;
        }
    }

    public int getText() {
        if (actionRunning) {
            return R.string.runningApproachButtonText;
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
