package com.jonkimbel.servicetest.settings;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import com.jonkimbel.servicetest.R;
import com.jonkimbel.servicetest.api.ActionCardViewModel;

import static com.jonkimbel.servicetest.settings.SettingsUtil.settingEnabled;

public class ChangeActivityDeveloperSetting implements ActionCardViewModel {
    private static final String EXTRA_FRAGMENT_ARG_KEY = ":settings:fragment_args_key";
    private final Context activityContext;

    public ChangeActivityDeveloperSetting(Context activityContext) {
        this.activityContext = activityContext;
    }

    public static boolean shouldShow(Context activityContext) {
        return settingEnabled(activityContext, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED)
                && !settingEnabled(activityContext, Settings.Global.ALWAYS_FINISH_ACTIVITIES);
    }

    @Override
    public int getTitleText() {
        return R.string.changeActivityDeveloperSettingTitle;
    }

    @Override
    public int getDescriptionText() {
        return R.string.changeActivityDeveloperSettingDescription;
    }

    @Override
    public int getButtonText() {
        return R.string.changeActivityDeveloperSettingButtonText;
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
        intent.setAction(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
        intent.putExtra(EXTRA_FRAGMENT_ARG_KEY, "bugreport_in_power");
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
