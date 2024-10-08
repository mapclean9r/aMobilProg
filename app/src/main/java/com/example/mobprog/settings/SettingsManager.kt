package com.example.mobprog.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class SettingsManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    companion object {
        private const val DARKMODE_KEY = "dark_mode"
    }

    fun saveDarkMode(isDarkMode: Boolean) {
        sharedPreferences.edit().putBoolean(DARKMODE_KEY, isDarkMode).apply()
    }

    fun isDarkMode(): Boolean {
        return sharedPreferences.getBoolean(DARKMODE_KEY, false)
    }
}