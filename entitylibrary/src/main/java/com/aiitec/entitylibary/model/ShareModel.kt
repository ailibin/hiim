package com.aiitec.model

import android.os.Parcel
import android.os.Parcelable
import com.aiitec.openapi.model.Entity

/**
 * Created by ailibin on 2018/3/22.
 */
//|--iconPath	String		是	图标路径
//|--shareTitle	String		是	分享标题 “[有人@你] XXX给您了一份礼物”
//|--shareText	String		是	副标题文本 “XXX给您了一份礼物，赶快拆开吧！”
//|--shareUrl	String		是	推广分享页面路径，用于分享出去。
class ShareModel() : Entity(), Parcelable {


    //分享标题 “[有人@你] XXX给您了一份礼物”
    var title: String? = null

    //分享描述
    var abstract: String? = null

    //图标路径
    var wimagePath: String? = null

    //推广分享页面路径，用于分享出去。
    var url: String? = null

    constructor(parcel: Parcel) : this() {
        title = parcel.readString()
        abstract = parcel.readString()
        wimagePath = parcel.readString()
        url = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(abstract)
        parcel.writeString(wimagePath)
        parcel.writeString(url)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ShareModel> {
        override fun createFromParcel(parcel: Parcel): ShareModel {
            return ShareModel(parcel)
        }

        override fun newArray(size: Int): Array<ShareModel?> {
            return arrayOfNulls(size)
        }
    }


}