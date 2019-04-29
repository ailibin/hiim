package com.herentan.giftfly.ui.location.entity

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by ailibin on 2018/1/30.
 * 位置经纬度坐标点
 */
class LatLon() : Parcelable {

    /**
     * 纬度坐标点
     */
    var latitude: Double = 0.0

    /**
     * 经度坐标点
     */
    var longitude: Double = 0.0

    constructor(parcel: Parcel) : this() {
        latitude = parcel.readDouble()
        longitude = parcel.readDouble()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LatLon> {
        override fun createFromParcel(parcel: Parcel): LatLon {
            return LatLon(parcel)
        }

        override fun newArray(size: Int): Array<LatLon?> {
            return arrayOfNulls(size)
        }
    }
}