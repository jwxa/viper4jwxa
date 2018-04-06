package com.github.jwxa;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import com.github.jwxa.BuildConfig;
import com.github.jwxa.R;


public class SettingFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private EditTextPreference mEditTextPreference;

    public SettingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesMode(Context.MODE_WORLD_READABLE);
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        addPreferencesFromResource(R.xml.preference);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mEditTextPreference = (EditTextPreference) findPreference("magnification");
        findPreference("version").setSummary(BuildConfig.VERSION_NAME);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        getKey();
        return true;
    }

    private void getKey() {
        Intent intent = new Intent("com.github.jwxa.SETTING_CHANGED");
        boolean viperSwitch = getPreferenceManager().getSharedPreferences().getBoolean("viper", true);
        intent.putExtra("viper", viperSwitch);
        if (getActivity() != null) {
            getActivity().sendBroadcast(intent);
        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        getKey();
    }
}
