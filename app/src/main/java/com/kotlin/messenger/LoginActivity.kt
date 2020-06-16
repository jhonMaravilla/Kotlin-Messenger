package com.kotlin.messenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.view.*

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var register: TextView
    lateinit var login: Button
    lateinit var username: String
    lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        register = login_txt_register
        register.setOnClickListener(this)

        login = login_btn_login
        login.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.login_txt_register -> {
                finish()
            }

            R.id.login_btn_login -> {
                val username = login_etxt_username.text.toString()
                val password = login_etxt_password.text.toString()

                if(!username.isEmpty() && !password.isEmpty()){
                    login(username, password);
                }
            }

        }
    }

    fun login(username:String, password:String){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(username, password).addOnCompleteListener{
            if (!it.isSuccessful){
              Toast.makeText(this, "Invalid credential, try again", Toast.LENGTH_LONG).show()
                return@addOnCompleteListener
            }

            Toast.makeText(this, "Logging in...", Toast.LENGTH_LONG).show()
        }
    }

}