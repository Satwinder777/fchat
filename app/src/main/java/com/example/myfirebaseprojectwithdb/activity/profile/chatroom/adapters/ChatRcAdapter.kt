package com.example.myfirebaseprojectwithdb.activity.profile.chatroom.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirebaseprojectwithdb.R
import com.example.myfirebaseprojectwithdb.activity.profile.chatroom.chatMessage
import com.example.myfirebaseprojectwithdb.databinding.ReceiveTextBinding
import com.example.myfirebaseprojectwithdb.databinding.SentItemBinding

class ChatRcAdapter(var arr :ArrayList<chatMessage>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var SENT_ITEM = 0
    var RECEIVE_ITEM = 1

    var ReceiverUID = ""
    var SenderUID = ""

    override fun getItemViewType(position: Int): Int {
//        return super.getItemViewType(position)
        return if (ReceiverUID==arr.get(position).receiverUID){
            RECEIVE_ITEM
        }
        else{
            SENT_ITEM
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