package com.yoyoyo.ca.core

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jarvis.ca.Mark
import com.yoyoyo.ca.model.User

class ChatApplication : Application(), Application.ActivityLifecycleCallbacks{

    var user: User? = null

    private fun setOnLine(enabled: Boolean){
        var uid = FirebaseAuth.getInstance().uid

        if(uid != null){
            FirebaseFirestore.getInstance().collection("users")
                .document(uid)
                .update("online", enabled)
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