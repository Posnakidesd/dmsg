package com.demetrisp.dmsg;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

public class PrefsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        PackageManager pm = getActivity().getPackageManager();

        if (!pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
            Preference import_sms_pref =
                    getPreferenceScreen().findPreference("pref_export_sms");
            import_sms_pref.setEnabled(false);
            import_sms_pref.setSelectable(false);
            Preference export_sms_pref =
                    getPreferenceScreen().findPreference("pref_import_sms");
            export_sms_pref.setEnabled(false);
            export_sms_pref.setSelectable(false);
        }


    }


}

