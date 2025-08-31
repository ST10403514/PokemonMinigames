package com.mason.pokemonminigames.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.Locale

object LocaleHelper {
    @SuppressLint("ObsoleteSdkInt")
    fun applyLocale(base: Context, langCode: String): Context {
        // Use forLanguageTag instead of deprecated constructor
        val locale = Locale.forLanguageTag(langCode)
        Locale.setDefault(locale)
        val config = Configuration(base.resources.configuration)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
            base.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
            @Suppress("DEPRECATION")
            base.resources.updateConfiguration(config, base.resources.displayMetrics)
            base
        }
    }
}
