package com.example.android.movieapp1;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;


import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity
    implements Preference.OnPreferenceChangeListener {
    /**
     * Determines whether to always show the simplified settings UI, where
     * settings are presented in a single list. When false, settings are shown
     * as a master/detail two-pane view on tablets. When true, a single pane is
     * shown on tablets.
     */

    private static String APP_TAG = "MOVIE_APP1_Settings Activity";
    private int prev_index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setupActionBar();
        Log.v(APP_TAG, "Its calling the settings activity");

        addPreferencesFromResource(R.xml.pref_general);

        bindPreferenceSummaryToValue(findPreference(getString(R.string.api_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.sort_pref)));

    }

    /**
     * Attaches a listener so the summary is always updated with the preference value.
     * Also fires the listener once, to initialize the summary (so it shows up before the value
     * is changed.)
     */
    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
       preference.setOnPreferenceChangeListener(this);

        Object prefManager = PreferenceManager
                .getDefaultSharedPreferences(preference.getContext())
                .getString(preference.getKey(), "");

        // Get the previous state value
        if (preference instanceof ListPreference ) {
            ListPreference listPreference = (ListPreference) preference;
            prev_index = listPreference.findIndexOfValue(prefManager.toString());
            Log.v(APP_TAG, "THE Previous INDEX IS  " + prev_index);
        }

        // Trigger the listener immediately with the preference's
        // current value.
        onPreferenceChange(preference, prefManager);

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();
        int prefIndex = -1;
        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prev_index != prefIndex ) {
                Log.v(APP_TAG, "Preference value changed");
                finish();
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainIntent);
            }

            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            preference.setSummary(stringValue);
        }

        return true;
    }



}
