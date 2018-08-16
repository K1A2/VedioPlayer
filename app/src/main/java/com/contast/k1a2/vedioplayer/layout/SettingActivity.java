package com.contast.k1a2.vedioplayer.layout;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;

import com.contast.k1a2.vedioplayer.R;

public class SettingActivity extends PreferenceActivity implements Preference.OnPreferenceClickListener {

    private SwitchPreference switchPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.srtting);

        getActionBar().hide();

        switchPreference = (SwitchPreference)findPreference("orien");
        switchPreference.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals("orien")) {

        }
        return false;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SettingActivity.this, MainActivity.class));
        finish();
    }
}
