package com.example.mybooksthoughts

import android.net.ConnectivityManager
import android.net.Network

class NetworkCallbackImp : ConnectivityManager.NetworkCallback() {

    override fun onAvailable(network: Network) {
        NetworkState.isConnected = true
    }

    override fun onLost(network: Network) {
        NetworkState.isConnected = false
    }
}