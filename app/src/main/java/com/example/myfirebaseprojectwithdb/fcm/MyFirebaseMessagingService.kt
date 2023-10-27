package com.example.myfirebaseprojectwithdb.fcm

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.myfirebaseprojectwithdb.MainActivity
import com.example.myfirebaseprojectwithdb.R
import com.example.myfirebaseprojectwithdb.User
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

val channelId = "SHER_GILL_4983"
val channelNAme = "SHER_GILL_Satta"
class MyFirebaseMessagingService : FirebaseMessagingService() {

//    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Handle the FCM message here
        // You can extract data and display a notification
        val notificationData = remoteMessage.notification
        val title = notificationData?.title!!
        val message = notificationData?.body!!
//        showNotification(this,title?:"satta", message?:"shergill")

        myFunction(title,message)


        Log.e("testcase1212", "onMessageReceived: call testcase msg>> ", )


//        checkNotificationPermission(this)

//        Log.e(TAG, "onMessageReceived: call", )
    }

    @SuppressLint("RemoteViewLayout")
    private fun remoteView(title:String,body:String):RemoteViews {
            var rv = RemoteViews("com.example.myfirebaseprojectwithdb",R.layout.notification_m)
        rv.setTextViewText(R.id.titlemsg,title)
        rv.setTextViewText(R.id.titlemsgbody,body)
        rv.setImageViewResource(R.id.msgImg,R.drawable.profile_img)
        return rv
    }

    private fun showNotification(context: Context, title: String, message: String) {
        // Define a unique notification channel ID for Android 8.0 (Oreo) and higher


        var builder:NotificationCompat.Builder
        = NotificationCompat.Builder(applicationContext,channelId)
            .setSmallIcon(R.drawable.profile_img)
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000,1000,1000,1000))

        builder.setContent(remoteView(title,message))
//       nBuilder.contentView
       var nManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
           val channel = NotificationChannel(channelId, channelNAme,NotificationManager.IMPORTANCE_HIGH)
            nManager.createNotificationChannel(channel)
        }else{
            nManager.notify(0,builder.build())
        }
//        val nManager = NotificationManagerCompat.from(this)
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.POST_NOTIFICATIONS
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//
//            nManager.notify(1, nBuilder)
//            return
//        }else{
//            checkNotificationPermission(this)
//        }



        // Create an Intent to launch when the user taps the notification
//        val intent = Intent(context, MainActivity::class.java)
//        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        // Create the notification
//        val builder = NotificationCompat.Builder(context, channelId)
//            .setSmallIcon(R.drawable.profile_img) // Set the small icon for the notification
//            .setContentTitle(title) // Set the notification title
//            .setContentText(message) // Set the notification message
////            .setContentIntent(pendingIntent) // Set the intent to execute when the notification is tapped
//            .setAutoCancel(true) // Dismiss the notification when tapped
//
//        // Get the NotificationManager service
//        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        // For Android 8.0 (Oreo) and higher, create a notification channel
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(channelId, "Sher gill production", NotificationManager.IMPORTANCE_DEFAULT)
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        // Show the notification
//        notificationManager.notify(0, builder.build())
    }


    override fun onNewToken(token: String) {
        Log.e("TOKEN123", "onNewToken:>> $token")
    }
    fun showNotificationPermissionDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Notification Permission")
            .setMessage("To receive notifications, please grant permission in the app settings.")
            .setPositiveButton("Open Settings") { _, _ ->
                // Open the app settings so the user can grant notification permission
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                context.startActivity(intent)
            }
            .setNegativeButton("Cancel") { a,b->
                // Handle the user's decision to not grant notification permission
                // You can provide appropriate feedback or take other actions here
                a.dismiss()

            }
            .show()
    }
    fun checkNotificationPermission(context: Context) {
        val notificationManager = NotificationManagerCompat.from(context)

        if (!notificationManager.areNotificationsEnabled()) {
            // Notification permission is not granted, so show a dialog to request it
            showNotificationPermissionDialog(context)
        } else {
            // Notification permission is already granted
            // You can perform any actions that require notification permission here
            showNotification(this,"customtitle","custommessage")
//            Toast.makeText(this, "notification permission already granted", Toast.LENGTH_SHORT).show()
        }
    }

//@RequiresApi(Build.VERSION_CODES.O)
fun myFunction(title: String,body: String){
    // Create a notification channel
//    val notificationChannel = NotificationChannel(
//            channelId,
//        channelNAme,
//        NotificationManager.IMPORTANCE_DEFAULT
//    )
//    notificationChannel.description = "Channel Description new"
//
//// Register the channel with the system
//    val notificationManager = getSystemService(NotificationManager::class.java)
//    notificationManager.createNotificationChannel(notificationChannel)

//    val builder = NotificationCompat.Builder(this, channelId)
//        .setSmallIcon(R.drawable.profile_img)
//        .setContentTitle(title)
//        .setContentText(body)
//        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    val builder = NotificationCompat.Builder(this, channelId)
        .setSmallIcon(R.drawable.profile_img) // Set your notification icon
        .setContentTitle(title)
        .setContentText(body)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)
//        .setCustomBigContentView(remoteView())

    val notificationManager1 = NotificationManagerCompat.from(this)
    if (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        Toast.makeText(this, "permission not granted!!", Toast.LENGTH_SHORT).show()
        return
    }else{
        notificationManager1.notify(1, builder.build())
    }


}

}