package com.example.myfirebaseprojectwithdb.fcm.model

data class MessageRequest(
    val notification: Notification,
    val to: String
)