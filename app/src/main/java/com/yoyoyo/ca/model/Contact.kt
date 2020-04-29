package com.yoyoyo.ca.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.sql.Timestamp

@Parcelize
class Contact (
    var uuid: String?,
    var userName: String?,
    var lastMessage: String?,
    var timestamp: Long?,
    var photoUrl: String?
): Parcelable {
    constructor() : this("", "", "", 0, "")
}