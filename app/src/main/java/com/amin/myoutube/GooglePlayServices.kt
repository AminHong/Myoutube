package com.amin.myoutube

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import com.amin.myoutube.MainActivity.Companion.REQUEST_GOOGLE_PLAY_SERVICES
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

fun isGooglePlayServicesAvailable(): Boolean{
    val connectionStatusCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(AppController.getInstance())
    return connectionStatusCode == ConnectionResult.SUCCESS
}
fun acquireGooglePlayServices(activity: Activity){
    val connectionStatusCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(AppController.getInstance())
    if (GoogleApiAvailability.getInstance().isUserResolvableError(connectionStatusCode)) {
        showGooglePlayServicesAvailabilityErrorDialog(activity, connectionStatusCode)
    }
}

fun showGooglePlayServicesAvailabilityErrorDialog(activity: Activity, connectionStatusCode: Int) {
    val dialog = GoogleApiAvailability.getInstance().getErrorDialog(
            activity,
            connectionStatusCode,
            REQUEST_GOOGLE_PLAY_SERVICES)
    dialog.show()
}

fun isDeviceOnline(): Boolean {
    val connMgr = AppController.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connMgr.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnected
}