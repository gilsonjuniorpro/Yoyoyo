package com.yoyoyo.ca.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Message (
    var text: String?,
    var timestamp: Long?,
    var fromId: String?,
    var toId: String?
) : Parcelable {
    constructor() : this("", 0, "","")
}