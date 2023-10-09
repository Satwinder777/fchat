package com.example.myfirebaseprojectwithdb.activity.profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.SyncStateContract.Helpers.update
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import com.bumptech.glide.Glide
import com.example.myfirebaseprojectwithdb.*
import com.example.myfirebaseprojectwithdb.application.MyApplication.Companion.sharedPref
import com.example.myfirebaseprojectwithdb.databinding.ActivityProfileBinding
import com.example.myfirebaseprojectwithdb.myfireobj.auth
import com.example.myfirebaseprojectwithdb.myfireobj.storageRef
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.play.integrity.internal.c
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.core.UserData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileActivity : AppCompatActivity() {
    lateinit var binding : ActivityProfileBinding

    lateinit var mGoogleSignInClient:GoogleSignInClient
    lateinit var user: User
    lateinit var progressDialog :ProgressDialog


    companion object{
        val PICK_IMAGE_REQUEST = 22023
        var permission = Manifest.permission.READ_MEDIA_IMAGES
        var profileUri:Uri?=null


    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)

        setContentView(binding.root)
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
      var fname = intent.getStringExtra("fname")
      var lname = intent.getStringExtra("lname")
       var img = intent.getStringExtra("img")
       var email= intent.getStringExtra("email")
       var uid =  intent.getStringExtra("uidcode")
       user =  User(img = img!!,
            firstName = fname!!,
            lastName = lname!!,
            email = email!!,
            userid = uid!!,
            password = ""
            )
        initView(user)

        Log.e("greatcoder", "onCreate: >>$fname\n$lname\n$img\n$email\n$uid", )




        binding.profileBack.setOnClickListener {
            onBackPressed()
        }

        binding.ProfileImg.setOnClickListener {
            checkPermissionCustom(this)
        }
        binding.saveProfile.setOnClickListener {
            updateUser(user)
            finish()
        }
        binding.logOutBtn.setOnClickListener {
            handleLogout()
        }



    }

    private fun updateUser(user: User) {
        user.firstName = binding.profileUserName.text.trim().toString()
        storageRef.child("images/${user.userid}").putFile(Uri.parse(user.img))
            .addOnCompleteListener {
                task->
                GlobalScope.launch (Dispatchers.IO){
                    var imgUri = task.result.storage.downloadUrl.await()

                    user.img = imgUri.toString()
                    val updatedData = hashMapOf(
                        "firstName" to user.firstName,
                        "lastName" to user.lastName,
                        "email" to user.email,
                        "password" to user.email,
                        "img" to user.img,
                        "userId" to user.userid

                    )
                    myfireobj.db.collection("users").document(user.userid!!)
                        .set(updatedData, SetOptions.merge())
                        .addOnSuccessListener {
                        progressDialog.dismiss()
                        }
                        .addOnFailureListener {
                        progressDialog.dismiss()
                        }
//                        .set(user, SetOptions.merge())
//                        .addOnCompleteListener {
//                            progressDialog.dismiss()
//                            Toast.makeText(this@ProfileActivity, "updated", Toast.LENGTH_SHORT).show()
//                        }.addOnFailureListener {
//                            progressDialog.dismiss()
//                            Log.e("failedlistner", "updateUser: ${it.message}",)
//                            Toast.makeText(this@ProfileActivity, "failed to update!1 ${it.message}", Toast.LENGTH_SHORT).show()
//                        }

                }
            }
            .addOnFailureListener {
                progressDialog.dismiss()
                Log.e("failedlistner", "updateUser: ${it.message}",)
                Toast.makeText(this@ProfileActivity, "failed to update!1 ${it.message}", Toast.LENGTH_SHORT).show()
            }
            .addOnProgressListener {
                taskSnapshot->
                var progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()

                progressDialog.setTitle("$progress % uploaded !")
//                progressDialog.show()
            }
    }

    private fun openGallery(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun checkPermissionCustom(context: Context){
        if (ContextCompat.checkSelfPermission(context, permission)== PackageManager.PERMISSION_GRANTED){
            openGallery()
        }
        else{
            requestPermissions(arrayOf(permission),PICK_IMAGE_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode==PICK_IMAGE_REQUEST){
            var data1 = data?.data
//            var bitmap = BitmapFactory.decodeFile()
            user.img = data1.toString()
            profileUri = data1
            binding.ProfileImg.setImageURI(data1)

            Log.e("onactivityresult", "onActivityResult: $data", )
        }else{
            Log.e("onactivityresult", "onActivityResult: $requestCode", )

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Log.e("onactivityresult", "onActivityResult: $requestCode  >>permission granted!!", )
            openGallery()
        }else{
            Log.e("onactivityresult", "onActivityResult: $requestCode>>permission not granted in permissionresult", )
        }
    }


    private fun handleLogout(){
        var alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Confirm Logout")
        alertDialogBuilder.setMessage("Are you sure you want to logout ")
        alertDialogBuilder.setIcon(R.drawable.baseline_delete_24)
        alertDialogBuilder.setPositiveButton("OK") { dialog, which ->
            // Positive button click handling
            logout()
            dialog.dismiss()

        }
        alertDialogBuilder.setNegativeButton("Cancel") { dialog, which ->
            // Negative button click handling
            dialog.dismiss()
        }

        alertDialogBuilder.create()
        alertDialogBuilder.show()
    }

    fun logout(){
        auth.signOut()
        var intent = Intent(this, MainActivity::class.java)
        intent.putExtra(navChain.LOGGED_USER_TO_MAIN.toString(),"")
        startActivity(intent)
        this.finish()
        sharedPref?.edit()?.clear()?.apply()
        logoutOperation()
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
    fun initView(user: User){
//        binding.ProfileImg.setImageURI(Uri.parse(user.img))
        Glide.with(this)
            .load(user.img) // Replace with your image URL
            .placeholder(R.drawable.error_ic) // Optional: Placeholder image while loading
            .error(R.drawable.error_ic) // Optional: Image to display if loading fails
            .into(binding.ProfileImg)

//        val bitmap = BitmapFactory.decodeFile(user.img)
//        binding.ProfileImg.setImageBitmap(bitmap)
        binding.profileUserName.setText(user.firstName.plus(" ").plus(user.lastName))
    }
    override fun onDestroy() {
        super.onDestroy()
        if (progressDialog != null && progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }
}