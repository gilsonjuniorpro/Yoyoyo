package com.yoyoyo.ca.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
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

        }else{
            Toast.makeText(this, getString(R.string.msg_fill_all_fields), Toast.LENGTH_LONG).show()
        }
    }
}
