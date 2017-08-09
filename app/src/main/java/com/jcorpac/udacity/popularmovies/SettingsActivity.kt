package com.jcorpac.udacity.popularmovies

import android.os.Bundle
import android.preference.ListPreference
import android.preference.Preference
import android.preference.PreferenceActivity
import android.preference.PreferenceManager

class SettingsActivity : PreferenceActivity(), Preference.OnPreferenceChangeListener {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addPreferencesFromResource(R.xml.pref_main)

        bindPreferenceSummaryToValue(findPreference(getString(R.string.sort_by_key)))
    }

    private fun bindPreferenceSummaryToValue(preference: Preference) {
        preference.onPreferenceChangeListener = this

        onPreferenceChange(preference,
                PreferenceManager.getDefaultSharedPreferences(preference.context)
                        .getString(preference.key, ""))
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
        val stringValue = newValue.toString()

        if (preference is ListPreference) {
            val listPreference = preference
            val prefIndex = listPreference.findIndexOfValue(stringValue)
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.entries[prefIndex])
            }
        } else {
            preference.summary = stringValue
        }
        return true
    }
}