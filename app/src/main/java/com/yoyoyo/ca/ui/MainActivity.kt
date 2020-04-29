package com.yoyoyo.ca.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.yoyoyo.ca.R
import com.yoyoyo.ca.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.btLogin.setOnClickListener{ login() }

        binding.tvRegister.setOnClickListener{ goToRegister() }
    }

    private fun goToRegister() {
        val it = Intent(this, RegisterActivity::class.java)
        startActivity(it)
    }

    private fun login() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        if(email.isNotEmpty() && password.isNotEmpty()){
            Log.i("Yoyoyo", "success")

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(OnCompleteListener {
                    if(it.isSuccessful){
                        Log.i("Yoyoyo", "user created: " + it.result.user.uid)
                        var intent = Intent(this, MessagesActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }else{
                        Log.i("Yoyoyo", it.exception.toString())
                    }
                })
                .addOnFailureListener(OnFailureListener {
                    Log.i("Yoyoyo", it.message.toString())
                })
        }else{
            Toast.makeText(this, getString(R.string.msg_fill_all_fields), Toast.LENGTH_LONG).show()
        }
    }
}
