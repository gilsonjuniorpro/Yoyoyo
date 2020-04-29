package com.yoyoyo.ca.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.yoyoyo.ca.R
import com.yoyoyo.ca.databinding.ActivityChatBinding
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {

    private var adapter = GroupAdapter<GroupieViewHolder>()
    lateinit var binding: ActivityChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat)

        recyclerMessages.adapter = adapter
        recyclerMessages.layoutManager = LinearLayoutManager(this)

    }
}
