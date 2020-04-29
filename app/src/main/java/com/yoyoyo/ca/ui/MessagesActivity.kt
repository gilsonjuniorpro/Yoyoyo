package com.yoyoyo.ca.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import coil.api.load
import coil.transform.CircleCropTransformation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import com.xwray.groupie.OnItemClickListener
import com.yoyoyo.ca.R
import com.yoyoyo.ca.databinding.ActivityChatBinding
import com.yoyoyo.ca.databinding.ActivityMessagesBinding
import com.yoyoyo.ca.model.Contact
import java.text.SimpleDateFormat
import java.util.*

class MessagesActivity : AppCompatActivity() {

    private val groupAdapter = GroupAdapter<GroupieViewHolder>()
    lateinit var binding: ActivityMessagesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_messages)
        binding.recyclerLast.adapter = groupAdapter
        binding.recyclerLast.layoutManager = LinearLayoutManager(this)

        groupAdapter.setOnItemClickListener(OnItemClickListener { item: Item<GroupieViewHolder>, view: View ->
            var intent = Intent(this@MessagesActivity, ChatActivity::class.java)
            var contactItem = item as MessagesActivity.ContactItem
            intent.putExtra("contact", contactItem.contact)
            startActivity(intent)
        })

        verifyAuthentication()

        fetchLastMessage()
    }

    private fun verifyAuthentication() {
        if(FirebaseAuth.getInstance().uid == null){
            var intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    private fun fetchLastMessage() {
        FirebaseFirestore.getInstance().collection("/last-messages")
            .document(FirebaseAuth.getInstance().uid.toString())
            .collection("contacts")
            .addSnapshotListener{ querySnapshot: QuerySnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->
                var docs = querySnapshot?.documentChanges

                if(docs != null){
                    for(doc in docs){
                        if(doc.type == DocumentChange.Type.ADDED){
                            var contact:Contact = doc.document.toObject(Contact::class.java)
                            groupAdapter.add(ContactItem(contact))
                            binding.recyclerLast.smoothScrollToPosition(groupAdapter.itemCount -1)
                        }
                    }
                }
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

    private class ContactItem() : Item<GroupieViewHolder>() {

        internal var contact: Contact? = null

        constructor(contact: Contact?) : this() {
            this.contact = contact
        }

        override fun getLayout(): Int {
            return R.layout.item_user_message
        }

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            var tvContactName = viewHolder.itemView.findViewById<TextView>(R.id.tvContactName)
            var tvLastMessage = viewHolder.itemView.findViewById<TextView>(R.id.tvLastMessage)
            var tvTime = viewHolder.itemView.findViewById<TextView>(R.id.tvTime)
            var ivContact = viewHolder.itemView.findViewById<ImageView>(R.id.ivContact)

            if(contact != null){
                tvLastMessage.text = contact?.lastMessage
                tvContactName.text = contact?.userName
                tvTime.text = convertLongToTime(contact?.timestamp!!)

                ivContact.load(contact?.photoUrl) {
                    transformations(CircleCropTransformation())
                }
            }
        }

        fun convertLongToTime (time: Long): String {
            val date = Date(time)
            val format = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            return format.format(date)
        }
    }
}