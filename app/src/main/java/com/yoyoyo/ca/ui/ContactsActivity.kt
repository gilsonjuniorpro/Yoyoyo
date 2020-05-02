package com.yoyoyo.ca.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import coil.api.load
import coil.transform.CircleCropTransformation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import com.xwray.groupie.OnItemClickListener
import com.yoyoyo.ca.R
import com.yoyoyo.ca.core.ChatApplication
import com.yoyoyo.ca.databinding.ActivityContactsBinding
import com.yoyoyo.ca.model.User
import com.yoyoyo.ca.repository.ChatRepository
import com.yoyoyo.ca.viewmodel.ChatViewModelFactory
import com.yoyoyo.ca.viewmodel.UserViewModel

class ContactsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContactsBinding
    private val groupAdapter = GroupAdapter<GroupieViewHolder>()
    private lateinit var chatApplication: ChatApplication
    private var me: User? = null

    private val viewModel: UserViewModel by lazy {
        ViewModelProvider(
            this,
            ChatViewModelFactory(
                ChatRepository(this)
            )
        ).get(UserViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_contacts)

        chatApplication = application as ChatApplication
        application.registerActivityLifecycleCallbacks(
            chatApplication
        )

        viewModel.user.observe(
            this,
            androidx.lifecycle.Observer {
                me = it
                binding.tvLoggedUser.text = me?.userName
            }
        )
        viewModel.onRecoverUser(FirebaseAuth.getInstance().uid.toString())

        if(me == null) {
            FirebaseFirestore.getInstance().collection("/users")
                .document(FirebaseAuth.getInstance().uid.toString())
                .get()
                .addOnSuccessListener {
                    me = it.toObject(User::class.java)
                    binding.tvLoggedUser.text = me?.userName
                }
                .addOnFailureListener {
                    Log.i("Yoyoyo", it.message.toString())
                }
        }

        binding.recyclerContacts.adapter = groupAdapter
        binding.recyclerContacts.layoutManager = LinearLayoutManager(this)

        groupAdapter.setOnItemClickListener(OnItemClickListener { item: Item<GroupieViewHolder>, view: View ->
            var intent = Intent(this@ContactsActivity, ChatActivity::class.java)
            var userItem = item as UserItem
            intent.putExtra("user", userItem.user)
            startActivity(intent)
        })
        verifyAuthentication()

        fetchUsers()
    }

    private fun fetchUsers() {
        FirebaseFirestore.getInstance().collection("/users")
            .addSnapshotListener(EventListener{ querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException? ->
                if(e != null){
                    return@EventListener
                }
                var docs: List<DocumentSnapshot> = querySnapshot!!.documents
                groupAdapter.clear()
                for(doc in docs){
                    var user = doc.toObject(User::class.java)
                    if(user?.uuid == FirebaseAuth.getInstance().uid)
                        continue

                    groupAdapter.add(UserItem(user))
                    groupAdapter.notifyDataSetChanged()

                }
            })
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
            R.id.logout -> {
                chatApplication.setOnLine(false)
                FirebaseAuth.getInstance().signOut()
                verifyAuthentication()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private class UserItem() : Item<GroupieViewHolder>() {

        internal var user: User? = null

        constructor(user: User?) : this() {
            this.user = user
        }

        override fun getLayout(): Int {
            return R.layout.item_contact
        }

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            var tvContactName = viewHolder.itemView.findViewById<TextView>(R.id.tvContactName)
            var ivContact = viewHolder.itemView.findViewById<ImageView>(R.id.ivContact)
            ivContact.load(user?.userPictureUrl) {
                crossfade(true)
                crossfade(100)
                transformations(CircleCropTransformation())
            }
            tvContactName.text = user?.userName
        }
    }
}