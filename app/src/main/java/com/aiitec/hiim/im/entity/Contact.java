package com.aiitec.hiim.im.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author ailibin
 * @version 1.0
 * @createTime 2018/1/4.
 */

public class Contact implements Parcelable {

    /**
     * 显示数据拼音的首字母
     */
    private String sortLetters;
    private String imagePath;
    private String identify;
    private String name;
    private Long userId;
    private boolean isSelected;
    private boolean isGroupOwner;
    /**
     * 用户备注
     */
    private String alias;
    private int eggNum;
    /**
     * 是群聊中个人信息还是单聊中个人信息
     */
    private int type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getIdentify() {
        return identify;
    }

    public void setIdentify(String identify) {
        this.identify = identify;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isGroupOwner() {
        return isGroupOwner;
    }

    public void setGroupOwner(boolean groupOwner) {
        isGroupOwner = groupOwner;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Contact() {

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.sortLetters);
        dest.writeString(this.imagePath);
        dest.writeString(this.identify);
        dest.writeString(this.name);
        dest.writeValue(this.userId);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isGroupOwner ? (byte) 1 : (byte) 0);
        dest.writeString(this.alias);
        dest.writeInt(this.eggNum);
        dest.writeInt(this.type);
    }

    protected Contact(Parcel in) {
        this.sortLetters = in.readString();
        this.imagePath = in.readString();
        this.identify = in.readString();
        this.name = in.readString();
        this.userId = (Long) in.readValue(Long.class.getClassLoader());
        this.isSelected = in.readByte() != 0;
        this.isGroupOwner = in.readByte() != 0;
        this.alias = in.readString();
        this.eggNum = in.readInt();
        this.type = in.readInt();
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel source) {
            return new Contact(source);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };
}
