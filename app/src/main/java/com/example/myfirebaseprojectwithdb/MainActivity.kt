package com.example.myfirebaseprojectwithdb

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import androidx.core.net.UriCompat
import androidx.core.net.toFile
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.myfirebaseprojectwithdb.application.MyApplication
import com.example.myfirebaseprojectwithdb.databinding.ActivityMainBinding
import com.example.myfirebaseprojectwithdb.myfireobj.auth
import com.example.myfirebaseprojectwithdb.myfireobj.database
import com.example.myfirebaseprojectwithdb.myfireobj.storage
import com.example.myfirebaseprojectwithdb.myfireobj.storageRef
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener

import com.google.firebase.FirebaseException
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.ListResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URI
import java.net.URL
import java.util.*


class MainActivity : AppCompatActivity() {
    val tag = "testcase1"
    private lateinit var binding : ActivityMainBinding
    private lateinit var sharedPref : SharedPreferences
    private lateinit var editor : SharedPreferences.Editor

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001
    var googleProfileUri:Uri?=null

    companion object{
        var sharedPref:SharedPreferences? = null
    }


//@gmail.com
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    sharedPref = getSharedPreferences("my_shared_pref", Context.MODE_PRIVATE)
    editor= sharedPref.edit()

    handleIntentData()
    isUserLogged()
    initgoogleLogin()
//    msghndle()

