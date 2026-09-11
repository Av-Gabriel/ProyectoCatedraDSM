package com.udb.appfinanzas.core.data

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val tokenKey = stringPreferencesKey("auth_token")

    val tokenFlow: Flow<String?> = context.dataStore.data.map { prefs -> prefs[tokenKey] }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { prefs -> prefs[tokenKey] = token }
    }

    suspend fun getTokenSync(): String? = tokenFlow.first()

    suspend fun clearToken() {
        context.dataStore.edit { prefs -> prefs.remove(tokenKey) }
    }
}