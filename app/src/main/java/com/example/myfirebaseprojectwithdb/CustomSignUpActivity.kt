package com.example.myfirebaseprojectwithdb

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import androidx.core.net.toUri
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.FileUtils
import android.util.Base64
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.myfirebaseprojectwithdb.MainActivity.Companion.sharedPref
import com.example.myfirebaseprojectwithdb.databinding.ActivityCustomSignUpBinding
import com.example.myfirebaseprojectwithdb.myfireobj.auth
import com.example.myfirebaseprojectwithdb.myfireobj.database
import com.example.myfirebaseprojectwithdb.myfireobj.storage
import com.example.myfirebaseprojectwithdb.myfireobj.storageRef
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream
import java.net.URI
import java.util.*
import java.util.regex.Pattern
import kotlin.random.Random

class CustomSignUpActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCustomSignUpBinding
    var userID:String? = null
    var direction:String ?=null
    var user:User?=null
    lateinit var profileImg:String
    lateinit var progressDialog :ProgressDialog
    companion object{

        val TAG = "SATWINDERQWERASDF"
        const val PICK_IMAGE_REQUEST_CODE = 2001
//        private val PERMISSION_OPENGALLERY: Array<String> =
//            arrayOf("android.permission.READ_EXTERNAL_STORAGE")
        var profileUri:Uri ?= null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        profileImg =""



        direction = intent.getStringExtra(naviDirect.LOGGEDLIST_TO_UPDATE.toString())
        try {
            handleUserNavigation()
        }
        catch (e:Exception){
            Log.e(TAG, "onCreate: $e", )
        }

        binding.profileImg.setOnClickListener {
//
//            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_MEDIA_IMAGES)==PackageManager.PERMISSION_GRANTED){
//                openGallery()
//            }else{
//                requestPermissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
//            }





            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Code for devices running Android 5.0 (Lollipop) or higher
                if (ContextCompat.checkSelfPermission(this,  android.Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                    openGallery()
                } else {
                    // Replace PERMISSION_OPENGALLERY with the actual permission(s) you want to request.
                    val storagePermission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
//                val permissionsToRequest = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,storagePermission)
                    this.requestPermissions(arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES), PICK_IMAGE_REQUEST_CODE)
//                ActivityCompat.requestPermissions(this, arrayOf(storagePermission,android.Manifest.permission.READ_EXTERNAL_STORAGE), PICK_IMAGE_REQUEST_CODE)
                }
            } else {
                // Code for devices running Android versions earlier than 4.1
                if (ContextCompat.checkSelfPermission(this,  android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    openGallery()
                } else {
                    // Replace PERMISSION_OPENGALLERY with the actual permission(s) you want to request.
                    val storagePermission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
//                val permissionsToRequest = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,storagePermission)
                    this.requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), PICK_IMAGE_REQUEST_CODE)
//                ActivityCompat.requestPermissions(this, arrayOf(storagePermission,android.Manifest.permission.READ_EXTERNAL_STORAGE), PICK_IMAGE_REQUEST_CODE)
                }
            }



        }
        binding.SignUp.setOnClickListener {

            if (direction != naviDirect.LOGGEDLIST_TO_UPDATE.toString()){

                checkValidation(

                    binding.profileImg
                    ,binding.firstnameid
                    ,binding.lastnameid
                    ,binding.emailSignup
                    ,binding.passwordId
                    ,binding.confirm
                )
                Log.e("btncall", "onCreate: if block call", )
            }
            else{
                user?.let { it1 -> updateUser(it1) }
//                Log.e("btncall", "onCreate: else block call", )

            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

       when(requestCode){
           PICK_IMAGE_REQUEST_CODE->{
               if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                   openGallery()
                   Log.d("coderTam", "onRequestPermissionsResult:$ permission  granted ")

               }else{
                   Log.d("coderTam", "onRequestPermissionsResult:$ permission not granted ")
//                   val permissionsToRequest = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
//                   this.requestPermissions(permissionsToRequest, PICK_IMAGE_REQUEST_CODE)
               }
           }
           else->{
               Log.d("tag1234", "onRequestPermissionsResult: $requestCode")
           }
       }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            PICK_IMAGE_REQUEST_CODE->{

                Log.e(TAG, "onActivityResult: $requestCode", )

                data?.data?.let {
                    var file = uriToFile(this, it)
                    Log.d("MyFile", "onActivityResult: ${file}")
                   var uri = Uri.fromFile(file)
                    Log.d("MyFile", "onActivityResult: ${uri}")
                    profileUri = uri
                    profileImg = profileUri.toString()
                }
                Glide.with(this)
                    .load(profileUri)
                    .into(binding.profileImg)

            }
            else->{
                Log.d(TAG, "onActivityResult: $requestCode ")
            }
        }
    }

    private fun openGallery(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
    }

    private fun signUp(email:String,password:String,img:Uri,fname:String,lname:String,uid:String){

        GlobalScope.launch (Dispatchers.IO){
//            var data = downloadUrl.await()
            loginUsingEmailPassword(email, password,fname, lname)

        }
//        Toast.makeText(this, "image uploaded", Toast.LENGTH_SHORT).show()



    }
    private fun  loginUsingEmailPassword(email:String,password:String,fname:String,lname:String){
        myfireobj.auth.createUserWithEmailAndPassword(email.trim(), password.trim())
            .addOnCompleteListener {


               val demoUser = auth.currentUser?.uid
                Log.e("demousers", "loginUsingEmailPassword1: $demoUser>>", )
                if(it.isSuccessful&&demoUser.isNullOrBlank().not()){

                    sharedPref?.edit()?.putString(phref.USER_UID.toString(),demoUser)?.apply()
//                    Log.e(TAG, "loginUsingEmailPassword2: $demoUser>>", )

                    storageRef.child("images/${demoUser}").putFile(profileUri!!)
                        .addOnCompleteListener { task->


                            GlobalScope.launch (Dispatchers.IO){
                                val imageuri = task.result.storage.downloadUrl.await()
                                storetoDb(
                                    imageuri.toString(),
                                    fname,
                                    lname,
                                    email,
                                    password,
                                    demoUser.toString(),

                                )
                            }


                        }.addOnFailureListener {
                            progressDialog.dismiss()
                            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                        }.addOnProgressListener{
                                taskSnapshot->
                            var progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
                            progressDialog.setTitle("uploading image $progress")
                            progressDialog.show()
                        }
//                    Log.d(TAG+1, "signUp: ${it.result.user?.email}>>${it.result.user?.displayName}>>${userID}>>${userID}")

                }else{

                    Toast.makeText(this, "userid>> $userID", Toast.LENGTH_SHORT).show()

                }
//                Toast.makeText(this, "welcome $fname", Toast.LENGTH_SHORT).show()

                progressDialog.dismiss()
            }
            .addOnFailureListener {
                Log.d(TAG, "signUp: $it")
                progressDialog.dismiss()
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkValidation(image: ImageView?, firstname :EditText?,lastName :EditText?,email: EditText?,password: EditText?,confirmPassword:EditText){

//        val list = listOf(image,firstname,lastName,email,password,confirmPassword)
//        var shouldBreak = false
//        val apassword = list.get(4) as EditText
//        val cpassword = list.get(5) as EditText
//        list.forEach {
//             when(it){
//                is ImageView->{
//                    if (it.drawable==null){
//                        shouldBreak = true
//                        Toast.makeText(this, "please set profile Image", Toast.LENGTH_SHORT).show()
//                    }
//                }
//                is  EditText->{
//                    if (it.text.isEmpty()){
//                        shouldBreak = true
//                        it.setError("Empty data found !!,",ContextCompat.getDrawable(this,R.drawable.error_ic))
//
//
//
//                    }
//                    if (it == binding.emailSignup){
//                        shouldBreak = it.text.toString().isEmailVerified().not()
//                        if (shouldBreak){
//                            binding.emailSignup.setError("format Mismatch !!,",ContextCompat.getDrawable(this,R.drawable.error_ic))
//                        }
//                    }
//                }
//                 else->{
//                     Log.d(TAG, "checkValidation: $it")}
//            }
//            if (shouldBreak) {
//                Log.d(TAG, "checkValidation: $it $shouldBreak")
//                return@forEach
//            }
//        }

//        if (profileImg.isNullOrBlank()){
//            Log.e("satta123", "checkValidation: null or empty", )
//        }else{
//            Log.e("satta123", "checkValidation: $profileUri", )
//        }
        if (profileImg.isNullOrBlank()){
            Toast.makeText(this, "Image null", Toast.LENGTH_SHORT).show()
        }else if(firstname?.text.isNullOrBlank()){
            Toast.makeText(this, "First name blank", Toast.LENGTH_SHORT).show()
        }else if(lastName?.text.isNullOrBlank()){
            Toast.makeText(this, "Last name blank", Toast.LENGTH_SHORT).show()

        }else if(email?.text.isNullOrBlank()){
            Toast.makeText(this, "Email blank", Toast.LENGTH_SHORT).show()

        }
        else if(isEmailValid(email?.text.toString()).not()){
            Toast.makeText(this, "Please enter valid email", Toast.LENGTH_SHORT).show()
        } else if(password?.text.isNullOrBlank()){
            Toast.makeText(this, "Password  blank", Toast.LENGTH_SHORT).show()
        } else if(isPasswordValid(password?.text.toString()).not()){
            Toast.makeText(this, "Must enter strong password", Toast.LENGTH_SHORT).show()
        }else if(confirmPassword?.text.isNullOrBlank()){
            Toast.makeText(this, "Confirm Password blank", Toast.LENGTH_SHORT).show()

        }else{

            if (password?.text.toString()==confirmPassword.text.toString()){
                signUp(email!!.text.trim().toString(), password!!.text.trim().toString(), profileUri as Uri,
                    firstname?.text?.trim().toString()
                    ,lastName?.text?.trim().toString(), userID.toString()  )
            }else{
                Toast.makeText(this, "something went wrong ", Toast.LENGTH_SHORT).show()
                binding.confirm.setError("pasword    mismatch ",ContextCompat.getDrawable(this,R.drawable.error_ic))

            }
//            Toast.makeText(this, "all condition full fill no one is empty", Toast.LENGTH_SHORT).show()
        }




    }
    private fun storetoDb(image: String, firstname :String,lastName :String,email: String,password: String,uid:String){

        var fcm = ""
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            // Use 'token' to send messages to this device
            fcm = token
            var user = User(image, firstname, lastName, email, password,uid,fcm)
            myfireobj.db.collection("users").document(uid.toString())
                .set(user).addOnCompleteListener {
                    sharedPref?.edit()?.putString(phref.USER_UID.toString(),uid)
                    val resultIntent = Intent()
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }
                .addOnFailureListener {
                    Log.d(TAG, "storetoDb: $it")
                }
            Log.e("testcase2", "onCreate: token>>$token", )
        }.addOnFailureListener { e ->
            Toast.makeText(this, "$e", Toast.LENGTH_SHORT).show()
            // Handle the failure to retrieve the token
        }

    }
    private fun openIntent(){
        startActivity(Intent(this,LoggedUserDetails::class.java))
    }

    private fun handleUserNavigation(){
        //naviDirect

        if (direction == naviDirect.LOGGEDLIST_TO_UPDATE.toString()){
            Log.d(TAG, "handleUserNavigation: if block signUpscreen $direction ")
            var first = intent.getStringExtra(userCodes.FIRST_NAME.toString())
            var last = intent.getStringExtra(userCodes.LAST_NAME.toString())
            var email = intent.getStringExtra(userCodes.EMAIL.toString())
            var password = intent.getStringExtra(userCodes.PASSWORD.toString())
            var img = intent.getStringExtra(userCodes.PROFILE_IMG.toString())
             userID = intent.getStringExtra(userCodes.User_Id.toString())

//            binding.profileImg.setImageURI(Uri.parse(img))
            val requestOptions = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache the image
                .placeholder(R.drawable.baseline_account_circle_24) // Placeholder image while loading (optional)

            binding.pageTitle.setText("Update Page")
            profileUri = Uri.parse(img)
            // Load the image using Glide
            user = User(
                img.toString(),
                first.toString(),
                last.toString(),
                email.toString(),
                password.toString(),
                userID

            )
            Glide.with(this)
                .load(profileUri)
                .apply(requestOptions)
                .into(binding.profileImg)
            binding.firstnameid.setText(first)
            binding.lastnameid.setText(last)
            binding.emailSignup.setText(email)
            binding.passwordId.setText(password)
            binding.SignUp.setText("UpdateUser")
//            binding.confirm.visibility = View.GONE

        }else{
            Log.d(TAG, "handleUserNavigation: signUpscreen else $direction ")
        }
    }

    private fun updateUser(user: User){
//        Log.e(TAG+345, "updateUser: ${user.userid}>> $userID", )
        val updatedData = hashMapOf(
            "firstName" to binding.firstnameid.text.toString(),
            "lastName" to binding.lastnameid.text.toString(),
            "email" to binding.emailSignup.text.toString(),
            "password" to binding.passwordId.text.toString(),
            "img" to profileUri,
            "userid" to userID

        )
        if (isEmailValid(binding.emailSignup.text.toString().trim())){
            if (auth.currentUser!!.email!=binding.emailSignup.text.toString()){
                auth.currentUser?.updateEmail("sattadev@gmail.com")
                    ?.addOnCompleteListener {
                        myfireobj.db.collection("users").document(user.userid.toString())
                            .set(updatedData, SetOptions.merge())
                            .addOnCompleteListener {
                                onBackPressed()

                                val resultIntent = Intent()
                                resultIntent.putExtra("firstName",binding.firstnameid.text.toString())
                                resultIntent.putExtra("lastName" ,binding.lastnameid.text.toString())
                                resultIntent.putExtra("email" , binding.emailSignup.text.toString())
                                resultIntent.putExtra("password" , binding.passwordId.text.toString())
                                resultIntent.putExtra("img" , profileUri)
                                resultIntent.putExtra("userid" , userID)
                                setResult(Activity.RESULT_OK, resultIntent)
                                finish()

                                Log.e("bitmapHandle", "updateUser: ${user.firstName},${user.email}", )
                                updateStorage(user.email.toString(), profileUri!!,)

                            }
                            .addOnFailureListener {
                                Log.e("bitmapHandle", "updateUser: exp : >>$it", )
                            }
                        Toast.makeText(this, "email updated!!>>${binding.emailSignup.text}", Toast.LENGTH_SHORT).show()
                    }
                    ?.addOnFailureListener {
                        Toast.makeText(this, "${auth.currentUser!!.email}", Toast.LENGTH_SHORT).show()
                        Log.e("addfail", "updateUser: ${it.message}", )
                    }

            }
        }else{
            Toast.makeText(this, "please Enter Strong Email", Toast.LENGTH_SHORT).show()
            binding.emailSignup.text.clear()

        }

    }
    private fun updateStorage(oldFile:String,newFileUri:Uri){
        storageRef.child("images/${user?.userid}").putFile(newFileUri)
            .addOnCompleteListener {
//                Log.d("updateStorage12", "updateStorage: file updated ${newFileUri.toFile().name}")
                Log.d("updateStorage12", "if updateStorage: storage updated ")

            }
            .addOnFailureListener {
                Log.d("updateStorage12", "else updateStorage: file updated ${it}")
            }
    }

    private fun uriToFile(context: Context,uri: Uri):File?{


        var inputStream = context.contentResolver.openInputStream(uri)
        if (inputStream!=null){
            try {
//                val randomInt = Random.nextInt(1, 101)
                var date = Date()
                var file = File(context.cacheDir,"$date")
                var outputStream = FileOutputStream(file)
                inputStream.copyTo(outputStream)
                outputStream.close()
                return file
            }
            catch (e:Exception){
                Log.d(TAG, "uriToFile: $e")
            }
            finally {
                inputStream.close()
            }
        }
        return null
    }

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {

                Toast.makeText(this, "granted true", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "granted false", Toast.LENGTH_SHORT).show()

            }
        }
    private fun isEmailValid(email: String): Boolean {
        val regexPattern = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$"
        val pattern = Pattern.compile(regexPattern)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }
    private fun isPasswordValid(email: String): Boolean {
        val regexPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!*])(?!.*\\s).{8,}$"
        val pattern = Pattern.compile(regexPattern)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }


}