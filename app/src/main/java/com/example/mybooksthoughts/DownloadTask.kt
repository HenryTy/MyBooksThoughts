package com.example.mybooksthoughts

import android.content.Context
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
        if (!NetworkState.isConnected) {
            callback?.onNoConnection()
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
    fun onNoConnection()
}