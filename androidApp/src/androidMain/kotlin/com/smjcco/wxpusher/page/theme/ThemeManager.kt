package com.smjcco.wxpusher.page.theme

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

object ThemeManager {

    private const val PREF_NAME = "theme_prefs"
    private const val KEY_DARK_MODE = "dark_mode"
    private const val KEY_THEME_COLOR = "theme_color"

    private const val DEFAULT_COLOR = 0xFF0088FF.toInt()

    fun getDarkMode(context: Context): ThemeMode {
        val prefs = getPrefs(context)
        val ordinal = prefs.getInt(KEY_DARK_MODE, ThemeMode.SYSTEM.ordinal)
        return ThemeMode.entries.getOrElse(ordinal) { ThemeMode.SYSTEM }
    }

    fun setDarkMode(context: Context, mode: ThemeMode) {
        getPrefs(context).edit().putInt(KEY_DARK_MODE, mode.ordinal).apply()
    }

    fun getThemeColor(context: Context): Int {
        val prefs = getPrefs(context)
        return prefs.getInt(KEY_THEME_COLOR, DEFAULT_COLOR)
    }

    fun setThemeColor(context: Context, color: Int) {
        getPrefs(context).edit().putInt(KEY_THEME_COLOR, color).apply()
    }

    fun applyTheme(context: Context) {
        val mode = getDarkMode(context)
        AppCompatDelegate.setDefaultNightMode(
            when (mode) {
                ThemeMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
                ThemeMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
                ThemeMode.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        )
    }

    fun init(context: Context) {
        applyTheme(context)
    }

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
}
