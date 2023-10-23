package com.example.myfirebaseprojectwithdb

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent

import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirebaseprojectwithdb.adapter.LoggedUserRcAdapter
import com.example.myfirebaseprojectwithdb.databinding.ActivityLoggedUserDetailsBinding
import com.example.myfirebaseprojectwithdb.myfireobj.auth
import com.example.myfirebaseprojectwithdb.myfireobj.db
import com.example.myfirebaseprojectwithdb.myfireobj.storageRef
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File

import android.database.Cursor
import android.os.Parcel
import android.os.Parcelable

import android.provider.MediaStore
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import com.example.myfirebaseprojectwithdb.activity.profile.ProfileActivity
import com.example.myfirebaseprojectwithdb.activity.profile.chatroom.ChatActivity
import com.example.myfirebaseprojectwithdb.adapter.GroupChatModel
import com.example.myfirebaseprojectwithdb.application.MyApplication.Companion.sharedPref
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.play.integrity.internal.l
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName


class LoggedUserDetails : AppCompatActivity(),LoggedUserRcAdapter.onItemClick {
    private lateinit var binding : ActivityLoggedUserDetailsBinding
    var user:User?=null
    lateinit var rc : RecyclerView
    lateinit var adapter : LoggedUserRcAdapter
    lateinit var mGoogleSignInClient: GoogleSignInClient

    var userList : MutableList<User> = mutableListOf()


    companion object{
        val TAG = "LoggedUserDetails"
        var createLiveButton:MutableLiveData<Boolean> = MutableLiveData()
        var currentUserUID:String?=null
        val gson = Gson()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoggedUserDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        currentUserUID = sharedPref?.getString(phref.USER_UID.toString(),"")


         rc = binding.loggedUserRc
        reInitdata()
        adapter = LoggedUserRcAdapter(null,this)


        binding.logOutBtn.setOnClickListener {
            auth.signOut()
            var intent = Intent(this,MainActivity::class.java)
            intent.putExtra(navChain.LOGGED_USER_TO_MAIN.toString(),"")
            startActivity(intent)
            this.finish()
            sharedPref?.edit()?.clear()?.apply()
            logoutOperation()
        }
        createLiveButton.observe(this, Observer {
            if (it){
                binding.createuser.visibility = View.VISIBLE
            }else{
                binding.createuser.visibility = View.GONE
            }
        })
        binding.createuser.setOnClickListener {
            startActivityForResult(Intent(this,CustomSignUpActivity::class.java),code.createusercode)

        }
        binding.userProfileLogged.setOnClickListener {
            val intent = Intent(this,ProfileActivity::class.java)
            //add user data in the bundle
            val bundle = Bundle()
            if(user!=null){
//                bun
////                bundle.putString(phref.USER_UID.toString(), sharedPref?.getString(phref.PHREF_KEY.toString(),""))
//
////                intent.putExtra(bundle)
                intent.putExtra("fname", user!!.firstName)
                intent.putExtra("lname",user!!.lastName)
                intent.putExtra("img",user!!.img)
                intent.putExtra("email",user!!.email)
                intent.putExtra("uidcode",currentUserUID)
                var uuid = sharedPref?.getString(phref.PHREF_KEY.toString(),"")
                Log.e(TAG, "onCreate: intentddata uid>>$currentUserUID", )
                startActivity(intent)
            }else{
                Toast.makeText(this, "user null", Toast.LENGTH_SHORT).show()
                Log.e("greatcoder", "onCreate: $user", )
            }

        }
        HandleListner()
    }

    private fun reInitdata()
    {
        GlobalScope.launch {
            launch(Dispatchers.IO){
                user  = getCurrentUserDetails(currentUserUID.toString())
                getAllUser()
//                        Log.e("sherGillSatta112", "onCreate:user is >>$user >>list== $data", )
            }
        }

    }
    @SuppressLint("NotifyDataSetChanged")
    private suspend fun getAllUser():MutableList<Any>{
        val listUser:MutableList<Any> = mutableListOf()

        val qyuerSnap = db.collection("users")
            .get().await()

        qyuerSnap.documents.forEach {
            var myData = gson.toJson(it.data)
           var mdata =  gson.fromJson(myData,User::class.java)
            listUser.add(mdata)
            Log.e("mData123", "getAllUser: $mdata", )
//            dcdjcd

        }
        listUser?.removeIf {
            it as User
            it.userid==user?.userid

        }
        listUser.add(GroupChatModel("The Boys Group","https://wallpapers.com/images/featured/the-boys-1fe3hnl120ch1bc6.webp","under development"))


        GlobalScope.launch {
            try {
                if (listUser.isNullOrEmpty().not()){
                    adapter.listUser?.clear()
                    var adapter = LoggedUserRcAdapter(listUser,this@LoggedUserDetails)
                    launch(Dispatchers.Main){
                        rc.adapter = adapter
                    }
                    adapter.notifyDataSetChanged()
                    Log.e(TAG+21, "getAllUser: if block data>>$listUser")
                }else{
                    Log.e(TAG+21, "getAllUser: else data >>$listUser", )
                }
            }
            catch (e:Exception){
                Log.e(TAG, "getAllUser: $e", )
            }
          
        }

        if (listUser.size<=3){
            createLiveButton.postValue(true)
        }else{
            createLiveButton.postValue(false)
        }

//        loadImagesFromStorage()
        return listUser
    }

