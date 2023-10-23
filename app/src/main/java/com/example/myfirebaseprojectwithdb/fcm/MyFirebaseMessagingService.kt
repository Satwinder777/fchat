package com.example.myfirebaseprojectwithdb.fcm

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.myfirebaseprojectwithdb.MainActivity
import com.example.myfirebaseprojectwithdb.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    @SuppressLint("MissingPermission")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Handle the FCM message here
        // You can extract data and display a notification
        val notificationData = remoteMessage.notification
        val title = notificationData?.title
        val message = notificationData?.body

        // Show a notification
        showNotification(this,title?:"satta", message?:"shergill")
    }

    private fun showNotification(context: Context, title: String, message: String) {
        // Define a unique notification channel ID for Android 8.0 (Oreo) and higher
        val channelId = "SHER_GILL_4983"

        // Create an Intent to launch when the user taps the notification
//        val intent = Intent(context, MainActivity::class.java)
//        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        // Create the notification
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.profile_img) // Set the small icon for the notification
            .setContentTitle(title) // Set the notification title
            .setContentText(message) // Set the notification message
//            .setContentIntent(pendingIntent) // Set the intent to execute when the notification is tapped
            .setAutoCancel(true) // Dismiss the notification when tapped

        // Get the NotificationManager service
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // For Android 8.0 (Oreo) and higher, create a notification channel
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Sher gill production", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        // Show the notification
        notificationManager.notify(0, builder.build())
    }


    override fun onNewToken(token: String) {
        Log.e("TOKEN123", "onNewToken:>> $token")
    }


}