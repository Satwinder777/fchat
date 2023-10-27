package com.example.myfirebaseprojectwithdb.fcm.model

data class FCMRequest(val to: String, val data: Map<String, String>)
data class FCMResponse(val success: Int, val failure: Int)
