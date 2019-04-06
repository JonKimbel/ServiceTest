package com.jonkimbel.servicetest.settings;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import com.jonkimbel.servicetest.R;
import com.jonkimbel.servicetest.api.ActionCardViewModel;

import static com.jonkimbel.servicetest.settings.SettingsUtil.settingEnabled;

public class TurnOnDeveloperSettings implements ActionCardViewModel {
    private final Context activityContext;

    public TurnOnDeveloperSettings(Context activityContext) {
        this.activityContext = activityContext;
    }

    public static boolean shouldShow(Context activityContext) {
        return !settingEnabled(activityContext, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED);
    }

    @Override
    public int getTitleText() {
        return R.string.turnOnDeveloperSettingsTitle;
    }

    @Override
    public int getDescriptionText() {
        return R.string.turnOnDeveloperSettingsDescription;
    }

    @Override
    public int getButtonText() {
        return R.string.turnOnDeveloperSettingsButtonText;
    }

    @Override
    public boolean isButtonEnabled() {
        return true;
    }

    @Override
    public int getButtonIcon() {
        return R.drawable.ic_baseline_play_circle_filled_white_48px;
    }

    @Override
    public void onClick() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_DEVICE_INFO_SETTINGS);
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
