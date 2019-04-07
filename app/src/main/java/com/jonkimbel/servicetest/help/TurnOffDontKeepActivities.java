package com.jonkimbel.servicetest.help;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import com.jonkimbel.servicetest.R;
import com.jonkimbel.servicetest.api.ActionCardViewModel;

import static com.jonkimbel.servicetest.help.SettingsUtil.settingEnabled;

public class TurnOffDontKeepActivities implements ActionCardViewModel {
    private final Context activityContext;

    public TurnOffDontKeepActivities(Context activityContext) {
        this.activityContext = activityContext;
    }

    public static boolean shouldShow(Context activityContext) {
        return settingEnabled(activityContext, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED)
                && settingEnabled(activityContext, Settings.Global.ALWAYS_FINISH_ACTIVITIES);
    }

    @Override
    public int getTitleText() {
        return R.string.turnOffDontKeepActivitiesTitle;
    }

    @Override
    public int getDescriptionText() {
        return R.string.turnOffDontKeepActivitiesDescription;
    }

    @Override
    public int getButtonText() {
        return R.string.turnOffDontKeepActivitiesButtonText;
    }

    @Override
    public boolean isButtonEnabled() {
        return true;
    }

    @Override
    public int getButtonIcon() {
        return R.drawable.ic_baseline_settings_20px;
    }

    @Override
    public void onClick() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
        activityContext.startActivity(intent);
    }

    @Override
    public void setDataChangedCallback(Runnable callback) {

    }

    @Override
    public boolean isSpecialCard() {
        // This is an important action, so we want it to be called out.
        return true;
    }
}
