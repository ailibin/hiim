package com.aiitec.entitylibary.model

import android.os.Parcel
import android.os.Parcelable
import com.aiitec.openapi.model.Entity

/**
 *@Author ailibin
 * @Version 1.0
 * Created on 2018/7/11
 *@effect 消息的实体类
 */
class Message() : Entity(), Parcelable {
    /**
     * 消息id
     */
    var id: Long = -1
    /**
     * 消息内容
     */
    var content: String? = null
    /**
     * 跳转id
     */
    var target_id: Long = -1
    /**
     * 新增时间
     */
    var time: String? = null

    /**
     * 是否有跳转 0 无跳转 1 课程详情 2 我的钱包 3 待评价课程
     *
     */
    var type: Int = 0

    /**
     * a=1 消息标题 a=2 明细内容
     */
    var title: String? = null

    /**
     * 金额
     */
    var price: Double = 0.0

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        content = parcel.readString()
        target_id = parcel.readLong()
        time = parcel.readString()
        type = parcel.readInt()
        title = parcel.readString()
        price = parcel.readDouble()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(content)
        parcel.writeLong(target_id)
        parcel.writeString(time)
        parcel.writeInt(type)
        parcel.writeString(title)
        parcel.writeDouble(price)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Message> {
        override fun createFromParcel(parcel: Parcel): Message {
            return Message(parcel)
        }

        override fun newArray(size: Int): Array<Message?> {
            return arrayOfNulls(size)
        }
    }


}