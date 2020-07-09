package com.example.mybooksthoughts

import android.content.Context
import android.net.ConnectivityManager
import android.os.AsyncTask
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest

class DownloadTask<T>(val context: Context, callback: DownloadCallback<T>)
    : AsyncTask<AbstractGoogleClientRequest<T>, Int, T>() {

    private var callback: DownloadCallback<T>? = null

    init {
        setCallback(callback)
    }

    internal fun setCallback(callback: DownloadCallback<T>) {
        this.callback = callback
    }

    override fun onPreExecute() {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo?.isConnected == false
            || networkInfo?.type != ConnectivityManager.TYPE_WIFI
            && networkInfo?.type != ConnectivityManager.TYPE_MOBILE) {
            callback?.updateFromDownload(null)
            cancel(true)
        }
    }

    override fun doInBackground(vararg requests: AbstractGoogleClientRequest<T>): T? {
        var result: T? = null
        if (!isCancelled) {
            result = requests[0].execute()
        }
        return result
    }

    override fun onPostExecute(result: T?) {
        callback?.apply {
            updateFromDownload(result)
        }
    }
}

interface DownloadCallback<T> {
    fun updateFromDownload(result: T?)
}