    override fun update(data: User) {

        var intent = Intent(this,CustomSignUpActivity::class.java)
        intent.putExtra(naviDirect.LOGGEDLIST_TO_UPDATE.toString(),naviDirect.LOGGEDLIST_TO_UPDATE.toString())
        intent.putExtra(userCodes.FIRST_NAME.toString(),data.firstName)
        intent.putExtra(userCodes.LAST_NAME.toString(),data.lastName)
        intent.putExtra(userCodes.EMAIL.toString(),data.email)
        intent.putExtra(userCodes.PASSWORD.toString(),data.password)
        intent.putExtra(userCodes.PROFILE_IMG.toString(),data.img)
        intent.putExtra(userCodes.User_Id.toString(),data.userid)
        startActivityForResult(intent,code.updatecode)
//        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            code.updatecode->{
                Log.e(TAG, "onActivityResult: $data", )
                Toast.makeText(this,"updated code call",Toast.LENGTH_SHORT).show()
//                reInitdata()
//                ggjg
            }
            code.createusercode->{
                Log.e(TAG, "onActivityResult: $data", )
                Toast.makeText(this,"create user code call",Toast.LENGTH_SHORT).show()
//                reInitdata()
            }
            else->{
                Toast.makeText(this, requestCode, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun deleteUser(data: User) {
        deleteUser1(data)
    }

    override fun itemCliked(user: User) {
        try {
            val intent = Intent(this,ChatActivity::class.java)
            intent.putExtra("userId",user.userid)
            intent.putExtra("name",user.firstName)
            intent.putExtra("image",user.img)
            intent.putExtra("senderUid",currentUserUID.toString())
            OpenChatRoom(intent)
        }
        catch (e:Exception){
            Log.e("DownLoadListner", "itemCliked: $e", )
        }
    }

    override fun ongroupItemClicked() {
        var name = "The Boys Group"

        var img ="https://wallpapers.com/images/featured/the-boys-1fe3hnl120ch1bc6.webp"

        try {
            val intent = Intent(this,ChatActivity::class.java)
            intent.putExtra("userId",user?.userid)
            intent.putExtra("name",name)
            intent.putExtra("image",img)
            intent.putExtra("senderUid",currentUserUID.toString())
            OpenChatRoom(intent)
        }
        catch (e:Exception){
            Log.e("DownLoadListner", "itemCliked: $e", )
        }
    }

    fun OpenChatRoom(intent: Intent){
        startActivity(intent)
    }


    fun deleteUser1(user: User){
//
//        myfireobj.db.collection("users").document(user.userid.toString())
//            .delete()
//            .addOnCompleteListener {
//                Toast.makeText(this, "successfully deleted user", Toast.LENGTH_SHORT).show()
//                reInitdata()
//                deleteFromStorage(user)
//            }
//            .addOnFailureListener {
//                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
//            }

        myfireobj.db.collection("users").addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
//                myfireobj.db.collection("users").document(user.userid.toString())
//            .delete()
//            .addOnCompleteListener {
//
//            }.addOnFailureListener{
//                        Toast.makeText(this@LoggedUserDetails, "failed to delete", Toast.LENGTH_SHORT).show()
//                    }
                reInitdata()

            }
        })
    }



        fun deleteFromStorage(user: User){
                var fileRef = storageRef.child("images/${user.userid}")
                    .delete()
                    .addOnCompleteListener {
                        Toast.makeText(this,"${user.firstName} deleted!",Toast.LENGTH_SHORT).show()
//                        Log.d("FileDeleted", "deleteFromStorage: ${it}>>successfully deleted fileref${user.img?.toUri()?.toFile()?.name}")
                    }
                    .addOnFailureListener {
                        Log.e("FileDeleted", "deleteFromStorage: $it", )
                    }
        }


    private suspend fun getCurrentUserDetails(uid:String):User?{
        var mdata :User?=null
        var fname=""
        var lname=""
        var img=""
        var password=""
        var email=""
        var uid1=""


       var a= db.collection("users").document(uid).get().await()

        img = a.get("img").toString()
        fname = a.get("firstName").toString()
        lname = a.get("lastName").toString()
        email = a.get("email").toString()
        password = a.get("password").toString()
        uid1 = a.get("userid").toString()
        user = User(img,fname,lname,email,password,uid1)
        mdata = user
        Log.e("abczz", "getCurrentUserDetails: $user", )



        Log.e(TAG+43, "getCurrentUserDetails: $user", )
        return mdata
    }

    @SuppressLint("SuspiciousIndentation")
   private fun logoutOperation(){

       val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
           .requestIdToken(getString(R.string.default_web_client_id))
           .requestEmail()
           .build()

     mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
       mGoogleSignInClient.signOut()
    }
    private fun HandleListner()
    {
        var list = mutableListOf<Any>()
        db.collection("users").addSnapshotListener { value, error ->

            try {
                if (value!=null) {
                    value?.forEach {
                        var myData = gson.toJson(it.data)
                        var mdata = gson.fromJson(myData, User::class.java)
                        list.add(mdata)
                    }
//            fdfd

                    list?.removeIf {
                        it as User
                        Log.e("mData1239", "getAllUser:removed ${it.userid}>>${user?.userid}",)
                        it.userid == user?.userid
                    }
                    GlobalScope.launch(Dispatchers.Main){

                        if (adapter!=null){
                           adapter = LoggedUserRcAdapter(list,this@LoggedUserDetails)
                            rc.adapter = adapter
                            adapter.notifyDataSetChanged()
//                            Toast.makeText(this@LoggedUserDetails, "adapter data added!!", Toast.LENGTH_SHORT).show()
                            Log.e(TAG, "HandleListner: task adaper done data added" +
                                    "$list", )
                        }else{
//                            Toast.makeText(this@LoggedUserDetails, "adapter null", Toast.LENGTH_SHORT).show()
                            Log.e(TAG, "HandleListner: task adaper null!!", )

                        }
//                        try {
//                            if (list.isNullOrEmpty().not()) {
//                        adapter.clearUser()
//                                var adapter = LoggedUserRcAdapter(list, this@LoggedUserDetails)
//                                launch(Dispatchers.Main) {
//                                    rc.adapter = adapter
//                                }
//                                adapter.notifyDataSetChanged()
//                                Log.e(TAG + 21, "getAllUser: if block data>>$list")
//                            } else {
//                                Log.e(TAG + 21, "getAllUser: else data >>$list",)
//                            }
//
//                        } catch (e: Exception) {
//                            Log.e(TAG, "getAllUser: $e",)
//                        }
                    }
                    Log.e("handleLitnerError2", "HandleListner: $value", )

                }
                Log.e("handleLitnerError1", "HandleListner: $error", )
                adapter.notifyDataSetChanged()
            }
            catch(e:Exception){
                Log.e("mytest", "HandleListner: ${e.message}>>$error", )
            }




        }
    }
    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Exit Confirmation")
        builder.setMessage("Are you sure you want to exit the app?")
        builder.setPositiveButton("Yes") { _, _ ->
            // Perform any necessary cleanup here
            finish()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }


}
enum class userCodes{
    FIRST_NAME,
    LAST_NAME,
    PROFILE_IMG,
    EMAIL,
    PASSWORD,
    User_Id,
}
enum class naviDirect{
    LOGGEDLIST_TO_UPDATE,
    SIGNUP_SCREEN
}


