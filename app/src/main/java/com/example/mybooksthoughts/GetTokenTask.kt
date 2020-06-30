package com.example.mybooksthoughts

import android.os.AsyncTask
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory

class GetTokenTask(callback: (String) -> (Unit))
    : AsyncTask<GoogleSignInAccount, Int, String>() {

    private var callback: ((String) -> (Unit))? = null

    init {
        setCallback(callback)
    }

    internal fun setCallback(callback: (String) -> (Unit)) {
        this.callback = callback
    }

    override fun onPreExecute() {
    }

    override fun doInBackground(vararg accounts: GoogleSignInAccount): String {
        val tokenResponse = GoogleAuthorizationCodeTokenRequest(
            NetHttpTransport(),
            JacksonFactory.getDefaultInstance(),
            "https://oauth2.googleapis.com/token",
            Keys.CLIENT_ID,
            Keys.CLIENT_SECRET,
            accounts[0].serverAuthCode,
            ""
        )
            .execute()

        return tokenResponse.accessToken
    }

    override fun onPostExecute(result: String) {
        callback?.invoke(result)
    }
}