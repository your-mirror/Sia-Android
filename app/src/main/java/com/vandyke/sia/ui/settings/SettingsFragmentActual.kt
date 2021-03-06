/*
 * Copyright (c) 2017 Nicholas van Dyke. All rights reserved.
 */

package com.vandyke.sia.ui.settings

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import com.vandyke.sia.BuildConfig
import com.vandyke.sia.R
import com.vandyke.sia.data.local.Prefs
import com.vandyke.sia.data.siad.SiadService
import com.vandyke.sia.util.GenUtil
import com.vandyke.sia.util.SnackbarUtil
import com.vandyke.sia.util.StorageUtil

/* the actual settings fragment, contained within SettingsFragment */
class SettingsFragmentActual : PreferenceFragmentCompat() {

    private var prefsListener: SharedPreferences.OnSharedPreferenceChangeListener? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings)

        findPreference("useExternal").onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, o ->
            if (StorageUtil.isExternalStorageWritable) {
                return@OnPreferenceChangeListener true
            } else {
                SnackbarUtil.showSnackbar(view, "Error: " + StorageUtil.externalStorageStateDescription(), Snackbar.LENGTH_LONG)
                return@OnPreferenceChangeListener false
            }
        }

        findPreference("displayedDecimalPrecision").onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
            try {
                return@OnPreferenceChangeListener Integer.parseInt(newValue as String) < 10
            } catch (e: Exception) {
                return@OnPreferenceChangeListener false
            }
        }

        findPreference("openAppSettings").onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val appSettings = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID))
            appSettings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(appSettings)
            false
        }

        findPreference("viewSubscription").onPreferenceClickListener = Preference.OnPreferenceClickListener {
            GenUtil.launchCustomTabs(context!!, "https://play.google.com/store/account/subscriptions")
            false
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        /* create and register a SharedPrefs PreferenceChangeListener that'll take appropriate action
         * when certain settings are changed. We are supposed to keep a reference, otherwise it could
         * be unregistered/destroyed. */
        prefsListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            when (key) {
                "apiPassword", "useExternal" -> SiadService.getService(context!!).subscribe { service ->
                    /* restart siad so that it'll switch storage directories */
                    service.restartSiad()
                }

                "darkMode" -> activity!!.recreate()
            }
        }
        Prefs.preferences.registerOnSharedPreferenceChangeListener(prefsListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        Prefs.preferences.unregisterOnSharedPreferenceChangeListener(prefsListener)
    }
}
