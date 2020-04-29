package com.yoyoyo.ca.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.yoyoyo.ca.R
import com.yoyoyo.ca.databinding.ActivityChatBinding
import com.yoyoyo.ca.databinding.ActivityContactsBinding

class ContactsActivity : AppCompatActivity() {

    lateinit var binding: ActivityContactsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_contacts)
    }
}
