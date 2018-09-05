package com.aiitec.entitylibary.model

import android.os.Parcel
import android.os.Parcelable
import com.aiitec.openapi.model.Entity

/**
 * Created by ailibin on 2018/3/8.
 * 微信分享实体
 */
class WxShareInfo() : Entity(), Parcelable {

    /**
     *分享的网页路径
     */
    var url = ""
    /**
     *分享的网页标题
     */
    var title = ""
    /**
     *分享的网页描述
     */
    var description = ""
    /**
     *分享的网页图标(本地资源id)
     */
    var imageRes = -1
    /**
     *分享的网页图标(网络图片)
     */
    var imageUrl = ""
    /**
     * 请求标识
     */
    var reqTag: String? = null

    /**
     * 分享类型：0网页 1纯图片
     */
    var shareType: Int = -1

    constructor(parcel: Parcel) : this() {
        url = parcel.readString()
        title = parcel.readString()
        description = parcel.readString()
        imageRes = parcel.readInt()
        imageUrl = parcel.readString()
        reqTag = parcel.readString()
        shareType = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(url)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeInt(imageRes)
        parcel.writeString(imageUrl)
        parcel.writeString(reqTag)
        parcel.writeInt(shareType)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<WxShareInfo> {
        override fun createFromParcel(parcel: Parcel): WxShareInfo {
            return WxShareInfo(parcel)
        }

        override fun newArray(size: Int): Array<WxShareInfo?> {
            return arrayOfNulls(size)
        }
    }
}