package com.jonkimbel.servicetest.settings;

import android.content.Context;
import android.provider.Settings;

class SettingsUtil {
    static boolean settingEnabled(Context activityContext, String setting) {
        return Settings.Global.getInt(activityContext.getContentResolver(), setting, 0) != 0;
    }
}
