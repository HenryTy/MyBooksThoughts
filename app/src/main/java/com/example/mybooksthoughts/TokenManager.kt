package com.example.mybooksthoughts

import android.content.Context
import androidx.preference.PreferenceManager
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

class TokenManager {

    companion object {
        private val REFRESH_TOKEN_KEY = "REFRESH_TOKEN_"

        private fun saveRefreshToken(context: Context, token: String, googleAccount: GoogleSignInAccount) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(
                userTokenKey(googleAccount), token).apply()
        }

        fun getToken(context: Context, googleAccount: GoogleSignInAccount, callback: (String?) -> (Unit)) {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val refreshToken = sharedPreferences.getString(userTokenKey(googleAccount), null)
            val isFirstRequest = refreshToken == null
            val getTokenTask =
                GetTokenTask(!isFirstRequest, context) { tokenResponse ->
                    if(tokenResponse != null) {
                        if (isFirstRequest) {
                            saveRefreshToken(context, tokenResponse.refreshToken, googleAccount)
                        }
                        callback.invoke(tokenResponse.accessToken)
                    } else {
                        callback.invoke(null)
                    }
                }
            val userToken = if(isFirstRequest) googleAccount.serverAuthCode else refreshToken
            getTokenTask.execute(userToken)
        }

        private fun userTokenKey(googleAccount: GoogleSignInAccount): String {
            return REFRESH_TOKEN_KEY + googleAccount.id
        }
    }
}