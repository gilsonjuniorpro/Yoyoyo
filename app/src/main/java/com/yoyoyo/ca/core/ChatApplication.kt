package com.yoyoyo.ca.core

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jarvis.ca.Mark
import com.yoyoyo.ca.model.User
import com.yoyoyo.ca.repository.AppDatabase
import com.yoyoyo.ca.repository.ChatRepository
import com.yoyoyo.ca.viewmodel.ChatViewModelFactory
import com.yoyoyo.ca.viewmodel.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatApplication : Application(), Application.ActivityLifecycleCallbacks{

    var user: User? = null

    lateinit var repository: ChatRepository

    companion object {
        @get:Synchronized
        lateinit var initializer:ChatApplication

        lateinit var activity: Activity
    }

    override fun onCreate() {
        super.onCreate()
        initializer = this

        repository = ChatRepository(this)

        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                user = repository.getUser(
                    FirebaseAuth.getInstance().uid.toString()
                )
            }
        }
    }

    fun setOnLine(status: Boolean){
        var uid = FirebaseAuth.getInstance().uid

        if(uid != null){
            FirebaseFirestore.getInstance().collection("users")
                .document(uid)
                .update("status", status)
        }
    }

    fun getUserLoggedInFromFirebase(){
        FirebaseFirestore.getInstance().collection("/users")
            .document(FirebaseAuth.getInstance().uid.toString())
            .get()
            .addOnSuccessListener {
                setUserLoggedIn(it.toObject(User::class.java))
            }
            .addOnFailureListener {
                Log.i("Yoyoyo", it.message.toString())
            }
    }

    fun setUserLoggedIn(fromFirebase: User?) {
        this.user = fromFirebase
    }

    fun getUserLoggedIn(): User?{
        return this.user
    }

    override fun onActivityPaused(activity: Activity) {
        setOnLine(false)
    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityDestroyed(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }

    override fun onActivityResumed(activity: Activity) {
        setOnLine(true)
    }
}