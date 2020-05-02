package com.yoyoyo.ca.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(
    var uuid: String?,
    var userName: String?,
    var userPictureUrl: String?,
    var token: String?,
    var status: Boolean?
) : Parcelable {
    constructor() : this("", "", "", "", null)
}