        binding.signUpId.setOnClickListener {
               startActivity(Intent(this,CustomSignUpActivity::class.java))
//            this.finish()
        }
    binding.SignInUsingGoogle.setOnClickListener {
        loginUsingIntent()
    }
    binding.loginBtn.setOnClickListener {
        login(binding.mailId.text.trim().toString(),binding.password.text.trim().toString())
    }
    }

    private fun login(email:String , password:String){
        try{

        myfireobj.auth.signInWithEmailAndPassword(email.trim(), password.trim())
            .addOnCompleteListener {
                if(it.isSuccessful){

                    var uid  = it.result.user?.uid!!
                    GlobalScope.launch (Dispatchers.IO){
                        var token =  FirebaseMessaging.getInstance().token.await()
                        Log.e("deviceToken", "login: $token", )
                        myfireobj.db.collection("users").document(uid).update("fcmToken",token )
                            .addOnCompleteListener{
                                Log.e("deviceToken", "login: task completed!!", )
                            }
                            .addOnFailureListener{
                                Log.e("deviceToken", "login: task failed!!>>${it.message}", )
                            }

                    }


                    Log.e("signInWithEmailAndPassword", "login: ${it.result.user?.uid}>>${auth.currentUser?.uid}", )
                    startActivity(Intent(this,LoggedUserDetails::class.java))
                    Log.d(tag, "login: successfully logged ${it.result.user?.displayName}${it.result.user?.email}")
                    editor.putString(phref.USER_UID.toString(),it.result.user?.uid)
                    editor.apply()
                    this.finish()
                }
            }
            .addOnFailureListener {

                when(it){
                    is FirebaseException ->{
                        Toast.makeText(this, "INVALID_USER", Toast.LENGTH_SHORT).show()
                    }
                    else->{
                        Log.d(tag+1, "login: $it")
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()

                    }
                }
            }
        }
        catch (e:Exception){
            Log.e(tag, "onCreate: loginbtn click  ${e.message}", )
        }
    }

    private fun isUserLogged(){
        var abc:ListResult?=null
        GlobalScope.launch (Dispatchers.IO){
            abc = storageRef.child("images").listAll().await()
            if (abc?.items?.isEmpty()?.not() ==true &&sharedPref?.getString(phref.USER_UID.toString(),"").isNullOrBlank().not() ){
                Log.e("qwer111", "isUserLogged: ${sharedPref?.getString(phref.USER_UID.toString(),"")}", )
                var intent = Intent(this@MainActivity,LoggedUserDetails::class.java)
                intent.putExtra("my_uid",sharedPref?.getString(phref.USER_UID.toString(),""))
                startActivity(intent)
                finish()
            }else{
                Log.e("qwer111", "isUserLogged: ${abc?.items?.isEmpty()?.not()}", )
            }
        }


    }

  fun initgoogleLogin() {
      val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
          .requestIdToken(getString(R.string.default_web_client_id))
          .requestEmail()
          .build()

      mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

  }

    private fun loginUsingIntent(){
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            RC_SIGN_IN->{
               var task = GoogleSignIn.getSignedInAccountFromIntent(data)

                try {
                    var account = task.getResult(ApiException::class.java)
                    handleLogin(account)
                }
                catch (e:Exception){
                    Log.w("SignInFailed", "Google sign-in failed", e)
                    Toast.makeText(this, "Google Sign-In failed.", Toast.LENGTH_SHORT).show()
                }
            }
            else->{
                Log.e("CodeTEst", "onActivityResult: $requestCode", )
            }
        }




    }
    @SuppressLint("SuspiciousIndentation")
    private fun handleLogin(account: GoogleSignInAccount) {
        if (account!=null){

            var name = account.displayName
            var img = account.photoUrl
            var  email = account.email
            var idToken = account.idToken
//            var name1 = account.account?.name
            val userId = account.id

            googleProfileUri = img
            Log.d("UserDAta123", "handleLogin: ${name}>> jjk>> ${img}>> ${email}>> ${idToken} ")
            editor.putString(phref.PHREF_KEY.toString(),email)
            editor.apply()
            val freshUser = User(img.toString(),
                name?.split(" ")?.get(0).toString(),
                name?.split(" ")?.get(1).toString(),
                email.toString(),
                password = "123456",
                userId
            )

                savedata(freshUser)

        }

    }
    @SuppressLint("SuspiciousIndentation")
    fun savedata(user: User){
        var date = Date()

       var mydata =  database.child(user.userid.toString())

        mydata.setValue(user).addOnCompleteListener {
            Toast.makeText(this, "successfully saved data", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }




      var store =  myfireobj.db.collection("users").document().set(user)
          .addOnCompleteListener {

                Toast.makeText(this, "logged", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this,LoggedUserDetails::class.java))
                Log.e("inputstreamtest", "savedata: url>> ${user.img }", )




                GlobalScope.launch (Dispatchers.IO) {

                    var inputstream = getInputStreamFromImageUrl(user.img.toString())

                            if (inputstream!=null){
                                storageRef.child("images/${user.email}")
                                    .putStream(inputstream)
                                    .addOnCompleteListener{
                                        Toast.makeText(this@MainActivity, "image uploaded!!", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this@MainActivity, "error occured !! ", Toast.LENGTH_SHORT).show()
                                        Log.e("inputstreamtest", "savedata: $it", )
                                    }

                            }

                }
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                Log.e("errorData", "savedata: $it", )
            }

    }

    fun msghndle(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("satta123", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            val token = task.result
            Log.d("satta123", token)
        })
    }
    private fun handleIntentData(){
        var data = intent.getStringExtra(navChain.LOGGED_USER_TO_MAIN.toString())
        if (data=="0"){
            editor.clear().apply()
            Log.e("dataHandle", "handleIntent: if call $data", )
        }else{
            Log.e("dataHandle", "handleIntent: if call $data", )
        }
    }
    fun getInputStreamFromImageUrl(imageUrl: String): InputStream? {
        val url = URL(imageUrl)

        val inputStream: InputStream? = try {
            Log.e("inputstreamtest", "getInputStreamFromImageUrl: $url", )
            url.openStream()
        } catch (e: IOException) {
            // Handle the error
            null
        }

        if (inputStream == null) {
            // The image could not be found or downloaded.
            return null
        }

        return inputStream
    }




}
enum class navChain{
    LOGGED_USER_TO_MAIN
}
enum class phref{
    PHREF_KEY,
    USER_UID,
    CURENT_USER_MODEL,
}