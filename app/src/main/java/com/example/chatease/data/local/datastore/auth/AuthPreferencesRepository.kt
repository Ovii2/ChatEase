package com.example.chatease.data.local.datastore.auth

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private object Keys {
        val SAVED_EMAIL = stringPreferencesKey("saved_email")
        val REMEMBER_EMAIL = booleanPreferencesKey("remember_email")
    }

    suspend fun saveEmail(email: String, remember: Boolean) {
        context.authPreferencesDataStore.edit { preferences ->
            preferences[Keys.REMEMBER_EMAIL] = remember

            if (remember) {
                preferences[Keys.SAVED_EMAIL] = email
            } else {
                preferences.remove(Keys.SAVED_EMAIL)
            }
        }
    }

    fun getEmail(): Flow<String> {
        return context.authPreferencesDataStore.data.map { preferences ->
            preferences[Keys.SAVED_EMAIL] ?: ""
        }
    }

    fun getRememberEmail(): Flow<Boolean> {
        return context.authPreferencesDataStore.data.map { preferences ->
            preferences[Keys.REMEMBER_EMAIL] ?: false
        }
    }
}