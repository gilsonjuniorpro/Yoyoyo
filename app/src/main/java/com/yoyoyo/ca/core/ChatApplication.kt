package com.yoyoyo.ca.core

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChatApplication : Application(), Application.ActivityLifecycleCallbacks{

    private fun setOnLine(enabled: Boolean){
        var uid = FirebaseAuth.getInstance().uid

        if(uid != null){
            FirebaseFirestore.getInstance().collection("users")
                .document(uid)
                .update("online", enabled)
        }
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