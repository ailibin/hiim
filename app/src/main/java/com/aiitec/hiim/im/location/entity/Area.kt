package com.herentan.giftfly.ui.location.entity

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by ailibin on 2018/1/30.
 * 高德地图用到地区信息
 */
class Area() : Parcelable {

    var name = ""

    /**
     * 简单地址
     */
    var district = ""

    /**
     * 详细地址
     */
    var address = ""

    /**
     * 地址id
     */
    var id: Long = 0

    /**
     * 地区编号
     */
    var regionId: String = ""

    /**
     * 位置信息的经纬度坐标点
     */
    var latLon: LatLon? = null

    /**
     * 搜索的关键字,这里为了着重显示
     */
    var searchKey: String = ""
    /**
     * 是否已经选择
     */
    var isSelected = false
    /**
     * 截图路径
     */
    var imagePath: String? = null

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()
        district = parcel.readString()
        address = parcel.readString()
        id = parcel.readLong()
        regionId = parcel.readString()
        latLon = parcel.readParcelable(LatLon::class.java.classLoader)
        searchKey = parcel.readString()
        isSelected = parcel.readByte() != 0.toByte()
        imagePath = parcel.readString()

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(district)
        parcel.writeString(address)
        parcel.writeLong(id)
        parcel.writeString(regionId)
        parcel.writeParcelable(latLon, flags)
        parcel.writeString(searchKey)
        parcel.writeByte(if (isSelected) 1 else 0)
        parcel.writeString(imagePath)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Area> {
        override fun createFromParcel(parcel: Parcel): Area {
            return Area(parcel)
        }

        override fun newArray(size: Int): Array<Area?> {
            return arrayOfNulls(size)
        }
    }


}