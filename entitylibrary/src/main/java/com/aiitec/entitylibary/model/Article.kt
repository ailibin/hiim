package com.aiitec.entitylibary.model

import android.os.Parcel
import android.os.Parcelable
import com.aiitec.openapi.model.Entity

/**
 * Created by ailibin on 2018/6/18.
 * 文章
 */
//|--id Number 是 文章 id
//|--title String 是 文章标题
//|--imagePath String 是 封面图
//|--explan String 是 简介 /寄语
class Article() : Entity(), Parcelable {

    /**
     * 文章 id
     */
    var id: Long = -1

    /**
     * 文章标题
     */
    var title: String? = null

    /**
     *  简介 /寄语
     */
    var summary: String? = null

    /**
     * 封面图
     */
    var imagePath: String? = null

    /**
     *  简介 /寄语
     */
    var explan: String? = null

    /**
     * 发布时间
     */
    var time: String? = null

    /**
     * 文章内容
     */
    var content: String? = null

    /**
     * 说明(底部寄语用到)
     */
    var description: String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        title = parcel.readString()
        summary = parcel.readString()
        imagePath = parcel.readString()
        explan = parcel.readString()
        description = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(title)
        parcel.writeString(summary)
        parcel.writeString(imagePath)
        parcel.writeString(explan)
        parcel.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Article> {
        override fun createFromParcel(parcel: Parcel): Article {
            return Article(parcel)
        }

        override fun newArray(size: Int): Array<Article?> {
            return arrayOfNulls(size)
        }
    }

}