package com.example.chatease.data.local.datastore.user_preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.chatease.domain.model.enums.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
    }

    fun getCurrentTheme(): Flow<ThemeMode> {
        return context.userPreferencesDataStore.data.map { preferences ->
            val value = preferences[Keys.THEME_MODE]
            value?.let {
                runCatching { ThemeMode.valueOf(it) }.getOrDefault(ThemeMode.LIGHT)
            } ?: ThemeMode.LIGHT
        }
    }

    suspend fun setCurrentTheme(themeMode: ThemeMode) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[Keys.THEME_MODE] = themeMode.name
        }
    }
}