package com.yoyoyo.ca.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
open class Message (
    var msg: String?,
    var timestamp: Long?,
    var fromId: String?,
    var toId: String?
) : Parcelable {
    constructor() : this("", 0, "","")
}