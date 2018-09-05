package com.aiitec.entitylibary.model

import android.os.Parcel
import android.os.Parcelable
import com.aiitec.openapi.model.Entity

/**
 * Created by ailibin on 2018/6/28.
 */
class Item() : Entity(),Parcelable {

    var content: String? = null

    var id: Long? = null

    var isSelected = false

    constructor(parcel: Parcel) : this() {
        content = parcel.readString()
        id = parcel.readValue(Long::class.java.classLoader) as? Long
        isSelected = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(content)
        parcel.writeValue(id)
        parcel.writeByte(if (isSelected) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Item> {
        override fun createFromParcel(parcel: Parcel): Item {
            return Item(parcel)
        }

        override fun newArray(size: Int): Array<Item?> {
            return arrayOfNulls(size)
        }
    }

}