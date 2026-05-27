package com.example.coffeu.data.session

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SessionManager(context: Context) {

    private val appContext = context.applicationContext

    private val masterKey: MasterKey = MasterKey.Builder(appContext)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        appContext,
        PREFS_FILE_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveTokens(accessToken: String, refreshToken: String) {
        sharedPreferences.edit {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            putString(KEY_LEGACY_AUTH_TOKEN, accessToken)
        }
    }

    fun saveAccessToken(accessToken: String) {
        sharedPreferences.edit {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_LEGACY_AUTH_TOKEN, accessToken)
        }
    }

    fun saveRefreshToken(refreshToken: String) {
        sharedPreferences.edit {
            putString(KEY_REFRESH_TOKEN, refreshToken)
        }
    }

    fun getAccessToken(): String? {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
            ?: sharedPreferences.getString(KEY_LEGACY_AUTH_TOKEN, null)
    }

    fun getRefreshToken(): String? {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, null)
    }

    fun isLoggedIn(): Boolean = !getAccessToken().isNullOrBlank()

    fun clearSession() {
        sharedPreferences.edit {
            remove(KEY_ACCESS_TOKEN)
            remove(KEY_REFRESH_TOKEN)
            remove(KEY_LEGACY_AUTH_TOKEN)
        }
    }

    companion object {
        private const val PREFS_FILE_NAME = "secure_session_prefs"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_LEGACY_AUTH_TOKEN = "auth_token"
    }
}
