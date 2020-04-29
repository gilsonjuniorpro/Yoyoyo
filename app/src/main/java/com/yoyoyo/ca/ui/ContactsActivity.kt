package com.yoyoyo.ca.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
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
import com.google.firebase.firestore.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import com.xwray.groupie.OnItemClickListener
import com.yoyoyo.ca.R
import com.yoyoyo.ca.databinding.ActivityContactsBinding
import com.yoyoyo.ca.model.User
import kotlinx.android.parcel.Parcelize

class ContactsActivity : AppCompatActivity() {

    lateinit var binding: ActivityContactsBinding
    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_contacts)

        binding.recyclerContacts.adapter = groupAdapter
        binding.recyclerContacts.layoutManager = LinearLayoutManager(this)

        groupAdapter.setOnItemClickListener(OnItemClickListener { item: Item<GroupieViewHolder>, view: View ->
            var intent:Intent = Intent(this@ContactsActivity, ChatActivity::class.java)
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
                    Log.e("Yoyoyo", e.message)
                    return@EventListener
                }
                var docs: List<DocumentSnapshot> = querySnapshot!!.documents

                for(doc in docs){
                    var user = doc.toObject(User::class.java)
                    Log.d("Yoyoyo", user?.userName)
                    groupAdapter.add(UserItem(user))
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
            R.id.contacts -> {
                var intent = Intent(this, ChatActivity::class.java)
                startActivity(intent)
            }
            R.id.logout -> {
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
            ivContact.load(user?.profileUrl) {
                crossfade(true)
                crossfade(500)
                transformations(CircleCropTransformation())
            }
            tvContactName.text = user?.userName
        }
    }
}