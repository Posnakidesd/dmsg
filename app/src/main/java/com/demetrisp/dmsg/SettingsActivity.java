package com.demetrisp.dmsg;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by dima on 10/24/14.
 */
public class SettingsActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PrefsFragment()).commit();
    }
}