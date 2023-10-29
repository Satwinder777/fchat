package com.example.myfirebaseprojectwithdb.activity.profile.chatroom

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myfirebaseprojectwithdb.LoggedUserDetails
import com.example.myfirebaseprojectwithdb.R
import com.example.myfirebaseprojectwithdb.activity.profile.chatroom.adapters.ChatRcAdapter
import com.example.myfirebaseprojectwithdb.databinding.ActivityChatBinding
import com.example.myfirebaseprojectwithdb.fcm.model.FCMRequest
import com.example.myfirebaseprojectwithdb.fcm.model.FCMResponse
import com.example.myfirebaseprojectwithdb.fcm.model.MessageRequest
import com.example.myfirebaseprojectwithdb.fcm.model.Notification
import com.example.myfirebaseprojectwithdb.myfireobj.db
import com.example.myfirebaseprojectwithdb.retrofit.RetroIns
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date

import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity() {
    lateinit var binding :ActivityChatBinding
    lateinit var chatrc:RecyclerView
    lateinit var chatRoomId:String
    var adapter: ChatRcAdapter?=null
    var sender_name = "chat_user"
    lateinit var arr :ArrayList<chatMessage>
    companion object{
        val TAG = "ChatActivity12"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initData()
    }
    fun initData(){
        var receivername = intent.getStringExtra("name")
        var receiverimage = intent.getStringExtra("image")
        var receiveruid = intent.getStringExtra("userId")
        var senderuid = intent.getStringExtra("senderUid")
        var fcm_token = intent.getStringExtra("fcm_token")
        sender_name = intent.getStringExtra("sender_name").toString()
//        intent.putExtra("sender_name",sname)
        arr = arrayListOf<chatMessage>()
        chatRoomId  = getChatRoomId(senderuid!!,receiveruid!!)
        Log.e(TAG, "initData: $receiveruid", )
        binding.morebtn.setOnClickListener {
            Toast.makeText(this, "under development!!", Toast.LENGTH_SHORT).show()
        }
        binding.sendbtn.setOnClickListener {
            sendMsg(senderuid.toString(),receiveruid.toString(),binding.editmsg.text.toString(),fcm_token?:"sattanull")
            binding.editmsg.text.clear()
//            Toast.makeText(this, "under development!!", Toast.LENGTH_SHORT).show()
        }

        binding.backbtn.setOnClickListener { onBackPressed() }
        Glide.with(this)
            .load(receiverimage) // URL of the image to load
            .placeholder(R.drawable.profile_img) // Placeholder image resource
            .error(R.drawable.error_ic) // Error image resource (optional)
            .into(binding.senderProfile)
        binding.senderName.setText(receivername.toString())
        chatrc = binding.chatrc
        chatrc.scrollToPosition(0)

//        if (receiveruid != null) {
//            getAllMsg(receiveruid)
//        }

        handleChatListner(receiveruid!!)
    }


    fun sendMsg(senderUID: String,receiverUID: String,msg: String,fcm_token:String){
        var chatInst =  chatMessage(senderUID, receiverUID, msg, Date().time)
        db.collection("chatRooms")
            .document(chatRoomId)
            .collection("messages")
            .add(chatInst)
            .addOnSuccessListener{
//                Toast.makeText(this, "data updated!!", Toast.LENGTH_SHORT).show()
//                Log.e(TAG, "sendMsg: ${it.id}", )

//                var map = hashMapOf<String,String>()
//                map["title"] = "satwinder"
//                map["body"] = "satwindershergill"
//                map["subtitle"] = "satwindershergill_subtitle"
//                val message = RemoteMessage.Builder(fcm_token)
//                    .setData(mapOf("msg" to "dsd")) // Add your message content
//                    .build()
//                FirebaseMessaging.getInstance().send(message)
               var msg =  MessageRequest(to = fcm_token, notification = Notification(body = msg, subtitle = "satwinder",sender_name))
                Log.e("myfcm", "sendMsg: $fcm_token", )
//                val message = FCMRequest(fcm_token, map)

                RetroIns.fcmservice.sendNotification(msg)
                    .enqueue(object : Callback<FCMResponse> {
                        override fun onResponse(call: Call<FCMResponse>, response: Response<FCMResponse>) {
                            if (response.isSuccessful) {
                                val fcmResponse = response.body()
                                Log.e("testcase1212", "onSuccess: ${fcmResponse}", )
                                if (fcmResponse?.success==1){
                                    Toast.makeText(this@ChatActivity, "msg send !", Toast.LENGTH_SHORT).show()
                                }else{
                                    Toast.makeText(this@ChatActivity, "msg not sended!! !", Toast.LENGTH_SHORT).show()

                                }

                            } else {
                                Log.e("testcase1212", "onSuccess: not success>>${response.message()}", )

                            }
                        }

                        override fun onFailure(call: Call<FCMResponse>, t: Throwable) {
//                Toast.makeText(, "${t.message}", Toast.LENGTH_SHORT).show()
                            Log.e("testcase1212", "onFailure: ${t.message}", )
                        }

                    })
//                FirebaseMessaging.getInstance().send()
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }

        Log.e("checkCurrentTime", "sendMsg: ${Date().time}", )

    }
    fun getChatRoomId(userid1:String,userid2:String):String{
        return if(userid1.hashCode()<userid2.hashCode()){
            userid1+"_"+userid2
        }else{
            userid2+"_"+userid1
        }
    }
    fun checkGroup(){

    }


    private fun handleChatListner(receiverUID: String){
        db.collection("chatRooms")
            .document(chatRoomId)
            .collection("messages")
            .addSnapshotListener { value, error ->

                if (error==null){
                    arr.clear()
                    value?.forEach {
                        var json = Gson().toJson(it.data)
                       var data =  Gson().fromJson(json,chatMessage::class.java)
                        Log.e("QueryDocumentSnapShot", "handleChatListner: $data", )
                        arr.add(data)
                    }
//                 var arr:kotlin.collections.ArrayList<chatMessage> = arr.sortedBy { it.time } as ArrayList
                    val arr1 = arr.sortedBy { it.time }.toMutableList()


                    Log.e("freshChat11", "addData: $arr1", )

                    if (adapter==null){
                        adapter = ChatRcAdapter(arr1,this,receiverUID)
                        chatrc.adapter = adapter

//                        adapter!!.addData(arr1)
                    }else{
                        adapter!!.clearData()
                        adapter!!.addData(arr1)
                        adapter?.notifyDataSetChanged()
                    }

//                    chatrc.adapter = adapter
                    adapter?.notifyDataSetChanged()
                }else{
                    Toast.makeText(this, "$error", Toast.LENGTH_SHORT).show()
                }
                Log.e("HandleChatListner", "handleChatListner: $error>>$value", )
            }
    }

//    fun getAllMsg(receiverid:String){
//
////fun getAllMsg(receiverid:String):ArrayList<chatMessage>{
//
//        db.collection("chatRooms")
//            .document(receiverid)
//            .collection("messages").get().addOnCompleteListener {
//                    querySnap->
////                var list  = mutableListOf<chatMessage>()
//                var chatins = chatMessage("","","")
//
//                querySnap.result.documents.forEach {
//                    it.data?.entries?.forEach {
//                        Log.e("checkListdata", "getAllMsg: ${it.key},value==${it.value}")
//
//                        when (it?.key){
//                            "msg"->{chatins.msg = it.value.toString()}
//                            "receiverUID"->{ chatins.toMsg = it.value.toString()}
//                            "senderUID"->{ chatins.fromMsg = it.value.toString()}
//                            else->{
//                                Log.e("testdata1", "getAllMsg: ${it.key}")
//                            }
//                        }
//                    }
//                    var myData = LoggedUserDetails.gson.toJson(it.data)
//                    var mdata =  LoggedUserDetails.gson.fromJson(myData, chatMessage::class.java)
////                    listUser.add(mdata)
//                    arr.add(mdata)
//                    Log.e("zxcvbnm", "getAllMsg: $mdata", )
////                    arr.addAll(list)
//
//                }
//                adapter = ChatRcAdapter(arr,this,receiverid)
//                chatrc.adapter = adapter
////                adapter.notifyDataSetChanged()
//                Log.e("chatList", "getAllMsg: $arr", )
//
//
//            }.addOnFailureListener {
//                Log.e("checkListdata", "getAllMsg: $it", )
//            }
//
//    }

}


data class chatMessage(
    var fromMsg:String,
    var toMsg:String,
    var msg :String,
    var time :Long = Date().time
)

