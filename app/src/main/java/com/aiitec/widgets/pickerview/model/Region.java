package com.aiitec.widgets.pickerview.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @Author Xiaobing
 * @Version 1.0
 * Created on 2018/3/16
 * @effect 地区的实体类
 */

public class Region implements Parcelable {

    private int id;
    private int parentId;
    private String name;
    private String pinyin;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.parentId);
        dest.writeString(this.name);
        dest.writeString(this.pinyin);
    }

    public Region() {
    }

    protected Region(Parcel in) {
        this.id = in.readInt();
        this.parentId = in.readInt();
        this.name = in.readString();
        this.pinyin = in.readString();
    }

    public static final Creator<Region> CREATOR = new Creator<Region>() {
        @Override
        public Region createFromParcel(Parcel source) {
            return new Region(source);
        }

        @Override
        public Region[] newArray(int size) {
            return new Region[size];
        }
    };
}
