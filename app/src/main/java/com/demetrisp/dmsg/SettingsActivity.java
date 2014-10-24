package com.demetrisp.dmsg;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by dima on 10/24/14.
 */
public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}