package com.mason.pokemonminigames.util

import android.content.Context
import androidx.core.content.edit

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("session_prefs", Context.MODE_PRIVATE)

    fun setBiometricEnabled(enabled: Boolean) = prefs.edit {
        putBoolean("biometric_enabled", enabled)
    }

    fun isBiometricEnabled(): Boolean =
        prefs.getBoolean("biometric_enabled", false)

    fun setLanguage(code: String) = prefs.edit {
        putString("lang_code", code)
    }

    fun getLanguage(): String =
        prefs.getString("lang_code", "en") ?: "en"
}