data class User(
    @SerializedName("img")
    var img: String,

    @SerializedName("firstName")
    var firstName:String,

    @SerializedName("lastName")
    var lastName:String,

    @SerializedName("email")
    var email:String,

    @SerializedName("password")
    var password:String,

    @SerializedName("userid")
    var userid:String?=null
//)
)
/**
 *
 *  : Parcelable {
constructor(parcel: Parcel) : this(
parcel.readString()?:"",
parcel.readString()?:"",
parcel.readString()?:"",
parcel.readString()?:"",
parcel.readString()?:"",
parcel.readString()
) {
}

override fun writeToParcel(parcel: Parcel, flags: Int) {
parcel.writeString(img)
parcel.writeString(firstName)
parcel.writeString(lastName)
parcel.writeString(email)
parcel.writeString(password)
parcel.writeString(userid)
}

override fun describeContents(): Int {
return 0
}

companion object CREATOR : Parcelable.Creator<User> {
override fun createFromParcel(parcel: Parcel): User {
return User(parcel)
}

override fun newArray(size: Int): Array<User?> {
return arrayOfNulls(size)
}
}
}
 */



enum class userDetails{
    NAME_,
    PROFILE_IMG_,
    UID_,
    EMAIL_
}
object code{
    const val updatecode = 2001
    const val createusercode = 2002

}


