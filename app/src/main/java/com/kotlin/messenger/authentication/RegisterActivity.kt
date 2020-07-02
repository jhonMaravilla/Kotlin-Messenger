package com.kotlin.messenger.authentication

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.kotlin.messenger.R
import com.kotlin.messenger.main.LatestMessagesActivity
import com.kotlin.messenger.model.User

import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var username: String
    lateinit var email: String
    lateinit var password: String
    val storage = Firebase.storage

    var selectedPhotoUri: Uri? = null


    lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        signup_btn_register.setOnClickListener(this)
        signup_txt_login.setOnClickListener(this)
        signup_btn_photo.setOnClickListener(this)

        mAuth = FirebaseAuth.getInstance()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.signup_btn_register -> {
                username = signup_etxt_username.text.toString()
                email = signup_etxt_email.text.toString()
                password = signup_etxt_password.text.toString()

                createUser(email, password);
            }

            R.id.signup_txt_login -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent);
            }

            R.id.signup_btn_photo -> {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, 0)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data

            var bitmap: Bitmap
            var bitmapPhotoDrawable: BitmapDrawable

            selectedPhotoUri?.let {
                if (Build.VERSION.SDK_INT < 28) {
                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
                } else {
                    val source = ImageDecoder.createSource(contentResolver, selectedPhotoUri!!)
                    bitmap = ImageDecoder.decodeBitmap(source)
                }

                signup_btn_photo.alpha = 0f

                signup_cimage_photo.setImageBitmap(bitmap)
            }

        }
    }

    fun createUser(email: String, password: String) {
        if (mAuth != null) {
            if (!email.isEmpty() && !password.isEmpty()) {
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener

                    signup_etxt_username.setText("")
                    signup_etxt_email.setText("")
                    signup_etxt_password.setText("")

                    uploadImageToFirebase()
                }
            }
        }
    }

    private fun uploadImageToFirebase() {
        if (selectedPhotoUri == null) {
            return
        }

        val filename = UUID.randomUUID().toString()
        val ref = storage.reference.child("/images/$filename")

        ref.putFile(selectedPhotoUri!!).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener {
                saveUserToFirebaseDatabase(it.toString())
            }
        }.addOnFailureListener {
            Log.d("PHOTO", "uploadImageToFirebase: ${it.message}")
        }

    }

    private fun saveUserToFirebaseDatabase(imageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, username, imageUrl, email)

        ref.setValue(user).addOnSuccessListener {
            val intent = Intent(this, LatestMessagesActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent);
        }.addOnFailureListener{
            Log.d("DATABASE", "saveUserToFirebaseDatabase: ${it.message}")
        }
    }

}