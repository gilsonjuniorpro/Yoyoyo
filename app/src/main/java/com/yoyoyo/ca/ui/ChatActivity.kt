package com.yoyoyo.ca.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import coil.api.load
import coil.transform.CircleCropTransformation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import com.yoyoyo.ca.R
import com.yoyoyo.ca.databinding.ActivityChatBinding
import com.yoyoyo.ca.model.Contact
import com.yoyoyo.ca.model.Message
import com.yoyoyo.ca.model.Notification
import com.yoyoyo.ca.model.User
import kotlinx.android.synthetic.main.activity_chat.*
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity() {

    private var me: User? = null
    private val groupAdapter = GroupAdapter<GroupieViewHolder>()
    lateinit var binding: ActivityChatBinding
    private var user: User? = null
    private var contact: Contact? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat)

        verifyAuthentication()

        user = intent?.extras?.getParcelable("user")
        contact = intent?.extras?.getParcelable("contact")

        if(contact != null){
            user = fromContactToUser(contact!!)
        }

        supportActionBar?.title = user?.userName

        binding.btSendMessage.setOnClickListener{ sendMessage() }

        binding.recyclerMessages.adapter = groupAdapter
        binding.recyclerMessages.layoutManager = LinearLayoutManager(this)

        FirebaseFirestore.getInstance().collection("/users")
            .document(FirebaseAuth.getInstance().uid.toString())
            .get()
            .addOnSuccessListener {
                me = it.toObject(User::class.java)
                fetchMessages()
            }
            .addOnFailureListener {
                Log.i("Yoyoyo", it.message.toString())
            }
    }

    private fun fromContactToUser(contact: Contact): User? {
        return User(
            contact.uuid,
            contact.userName,
            contact.photoUrl,
            "",
            false
        )
    }


    private fun sendMessage() {
        var text = binding.edMessage.text.toString()
        binding.edMessage.text = null

        var fromId:String = FirebaseAuth.getInstance().uid.toString()
        var toId:String = user?.uuid.toString()
        var timestamp = System.currentTimeMillis()

        var message = Message(text, timestamp, fromId, toId)

        if(message.text!!.isNotEmpty()){
            FirebaseFirestore.getInstance().collection("conversations")
                .document(fromId)
                .collection(toId)
                .add(message)
                .addOnSuccessListener {
                    var contact = Contact(
                        toId,
                        user?.userName,
                        message.text,
                        timestamp,
                        user?.profileUrl
                    )

                    FirebaseFirestore.getInstance().collection("/last-messages")
                        .document(fromId)
                        .collection("contacts")
                        .document(toId)
                        .set(contact)

                    if(!user?.online!!){
                        var notification = Notification(
                            message.fromId,
                            message.toId,
                            message.timestamp,
                            message.text,
                            me?.userName
                        )

                        FirebaseFirestore.getInstance().collection("notifications")
                            .document(user?.token!!)
                            .set(notification)
                    }

                }
                .addOnFailureListener {
                    Log.i("Yoyoyo", it.message.toString())
                }

            FirebaseFirestore.getInstance().collection("conversations")
                .document(toId)
                .collection(fromId)
                .add(message)
                .addOnSuccessListener {
                    var contact = Contact(
                        toId,
                        user?.userName,
                        message.text,
                        timestamp,
                        user?.profileUrl
                    )

                    FirebaseFirestore.getInstance().collection("/last-messages")
                        .document(toId)
                        .collection("contacts")
                        .document(fromId)
                        .set(contact)

                    if(!user?.online!!){
                        var notification = Notification(
                            message.fromId,
                            message.toId,
                            message.timestamp,
                            message.text,
                            me?.userName
                        )

                        FirebaseFirestore.getInstance().collection("notifications")
                            .document(user?.token!!)
                            .set(notification)
                    }
                }
                .addOnFailureListener {
                    Log.i("Yoyoyo", it.message.toString())
                }
        }

    }

    private fun fetchMessages() {
        if(me != null){
            var fromId = me?.uuid.toString()
            var toId = user?.uuid.toString()

            FirebaseFirestore.getInstance().collection("/conversations")
                .document(fromId)
                .collection(toId)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener{ querySnapshot: QuerySnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->
                    var docs = querySnapshot?.documentChanges

                    if(docs != null){
                        for(doc in docs){
                            if(doc.type == DocumentChange.Type.ADDED){
                                var message:Message = doc.document.toObject(Message::class.java)
                                groupAdapter.add(MessageItem(message))
                                binding.recyclerMessages.smoothScrollToPosition(groupAdapter.itemCount -1)
                            }
                        }
                    }
                }
        }
    }

    private fun verifyAuthentication() {
        if(FirebaseAuth.getInstance().uid == null){
            var intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_contacts, menu)
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

    inner class MessageItem() : Item<GroupieViewHolder>() {

        private var message: Message? = null

        constructor(message: Message?) : this() {
            this.message = message
        }

        override fun getLayout(): Int {
            return if(message?.fromId == FirebaseAuth.getInstance().uid) {
                R.layout.item_message_sent
            } else {
                R.layout.item_message_received
            }
        }

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            var tvUserMessage = viewHolder.itemView.findViewById<TextView>(R.id.tvUserMessage)
            var tvTime = viewHolder.itemView.findViewById<TextView>(R.id.tvTime)
            var ivUserMessage = viewHolder.itemView.findViewById<ImageView>(R.id.ivUserMessage)

            if(message?.fromId == user?.uuid){
                if(this@ChatActivity.user != null) {
                    ivUserMessage.load(this@ChatActivity.user?.profileUrl) {
                        transformations(CircleCropTransformation())
                    }
                }
            }else{
                if(this@ChatActivity.me != null) {
                    ivUserMessage.load(this@ChatActivity.me?.profileUrl) {
                        transformations(CircleCropTransformation())
                    }
                }
            }
            if(message != null){
                tvUserMessage.text = message?.text
                tvTime.text = convertLongToTime(message?.timestamp!!)
            }
        }

        fun convertLongToTime(time: Long): String {
            val date = Date(time)
            val format = SimpleDateFormat("hh:mm")
            return format.format(date)
        }
    }
}
