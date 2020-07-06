package com.example.mybooksthoughts

import android.os.AsyncTask
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest

class DownloadTask<T>(callback: DownloadCallback<T>)
    : AsyncTask<AbstractGoogleClientRequest<T>, Int, T>() {

    private var callback: DownloadCallback<T>? = null

    init {
        setCallback(callback)
    }

    internal fun setCallback(callback: DownloadCallback<T>) {
        this.callback = callback
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