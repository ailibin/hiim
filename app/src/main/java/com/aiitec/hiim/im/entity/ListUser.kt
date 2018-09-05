package com.aiitec.hiim.im.entity

import android.os.Parcel
import android.os.Parcelable
import com.aiitec.openapi.json.annotation.JSONField
import com.aiitec.openapi.model.Entity

/**
 * Created by ailibin on 2018/1/4.
 * 列表专用的User对象，因为如果直接用user对象，字段太多，json解析会有点慢
 * @version 1.0
 */
class ListUser() : Entity(), Parcelable {

    var id: Long = 0
    var nickname: String? = null
    var imagePath: String? = null
    var mobile: String? = null
    @JSONField(isPassword = true)
    var password: String? = null
    var gradeId: Int = 0
    var relationship: Int = 0
    //验证信息
    var msg: String = ""
    //消息免打扰(私人的免打扰腾讯云不支持)
    var nodisturb: Int = 0
    //备注信息
    var alias: String? = null
    //是否已经关注 0 没有关注  1 已经关注
    var attention: Int = 0
    //关注数量
    var attentionNum: Int = 0
    //粉丝数量
    var fansNum: Int = 0

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        nickname = parcel.readString()
        imagePath = parcel.readString()
        mobile = parcel.readString()
        password = parcel.readString()
        gradeId = parcel.readInt()
        relationship = parcel.readInt()
        msg = parcel.readString()
        nodisturb = parcel.readInt()
        alias = parcel.readString()
        attention = parcel.readInt()
        attentionNum = parcel.readInt()
        fansNum = parcel.readInt()

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(nickname)
        parcel.writeString(imagePath)
        parcel.writeString(mobile)
        parcel.writeString(password)
        parcel.writeInt(gradeId)
        parcel.writeInt(relationship)
        parcel.writeString(msg)
        parcel.writeInt(nodisturb)
        parcel.writeString(alias)
        parcel.writeInt(attention)
        parcel.writeInt(attentionNum)
        parcel.writeInt(fansNum)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ListUser> {
        override fun createFromParcel(parcel: Parcel): ListUser {
            return ListUser(parcel)
        }

        override fun newArray(size: Int): Array<ListUser?> {
            return arrayOfNulls(size)
        }
    }


}