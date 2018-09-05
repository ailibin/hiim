package com.aiitec.entitylibary.model

import android.os.Parcel
import android.os.Parcelable
import com.aiitec.openapi.model.Entity

/**
 * @author ailibin
 * @version 1.0
 * createTime 2017/12/6.
 * 用户实体
 */
//|--id  Number  是
//|--code  Number  是  用户代码
//|--mobile  Number  是  手机号码
//|--obmLogo  String  是  OBM logo
class User() : Entity(), Parcelable {

    /**
     *  用户id
     */
    var id: Long = 0
    /**
     * 用户代码
     */
    var code: String? = null

    /**
     * 手机号码
     */
    var mobile: String? = null
    /**
     *  OBM logo
     */
    var obmLogo: String? = null

    /**
     * 推送用到,用户首次进入设置默认的推送状态
     */
    var isPush: Int = 0

    var imagePath: String? = null

    //0 表示未设置 1.标示已设置
    var isPaypw: Int = 0

    var name: String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        code = parcel.readString()
        mobile = parcel.readString()
        obmLogo = parcel.readString()
        isPush = parcel.readInt()
        imagePath = parcel.readString()
        isPaypw = parcel.readInt()
        name = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(code)
        parcel.writeString(mobile)
        parcel.writeString(obmLogo)
        parcel.writeInt(isPush)
        parcel.writeString(imagePath)
        parcel.writeInt(isPaypw)
        parcel.writeString(name)
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
