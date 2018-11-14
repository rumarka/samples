package com.sample.sample1

import android.os.Parcel
import android.os.Parcelable

data class User (
    val userId: Long,
    val name: String,
    val lastActivity: Long,
    val imageLink: String,
    val reputation: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(userId)
        parcel.writeString(name)
        parcel.writeLong(lastActivity)
        parcel.writeString(imageLink)
        parcel.writeInt(reputation)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}
