package com.aiitec.entitylibary.request

import android.os.Parcel
import android.os.Parcelable
import com.aiitec.openapi.model.RequestQuery

/**
 * Created by ailibin on 2017/12/4.
 */
class UserLoginRequestQuery() : RequestQuery(), Parcelable {

    /**
     * 密码
     */
    var password: String? = null

    /**
     * 电话号码
     */
    var mobile: String? = null

    /**
     * 手机号码/邮箱号码
     */
    var name: String = ""

    /**
     * 第三方绑定的openid
     */
    var code: String? = null

    /**
     * 普通的登录功能需要用到
     */
    var smsKey: String? = null

    /**
     * 推荐人id
     */
    var referee_id: Long? = 0

    constructor(parcel: Parcel) : this() {
        password = parcel.readString()
        mobile = parcel.readString()
        name = parcel.readString()
        code = parcel.readString()
        smsKey = parcel.readString()
        referee_id = parcel.readLong()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(password)
        parcel.writeString(mobile)
        parcel.writeString(name)
        parcel.writeString(code)
        parcel.writeString(smsKey)
        referee_id?.let { parcel.writeLong(it) }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserLoginRequestQuery> {
        override fun createFromParcel(parcel: Parcel): UserLoginRequestQuery {
            return UserLoginRequestQuery(parcel)
        }

        override fun newArray(size: Int): Array<UserLoginRequestQuery?> {
            return arrayOfNulls(size)
        }
    }


}