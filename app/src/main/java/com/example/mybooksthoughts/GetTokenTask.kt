package com.example.mybooksthoughts

import android.content.Context
import android.net.ConnectivityManager
import android.os.AsyncTask
import com.google.api.client.auth.oauth2.TokenResponse
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.books.BooksScopes


class GetTokenTask(refresh: Boolean, val context: Context, callback: (TokenResponse?) -> (Unit))
    : AsyncTask<String, Int, TokenResponse>() {

    private var callback: ((TokenResponse?) -> (Unit))? = null
    private var refresh = false

    init {
        setCallback(callback)
        this.refresh = refresh
    }

    internal fun setCallback(callback: (TokenResponse?) -> (Unit)) {
        this.callback = callback
    }

    override fun onPreExecute() {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo?.isConnected == false
            || networkInfo?.type != ConnectivityManager.TYPE_WIFI
            && networkInfo?.type != ConnectivityManager.TYPE_MOBILE) {
            callback?.invoke(null)
            cancel(true)
        }
    }

    override fun doInBackground(vararg userToken: String): TokenResponse {
        return if (refresh) {
            GoogleRefreshTokenRequest(
                NetHttpTransport(),
                JacksonFactory.getDefaultInstance(),
                userToken[0],
                Keys.CLIENT_ID,
                Keys.CLIENT_SECRET
            ).execute()
        } else {
            val requestUrl = GoogleAuthorizationCodeRequestUrl(
                "https://oauth2.googleapis.com/token",
                Keys.CLIENT_ID, "",
                listOf(BooksScopes.BOOKS))
                .setApprovalPrompt("force")
                .setAccessType("offline")
                .build()
            GoogleAuthorizationCodeTokenRequest(
                NetHttpTransport(),
                JacksonFactory.getDefaultInstance(),
                requestUrl,
                Keys.CLIENT_ID,
                Keys.CLIENT_SECRET,
                userToken[0],
                ""
            ).execute()
        }
    }

    override fun onPostExecute(result: TokenResponse) {
        callback?.invoke(result)
    }
}