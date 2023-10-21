package com.example.myfirebaseprojectwithdb.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.myfirebaseprojectwithdb.R
import com.example.myfirebaseprojectwithdb.User
import com.example.myfirebaseprojectwithdb.activity.profile.chatroom.adapters.ChatRcAdapter
import com.example.myfirebaseprojectwithdb.databinding.MchatroomBinding
import com.example.myfirebaseprojectwithdb.databinding.UserItemRcBinding
import com.google.android.material.button.MaterialButton

class LoggedUserRcAdapter(var listUser:MutableList<Any>?,val onclickItem:onItemClick): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val GroupChatView = 1
    val UserView = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var user_view =  LayoutInflater.from(parent.context).inflate(R.layout.user_item_rc,parent,false)
        var groupchat =  LayoutInflater.from(parent.context).inflate(R.layout.mchatroom,parent,false)

//        return MyListClass(view)
        return if (viewType == UserView){
            UserViewHolder(user_view)
        }else{
            ChatGroupViewHolder(groupchat)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        var mItem = listUser?.get(position)
        if(holder.itemViewType == UserView){
            var viewHolder = holder as UserViewHolder
           val pview =  viewHolder.bind
            pview.deletebtn.setOnClickListener { onclickItem.deleteUser(mItem as User) }
            pview.updatebtn.setOnClickListener { onclickItem.update(mItem as User) }
            holder.itemView.setOnClickListener { onclickItem.itemCliked(mItem as User) }
            var m_item = mItem as User
            pview.userEmail.setText(m_item.email.toString())
            pview.userName.setText(m_item.firstName+" "+m_item.lastName)
            pview.userPassword.setText(m_item.password.toString())

            val requestOptions = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache the image
                .placeholder(R.drawable.baseline_account_circle_24) // Placeholder image while loading (optional)



            Glide.with(holder.itemView.context)
                .load(m_item.img)
                .apply(requestOptions)
                .into(pview.profileItem)
        }
        else{
            var viewHolder = holder as ChatGroupViewHolder
            val pview = viewHolder.bind
            var m_item = mItem as GroupChatModel
            pview.grouptitle.setText(m_item.title)
            pview.lastmsg.setText(m_item.lastmsgby)
            var userItem :User

            holder.itemView.setOnClickListener { onclickItem.ongroupItemClicked() }


            pview.lastmsg
            val requestOptions = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache the image
                .placeholder(R.drawable.baseline_account_circle_24) // Placeholder image while loading (optional)

            Glide.with(holder.itemView.context)
                .load(m_item.profile)
                .apply(requestOptions)
                .into(pview.grpchatprofile)

        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(listUser?.get(position)  is User){
            UserView
        }else{
            GroupChatView
        }
    }

    override fun getItemCount(): Int {
        return listUser?.size?:0
    }

    fun addUsers(list: MutableList<User>){
        listUser?.clear()
        list.forEach {
            listUser?.add(it)
           var index = listUser?.indexOf(it)
            if (index != null) {
                notifyItemInserted(index)
            }

        }
        notifyDataSetChanged()
    }





    inner class UserViewHolder(view: View):RecyclerView.ViewHolder(view){
        var bind = UserItemRcBinding.bind(view)

    }
    inner class ChatGroupViewHolder(view:View):RecyclerView.ViewHolder(view){
        var bind = MchatroomBinding.bind(view)
    }


    interface onItemClick{
        fun update(data:User)
        fun deleteUser(data: User)
        fun itemCliked(user: User)

        fun ongroupItemClicked()
    }
}
data class GroupChatModel(
    val title :String = "",
    val profile :String = "",
    val lastmsgby :String = ""
)











/*
*   class MyListClass(view: View): RecyclerView.ViewHolder(view) {
        var profileImage = view.findViewById<ImageView>(R.id.profileItem)
        var name = view.findViewById<TextView>(R.id.userName)
        var email = view.findViewById<TextView>(R.id.userEmail)
        var password = view.findViewById<TextView>(R.id.userPassword)
        var updatebtn = view.findViewById<MaterialButton>(R.id.updatebtn)
        var deletebtn = view.findViewById<MaterialButton>(R.id.deletebtn)
        @SuppressLint("SetTextI18n")
        fun bind(item : User){
//            profileImage.setImageURI(item.img)

            val requestOptions = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache the image
                .placeholder(R.drawable.baseline_account_circle_24) // Placeholder image while loading (optional)

            // Load the image using Glide
            Log.e("adaptertag", "bind: ${item.img}", )
            Glide.with(itemView.context)
                .load(item.img)
                .apply(requestOptions)
                .into(profileImage)
//            profileImage.setImageURI(Uri.parse(item.img))
            name.setText("${item.firstName } ${item.lastName }")
            email.setText(item.email)
            password.setText(item.password)
        }
    }

    fun setData(data:MutableList<User>){
        listUser?.addAll(data)
        notifyDataSetChanged()
    }
    fun setImg(uri: Uri,position: Int){
        listUser?.get(position)?.img = uri.toString()

        notifyDataSetChanged()
    }
* */

