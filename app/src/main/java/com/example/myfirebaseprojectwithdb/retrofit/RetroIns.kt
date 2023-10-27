package com.example.myfirebaseprojectwithdb.retrofit

import android.util.Log
import android.widget.Toast
import com.example.myfirebaseprojectwithdb.fcm.FCMService
import com.example.myfirebaseprojectwithdb.fcm.model.FCMRequest
import com.example.myfirebaseprojectwithdb.fcm.model.FCMResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetroIns {

    val retrofit = Retrofit.Builder()
        .baseUrl("https://fcm.googleapis.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val fcmservice = retrofit.create(FCMService::class.java)
//    var serverKey = "AAAArPJjryQ:APA91bG3fRvaxNf5L48JkjsG1JforqUxVVIHxYV0Cb_eu95oG5zceSqFN_eBJwF61VFphaIwsq9FyTVWbqVcCAxPT3uraV2tbxiXhMiLV7FFBVh0Mvqv13Ig_Tka5DiM2YMaGzvqa7FY"



//    call1.enqueue(object : Callback<FCMResponse> {
//        override fun onResponse(call: Call<FCMResponse>, response: Response<FCMResponse>) {

//        }
//
//        override fun onFailure(call: Call<FCMResponse>, t: Throwable) {
//            // Handle the network failure
//        }
//    })

}