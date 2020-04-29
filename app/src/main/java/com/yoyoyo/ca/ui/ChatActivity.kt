package com.yoyoyo.ca.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import coil.api.load
import coil.transform.CircleCropTransformation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import com.yoyoyo.ca.R
import com.yoyoyo.ca.databinding.ActivityChatBinding
import com.yoyoyo.ca.model.Message
import com.yoyoyo.ca.model.User
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {

    private val groupAdapter = GroupAdapter<GroupieViewHolder>()
    lateinit var binding: ActivityChatBinding
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat)

        user = intent?.extras?.getParcelable("user")

        supportActionBar?.title = user?.userName

        binding.btSendMessage.setOnClickListener{ sendMessage() }

        recyclerMessages.adapter = groupAdapter
        recyclerMessages.layoutManager = LinearLayoutManager(this)

        verifyAuthentication()

        fetchMessages()
    }

    private fun sendMessage() {
        var text = binding.edMessage.text.toString()
        binding.edMessage.text = null

        var fromId = FirebaseAuth.getInstance().uid
        var toId = user?.uuid
        var timestamp = System.currentTimeMillis()

        var message: Message = Message(text, timestamp, fromId, toId, false)

        if(message.text!!.isNotEmpty()){
            FirebaseFirestore.getInstance().collection("conversations")
                .document(fromId)
                .collection(toId)
        }

    }

    private fun fetchMessages() {

    }

    private fun verifyAuthentication() {
        if(FirebaseAuth.getInstance().uid == null){
            var intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.contacts -> {
                var intent = Intent(this, ContactsActivity::class.java)
                startActivity(intent)
            }
            R.id.logout -> {
                FirebaseAuth.getInstance().signOut()
                verifyAuthentication()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private class MessageItem() : Item<GroupieViewHolder>() {

        private var message: Message? = null

        constructor(message: Message?) : this() {
            this.message = message
        }

        override fun getLayout(): Int {
            return if(message?.fromId == FirebaseAuth.getInstance().uid) {
                R.layout.item_message_received
            } else {
                R.layout.item_message_sent
            }
        }

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            var tvUserMessage = viewHolder.itemView.findViewById<TextView>(R.id.tvUserMessage)
            var ivUserMessage = viewHolder.itemView.findViewById<ImageView>(R.id.ivUserMessage)
            if(ChatActivity().user != null) {
                ivUserMessage.load(ChatActivity().user?.profileUrl) {
                    crossfade(true)
                    crossfade(500)
                    transformations(CircleCropTransformation())
                }
                tvUserMessage.text = message?.text
            }
        }
    }
}
