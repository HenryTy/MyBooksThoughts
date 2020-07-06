package com.example.mybooksthoughts

import android.content.Context
import androidx.preference.PreferenceManager
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

class TokenManager {

    companion object {
        private val BOOKS_TOKEN_KEY = "BOOKS_TOKEN"

        fun saveToken(context: Context, token: String) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(BOOKS_TOKEN_KEY, token).apply()
        }

        fun getToken(context: Context, googleAccount: GoogleSignInAccount, callback: (String) -> (Unit)) {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val token = sharedPreferences.getString(BOOKS_TOKEN_KEY, null)
            if(token == null) {
                val getTokenTask =
                    GetTokenTask { accessToken ->
                        saveToken(context, accessToken)
                        callback.invoke(accessToken) }
                getTokenTask.execute(googleAccount)
            }
            else {
                callback.invoke(token)
            }
        }

        fun removeToken(context: Context) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().remove(BOOKS_TOKEN_KEY).apply()
        }
    }
}