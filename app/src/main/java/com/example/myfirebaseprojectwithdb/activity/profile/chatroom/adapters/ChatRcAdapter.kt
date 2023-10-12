package com.example.myfirebaseprojectwithdb.activity.profile.chatroom.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirebaseprojectwithdb.R
import com.example.myfirebaseprojectwithdb.activity.profile.chatroom.chatMessage
import com.example.myfirebaseprojectwithdb.databinding.ReceiveTextBinding
import com.example.myfirebaseprojectwithdb.databinding.SentItemBinding
import com.google.firebase.auth.FirebaseAuth

class ChatRcAdapter(var arr :MutableList<chatMessage>,var context : Context,var chatroomId:String):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var SENT_ITEM = 0
    var RECEIVE_ITEM = 1


    var SenderUID = ""

    override fun getItemViewType(position: Int): Int {
//        return super.getItemViewType(position)
        return if (FirebaseAuth.getInstance().uid==arr.get(position).fromMsg){
            SENT_ITEM
        }
        else{
            RECEIVE_ITEM
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       return if ( viewType == SENT_ITEM ){
            var view = LayoutInflater.from(parent.context).inflate(R.layout.sent_item,parent,false)
            SentchatViewHolder(view)
        }
        else{
            var view = LayoutInflater.from(parent.context).inflate(R.layout.receive_text,parent,false)
            ReceivechatViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var mItem = arr.get(position)
        if(holder.itemViewType == SENT_ITEM){
            var viewHolder = holder as SentchatViewHolder
            viewHolder.binding.senttext.setText(mItem.msg)
        }
        else{
            var viewHolder = holder as ReceivechatViewHolder
            viewHolder.binding.receivetext.setText(mItem.msg)
        }
    }

    override fun getItemCount(): Int {
        return arr.size
    }

    fun clearData() {
        arr.clear()
        notifyDataSetChanged()
    }

    fun addData(newData: MutableList<chatMessage>) {

        if (newData.isNullOrEmpty()){
            Log.e("freshChat", "addData: ListEmpty>>$newData", )
        }else{
            clearData()
            notifyDataSetChanged()
            arr = newData
            Log.e("freshChat", "addData:11 $newData", )
            notifyDataSetChanged()
        }
//        var insertPosition = arr.size // The position where the new items will be inserted
//        newData.forEach {
//            arr.add(it)
//            notifyItemInserted(insertPosition) // Notify for each inserted item
//            insertPosition++
//            satttashergill code  end



        Toast.makeText(context, "data added", Toast.LENGTH_SHORT).show()

        // Do NOT call notifyDataSetChanged() here.
    }

    class SentchatViewHolder(view:View):RecyclerView.ViewHolder(view) {
        var binding = SentItemBinding.bind(view)
    }
    class ReceivechatViewHolder(view:View):RecyclerView.ViewHolder(view) {
        var binding = ReceiveTextBinding.bind(view)

    }



//    interface onclick{
//
//    }
}