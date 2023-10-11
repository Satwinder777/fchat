package com.example.myfirebaseprojectwithdb.activity.profile.chatroom

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myfirebaseprojectwithdb.LoggedUserDetails
import com.example.myfirebaseprojectwithdb.R
import com.example.myfirebaseprojectwithdb.activity.profile.chatroom.adapters.ChatRcAdapter
import com.example.myfirebaseprojectwithdb.databinding.ActivityChatBinding
import com.example.myfirebaseprojectwithdb.myfireobj.db
import com.google.gson.Gson
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity() {
    lateinit var binding :ActivityChatBinding
    lateinit var chatrc:RecyclerView
    var adapter: ChatRcAdapter?=null

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
        arr = arrayListOf<chatMessage>()

        Log.e(TAG, "initData: $receiveruid", )
        binding.morebtn.setOnClickListener {
            Toast.makeText(this, "under development!!", Toast.LENGTH_SHORT).show()
        }
        binding.sendbtn.setOnClickListener {
            sendMsg(senderuid.toString(),receiveruid.toString(),binding.editmsg.text.toString())
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

        if (receiveruid != null) {
            getAllMsg(receiveruid)
        }

        handleChatListner(receiveruid!!)
    }


    fun sendMsg(senderUID: String,receiverUID: String,msg: String){
        var chatInst =  chatMessage(senderUID, receiverUID, msg,Date().time)
        db.collection("chatRooms")
            .document(receiverUID)
            .collection("messages")
            .add(chatInst)
            .addOnSuccessListener{
                Toast.makeText(this, "data updated!!", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "sendMsg: ${it.id}", )
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }

        Log.e("checkCurrentTime", "sendMsg: ${Date().time}", )

    }

    private fun handleChatListner(receiverUID: String){
        db.collection("chatRooms")
            .document(receiverUID)
            .collection("messages")
            .addSnapshotListener { value, error ->

                if (error==null){
                    value?.forEach {
                        var json = Gson().toJson(it.data)
                       var data =  Gson().fromJson(json,chatMessage::class.java)
                        Log.e("QueryDocumentSnapShot", "handleChatListner: $data", )
                    }
//                 var arr:kotlin.collections.ArrayList<chatMessage> = arr.sortedBy { it.time } as ArrayList
                    val arr1 = arr.sortedBy { it.time }.toMutableList()

                    if (adapter==null){
                        adapter = ChatRcAdapter(arr1)
                        adapter!!.addData(arr1)
                    }else{
                        adapter!!.clearData()
                        adapter!!.addData(arr1)
                    }

//                    chatrc.adapter = adapter
                    adapter?.notifyDataSetChanged()
                }else{
                    Toast.makeText(this, "$error", Toast.LENGTH_SHORT).show()
                }
                Log.e("HandleChatListner", "handleChatListner: $error>>$value", )
            }
    }

    fun getAllMsg(receiverid:String){

//fun getAllMsg(receiverid:String):ArrayList<chatMessage>{

        db.collection("chatRooms")
            .document(receiverid)
            .collection("messages").get().addOnCompleteListener {
                    querySnap->
//                var list  = mutableListOf<chatMessage>()
                var chatins = chatMessage("","","")

                querySnap.result.documents.forEach {
                    it.data?.entries?.forEach {
                        Log.e("checkListdata", "getAllMsg: ${it.key},value==${it.value}")

                        when (it?.key){
                            "msg"->{chatins.msg = it.value.toString()}
                            "receiverUID"->{ chatins.receiverUID = it.value.toString()}
                            "senderUID"->{ chatins.senderUID = it.value.toString()}
                            else->{
                                Log.e("testdata1", "getAllMsg: ${it.key}")
                            }
                        }
                    }
                    var myData = LoggedUserDetails.gson.toJson(it.data)
                    var mdata =  LoggedUserDetails.gson.fromJson(myData, chatMessage::class.java)
//                    listUser.add(mdata)
                    arr.add(mdata)
                    Log.e("zxcvbnm", "getAllMsg: $mdata", )
//                    arr.addAll(list)

                }
                adapter = ChatRcAdapter(arr)
                chatrc.adapter = adapter
//                adapter.notifyDataSetChanged()
                Log.e("chatList", "getAllMsg: $arr", )


            }.addOnFailureListener {
                Log.e("checkListdata", "getAllMsg: $it", )
            }

    }

}


data class chatMessage(
    var senderUID:String,
    var receiverUID:String,
    var msg :String,
    var time :Long = Date().time
)

