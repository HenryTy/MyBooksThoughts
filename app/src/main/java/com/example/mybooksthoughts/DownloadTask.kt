package com.example.mybooksthoughts

import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.AsyncTask
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest

/**
 * Implementation of AsyncTask designed to fetch data from the network.
 */
class DownloadTask<T>(callback: DownloadCallback<T>)
    : AsyncTask<AbstractGoogleClientRequest<T>, Int, T>() {

    private var callback: DownloadCallback<T>? = null

    init {
        setCallback(callback)
    }

    internal fun setCallback(callback: DownloadCallback<T>) {
        this.callback = callback
    }

    /**
     * Cancel background network operation if we do not have network connectivity.
     */
    override fun onPreExecute() {
        if (callback != null) {
            val networkInfo = callback?.getActiveNetworkInfo()
            if (networkInfo?.isConnected == false
                || networkInfo?.type != ConnectivityManager.TYPE_WIFI
                && networkInfo?.type != ConnectivityManager.TYPE_MOBILE) {
                // If no connectivity, cancel task and update Callback with null data.
                callback?.updateFromDownload(null)
                cancel(true)
            }
        }
    }

    /**
     * Defines work to perform on the background thread.
     */
    override fun doInBackground(vararg requests: AbstractGoogleClientRequest<T>): T? {
        var result: T? = null
        if (!isCancelled) {
            result = requests[0].execute()
        }
        return result
    }

    /**
     * Updates the DownloadCallback with the result.
     */
    override fun onPostExecute(result: T?) {
        callback?.apply {
            updateFromDownload(result)
        }
    }
}

interface DownloadCallback<T> {
    fun updateFromDownload(result: T?)
    fun getActiveNetworkInfo(): NetworkInfo
}