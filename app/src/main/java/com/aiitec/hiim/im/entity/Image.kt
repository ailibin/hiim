package com.aiitec.hiim.im.entity

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by ailibin on 2018/1/9.
 */
class Image() : Parcelable {

    /**
     * 文件名
     */
    var filename: String? = null
    /**
     * 图片文件夹路径
     */
    var filePath: String? = null
    /**
     * 与服务器端的链接地址
     */
    var path: String? = null

    var imagePath: String? = null
    var extension: String? = null
    var type = -1
    var isSelected: Boolean = false

    constructor(parcel: Parcel) : this() {
        filename = parcel.readString()
        filePath = parcel.readString()
        path = parcel.readString()
        imagePath = parcel.readString()
        extension = parcel.readString()
        type = parcel.readInt()
        isSelected = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(filename)
        parcel.writeString(filePath)
        parcel.writeString(path)
        parcel.writeString(imagePath)
        parcel.writeString(extension)
        parcel.writeInt(type)
        parcel.writeByte(if (isSelected) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Image> {
        override fun createFromParcel(parcel: Parcel): Image {
            return Image(parcel)
        }

        override fun newArray(size: Int): Array<Image?> {
            return arrayOfNulls(size)
        }
    }


}