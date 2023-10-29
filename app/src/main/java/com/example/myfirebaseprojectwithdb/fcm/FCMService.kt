package com.example.myfirebaseprojectwithdb.fcm

import com.example.myfirebaseprojectwithdb.fcm.model.FCMRequest
import com.example.myfirebaseprojectwithdb.fcm.model.FCMResponse
import com.example.myfirebaseprojectwithdb.fcm.model.MessageRequest
import com.example.myfirebaseprojectwithdb.retrofit.RetroIns
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

const val key = "AAAArPJjryQ:APA91bG3fRvaxNf5L48JkjsG1JforqUxVVIHxYV0Cb_eu95oG5zceSqFN_eBJwF61VFphaIwsq9FyTVWbqVcCAxPT3uraV2tbxiXhMiLV7FFBVh0Mvqv13Ig_Tka5DiM2YMaGzvqa7FY"
interface FCMService {
    @Headers("Authorization:key=$key", "Content-Type: application/json")
    @POST("fcm/send")
    fun sendNotification(@Body request: MessageRequest): Call<FCMResponse>
}