package com.example.myfirebaseprojectwithdb.activity.profile.chatroom

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myfirebaseprojectwithdb.R
import com.example.myfirebaseprojectwithdb.databinding.ActivityChatBinding
import com.example.myfirebaseprojectwithdb.myfireobj.db

class ChatActivity : AppCompatActivity() {
    lateinit var binding :ActivityChatBinding
    lateinit var chatrc:RecyclerView
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
        var sendername = intent.getStringExtra("name")
        var senderimage = intent.getStringExtra("image")
        var senderuid = intent.getStringExtra("userId")

        Log.e(TAG, "initData: $senderuid", )
        binding.morebtn.setOnClickListener {
            Toast.makeText(this, "under development!!", Toast.LENGTH_SHORT).show()
        }
        binding.sendbtn.setOnClickListener {
            sendMsg(senderuid.toString(),binding.editmsg.text.toString())
            Toast.makeText(this, "under development!!", Toast.LENGTH_SHORT).show()
        }

        binding.backbtn.setOnClickListener { onBackPressed() }
        Glide.with(this)
            .load(senderimage) // URL of the image to load
            .placeholder(R.drawable.profile_img) // Placeholder image resource
            .error(R.drawable.error_ic) // Error image resource (optional)
            .into(binding.senderProfile)
        binding.senderName.setText(sendername.toString())
        chatrc = binding.chatrc
    }


    fun sendMsg(uid:String,msg: String){
        var chatInst =  chatMessage(uid,msg,System.currentTimeMillis().toDouble().toString())
        db.collection("chatRooms")
            .document(uid)
            .collection("messages")
            .add(chatInst)
            .addOnSuccessListener{
                Toast.makeText(this, "data updated!!", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "sendMsg: ${it.id}", )
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }

    }
}

data class chatMessage(
    var senderCode:String,
    var msg :String,
    var timeStamp :String

)