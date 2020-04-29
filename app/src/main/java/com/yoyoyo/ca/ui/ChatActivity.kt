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
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import com.yoyoyo.ca.R
import com.yoyoyo.ca.databinding.ActivityChatBinding
import com.yoyoyo.ca.model.User
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {

    private val groupAdapter = GroupAdapter<GroupieViewHolder>()
    lateinit var binding: ActivityChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat)

        var user: User? = intent?.extras?.getParcelable("user")

        supportActionBar?.title = user?.userName

        recyclerMessages.adapter = groupAdapter
        recyclerMessages.layoutManager = LinearLayoutManager(this)

        groupAdapter.add(MessageItem(true))
        groupAdapter.add(MessageItem(false))
        groupAdapter.add(MessageItem(false))
        groupAdapter.add(MessageItem(true))
        groupAdapter.add(MessageItem(true))
        verifyAuthentication()

        fetchMessages()
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

    class MessageItem() : Item<GroupieViewHolder>() {

        private var isLeft = false
        private var user: User? = null

        constructor(isLeft: Boolean) : this() {
            this.isLeft = isLeft
        }

        override fun getLayout(): Int {
            return if(isLeft) {
                R.layout.item_message_received
            } else {
                R.layout.item_message_sent
            }
        }

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            /*var tvContactName = viewHolder.itemView.findViewById<TextView>(R.id.tvContactName)
            var ivContact = viewHolder.itemView.findViewById<ImageView>(R.id.ivContact)
            ivContact.load(user?.profileUrl) {
                crossfade(true)
                crossfade(500)
                transformations(CircleCropTransformation())
            }
            tvContactName.text = user?.userName*/
        }
    }
}
