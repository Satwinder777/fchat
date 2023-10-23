package com.example.myfirebaseprojectwithdb.application

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.myfirebaseprojectwithdb.myfireobj
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging

class MyApplication :Application() {

    companion object{
        var sharedPref:SharedPreferences? = null
    }
    override fun onCreate() {
        super.onCreate()
        firebaseAppInit(this)
        sharedPref = getSharedPreferences("my_shared_pref", Context.MODE_PRIVATE)

    }
    fun firebaseAppInit(context: Context){
        if (FirebaseApp.getApps(context).isEmpty()){
            val options = FirebaseOptions.Builder()
                .setApiKey(myfireobj.firebaseKey)
                .setApplicationId(myfireobj.firebaseProjectId)
                .setProjectId(myfireobj.firebaseProjectId)
                .build()
            FirebaseApp.initializeApp(context, options)
        }

        FirebaseMessaging.getInstance().isAutoInitEnabled = true


    }

}