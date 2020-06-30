package com.example.mybooksthoughts

import android.content.Context
import androidx.preference.PreferenceManager

class TokenManager {

    companion object {
        private val BOOKS_TOKEN_KEY = "BOOKS_TOKEN"

        fun saveToken(context: Context, token: String) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(BOOKS_TOKEN_KEY, token).apply()
        }

        fun getToken(context: Context): String? {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            return sharedPreferences.getString(BOOKS_TOKEN_KEY, null)
        }

        fun removeToken(context: Context) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().remove(BOOKS_TOKEN_KEY).apply()
        }
    }
}