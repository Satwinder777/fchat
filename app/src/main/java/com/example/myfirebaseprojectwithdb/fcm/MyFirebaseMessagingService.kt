package com.example.myfirebaseprojectwithdb.fcm

import android.Manifest
import android.R
import android.R.id
import android.R.id.message
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
//import com.example.myfirebaseprojectwithdb.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


val channelId = "SHER_GILL_4983"
val channelNAme = "SHER_GILL_Satta"
class MyFirebaseMessagingService : FirebaseMessagingService() {

//    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val notificationData = remoteMessage.notification
        val title = notificationData?.title?:"nulldata"
        val message = notificationData?.body?:"nullbody"

//        myFunction(title,message)
        Log.e("testcase1212", "onMessageReceived: call testcase msg>> ")
    createNotificationChannel(applicationContext,title,message)

    }

    @SuppressLint("RemoteViewLayout")
    private fun remoteView(title:String,body:String):RemoteViews {
            var rv = RemoteViews("com.example.myfirebaseprojectwithdb",
                com.example.myfirebaseprojectwithdb.R.layout.notification_m)
        rv.setTextViewText(com.example.myfirebaseprojectwithdb.R.id.titlemsg,title)
        rv.setTextViewText(com.example.myfirebaseprojectwithdb.R.id.titlemsgbody,body)
        rv.setImageViewResource(com.example.myfirebaseprojectwithdb.R.id.msgImg,
            com.example.myfirebaseprojectwithdb.R.drawable.profile_img)
        return rv
    }

    private fun showNotification(context: Context, title: String, message: String) {
        // Define a unique notification channel ID for Android 8.0 (Oreo) and higher


        var builder:NotificationCompat.Builder
        = NotificationCompat.Builder(applicationContext,channelId)
            .setSmallIcon(com.example.myfirebaseprojectwithdb.R.drawable.profile_img)
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

    }


    override fun onNewToken(token: String) {
        Log.e("TOKEN123", "onNewToken:>> $token")
    }
//    fun showNotificationPermissionDialog(context: Context) {
//        AlertDialog.Builder(context)
//            .setTitle("Notification Permission")
//            .setMessage("To receive notifications, please grant permission in the app settings.")
//            .setPositiveButton("Open Settings") { _, _ ->
//                // Open the app settings so the user can grant notification permission
//                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
//                intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
//                context.startActivity(intent)
//            }
//            .setNegativeButton("Cancel") { a,b->
//                // Handle the user's decision to not grant notification permission
//                // You can provide appropriate feedback or take other actions here
//                a.dismiss()
//
//            }
//            .show()
//    }
//    fun checkNotificationPermission(context: Context) {
//        val notificationManager = NotificationManagerCompat.from(context)
//
//        if (!notificationManager.areNotificationsEnabled()) {
//            // Notification permission is not granted, so show a dialog to request it
//            showNotificationPermissionDialog(context)
//        } else {
//            // Notification permission is already granted
//            // You can perform any actions that require notification permission here
//            showNotification(this,"customtitle","custommessage")
////            Toast.makeText(this, "notification permission already granted", Toast.LENGTH_SHORT).show()
//        }
//    }

//@RequiresApi(Build.VERSION_CODES.O)
fun myFunction(title: String,body: String){

    val builder = NotificationCompat.Builder(this, channelId)
        .setSmallIcon(com.example.myfirebaseprojectwithdb.R.drawable.profile_img) // Set your notification icon
        .setContentTitle(title)
        .setContentText(body)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)


    val notificationManager1 = NotificationManagerCompat.from(this)
    if (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        val intent = Intent()
        intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
        intent.putExtra("app_package", packageName)
        intent.putExtra("app_uid", applicationInfo.uid)
        startActivity(intent)
//        Toast.makeText(this, "permission not granted!!", Toast.LENGTH_SHORT).show()
        return
    }else{
        notificationManager1.notify(1, builder.build())
    }
}

}
private fun createNotificationChannel(context: Context,title: String,msg:String) {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name: CharSequence = channelNAme
        val description: String = "this is desc"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, name, importance)
        channel.description = description
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        var notificationManager = NotificationManagerCompat.from(context)
        notificationManager.createNotificationChannel(channel)


        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, channelId)
                .setSmallIcon(com.example.myfirebaseprojectwithdb.R.drawable.profile_img)
                .setContentText(msg)
                .setContentTitle(title)
                .setAutoCancel(true)
//            .setSound(defaultSoundUri)
//            .setContentIntent(pendingIntent)

//    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            notificationManager?.notify(1, notificationBuilder.build())

//            return
        }
        else{
            Toast.makeText(context, "permission not granted ", Toast.LENGTH_SHORT).show()
        }
    }


}