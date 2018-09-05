package com.aiitec.entitylibary.model

import android.os.Parcel
import android.os.Parcelable
import com.aiitec.openapi.model.Entity

/**
 * Created by ailibin on 2018/6/29.
 * 评论实体
 */
class Comment() : Entity(), Parcelable {

    /**
     * 评论内容
     */
    var content: String? = null

    /**
     * 点赞数量
     */
    var fabulousNum: Int = 0

    /**
     * 评论时间
     */
    var time: String? = null

    /**
     * 是否已经点赞 1是 2否
     */
    var isFabulous: Int = -1

    /**
     * 评论id
     */
    var id: Long = -1

    /**
     * 同问人数
     */
    var sqNum: Int = 0

//    /**
//     * 是否已经同问 isSq=1已经同问 isSq=2没有同问
//     */
//    var isSq: Int = 0

    /**
     * 是否已经同问 isSame=1已经同问 isSame=2没有同问
     */
    var isSame: Int = 0

    constructor(parcel: Parcel) : this() {
        content = parcel.readString()
        fabulousNum = parcel.readInt()
        time = parcel.readString()
        isFabulous = parcel.readInt()
        id = parcel.readLong()
        sqNum = parcel.readInt()
        isSame = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(content)
        parcel.writeInt(fabulousNum)
        parcel.writeString(time)
        parcel.writeInt(isFabulous)
        parcel.writeLong(id)
        parcel.writeInt(sqNum)
        parcel.writeInt(isSame)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Comment> {
        override fun createFromParcel(parcel: Parcel): Comment {
            return Comment(parcel)
        }

        override fun newArray(size: Int): Array<Comment?> {
            return arrayOfNulls(size)
        }
    }

}