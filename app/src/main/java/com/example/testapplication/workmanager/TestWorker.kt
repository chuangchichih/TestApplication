package com.example.testapplication.workmanager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.ContentObserver
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Handler
import android.os.BatteryManager
import android.os.Looper
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay

class TestWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    private var isCharging: Boolean = false
    private var networkType: String = "NONE"

    private val chargingStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val status: Int = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
            isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL
            Log.e(TAG, "Device is charging: $isCharging (from receiver)")
        }
    }

    private val networkStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val connectivityManager =
                context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            networkType = if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WIFI"
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "CELLULAR"
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "ETHERNET"
                    else -> "UNKNOWN"
                }
            } else {
                "NONE"
            }
            Log.e(TAG, "Network type: $networkType (from receiver)")
        }
    }

    private val photoUriChangeListener = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean, uri: Uri?) {
            super.onChange(selfChange, uri)
            Log.e(TAG, "Photo URI changed: $uri")
        }
    }

    private val videoUriChangeListener = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean, uri: Uri?) {
            super.onChange(selfChange, uri)
            Log.e(TAG, "Video URI changed: $uri")
        }
    }


    override suspend fun doWork(): Result {
        try {
            Log.e(TAG, "Executing work")

            // Register the receiver
            val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            applicationContext.registerReceiver(chargingStateReceiver, intentFilter)

            // Register network state receiver
            val networkIntentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            applicationContext.registerReceiver(networkStateReceiver, networkIntentFilter)

            // Register URI change listeners
            val photoUri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            applicationContext.contentResolver.registerContentObserver(
                photoUri, true, photoUriChangeListener
            )
            Log.d(TAG, "Photo URI change listener registered")
            val videoUri = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            applicationContext.contentResolver.registerContentObserver(
                videoUri, true, videoUriChangeListener
            )
            Log.d(TAG, "Video URI change listener registered")

            delay(30 * 1000)
            Log.e(TAG, "Work finished. Charging: $isCharging, Network Type: $networkType")
        } catch (e: Exception) {
            Log.e(TAG, "Error executing work ", e)
            return Result.failure() // It's good practice to return failure on error
        } finally {
            // Unregister the receiver
            applicationContext.unregisterReceiver(chargingStateReceiver)
            Log.d(TAG, "Charging state listener unregistered")
            applicationContext.unregisterReceiver(networkStateReceiver)
            Log.d(TAG, "Network state listener unregistered")
            applicationContext.contentResolver.unregisterContentObserver(photoUriChangeListener)
            Log.d(TAG, "Photo URI change listener unregistered")
            applicationContext.contentResolver.unregisterContentObserver(videoUriChangeListener)
            Log.d(TAG, "Video URI change listener unregistered")
        }

        return Result.success()
    }

    companion object {
        private const val TAG = "TestWorker"
    }
}