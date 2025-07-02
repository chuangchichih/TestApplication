package com.example.testapplication.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class TestService : Service() {

    override fun onCreate() {
        super.onCreate()
        // Initialization logic when the service is created
        Log.d("TestService", "Service created")
        createNotificationChannel()
    }

    override fun onBind(intent: Intent): IBinder? {
        // Return null if the service is not meant to be bound
        // If you want to bind the service, return an IBinder implementation
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Logic to handle when the service is started
        Log.d("TestService", "Service started")
        // Start as a foreground service using NotificationCompat for compatibility
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Test Service")
            .setContentText("TestService is running in the foreground")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()
        startForeground(NOTIFICATION_ID, notification)
        // You can process the intent here if needed
        return START_STICKY // Ensures the service is restarted if killed
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cleanup logic when the service is destroyed
        Log.d("TestService", "Service destroyed")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Test Service Channel",
                NotificationManager.IMPORTANCE_MIN
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "TestServiceChannel"
        private const val NOTIFICATION_ID = 1
    }
}