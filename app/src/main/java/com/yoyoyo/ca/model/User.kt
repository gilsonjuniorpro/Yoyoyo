package com.yoyoyo.ca.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(var emp_uuid: String?, var emp_userName: String?, var emp_profileUrl: String?) : Parcelable {
    var uuid: String? = null
    var userName: String? = null
    var profileUrl: String? = null

    constructor() : this("","","") {
    }

    init {
        this.uuid = emp_uuid
        this.userName = emp_userName
        this.profileUrl = emp_profileUrl
    }
}