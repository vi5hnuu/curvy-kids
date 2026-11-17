package com.vi5hnu.curvykids.net

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Whether the device can reach the internet right now, kept up to date for as long as the app
 * runs. "Online" means a network the system has validated (it really reaches the internet),
 * not just a Wi-Fi with no connection behind it, since that is what a video needs.
 *
 * One instance for the app (see CurvyKidsApp); screens read [online] and never ask the system.
 */
class Connectivity(context: Context) {

    private val manager = context.applicationContext.getSystemService(ConnectivityManager::class.java)

    private val _online = MutableStateFlow(isOnlineNow())
    val online: StateFlow<Boolean> = _online.asStateFlow()

    init {
        manager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
            override fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) {
                _online.value = caps.reachesInternet()
            }

            override fun onLost(network: Network) {
                _online.value = false
            }
        })
    }

    private fun isOnlineNow(): Boolean =
        manager.getNetworkCapabilities(manager.activeNetwork)?.reachesInternet() == true

    private fun NetworkCapabilities.reachesInternet() =
        hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}
