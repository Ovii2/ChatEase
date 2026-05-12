package com.example.chatease.data.local.datastore.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

val Context.authPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_preferences")