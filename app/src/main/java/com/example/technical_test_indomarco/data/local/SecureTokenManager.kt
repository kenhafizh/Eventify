package com.example.technical_test_indomarco.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.core.content.edit

class SecureTokenManager(context: Context) {

    private val masterKey: MasterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val securePrefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        FILE_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val FILE_NAME = "secure_auth_prefs"
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_USER_EMAIL = "user_email"

        @Volatile
        private var instance: SecureTokenManager? = null

        fun getInstance(context: Context): SecureTokenManager {
            return instance ?: synchronized(this) {
                instance ?: SecureTokenManager(context.applicationContext).also { instance = it }
            }
        }
    }

    fun saveAuthToken(token: String, email: String? = null) {
        securePrefs.edit().apply {
            putString(KEY_AUTH_TOKEN, token)
            if (!email.isNullOrBlank()) {
                putString(KEY_USER_EMAIL, email)
            }
            apply()
        }
    }

    fun getAuthToken(): String? {
        return securePrefs.getString(KEY_AUTH_TOKEN, null)
    }

    fun isLoggedIn(): Boolean {
        return !getAuthToken().isNullOrBlank()
    }

    fun clearSession() {
        securePrefs.edit {
            remove(KEY_AUTH_TOKEN)
                .remove(KEY_USER_EMAIL)
        }
    }
}
