package com.aiitec.hiim.im.model;

import android.content.Context;

import com.aiitec.hiim.R;
import com.aiitec.hiim.base.App;
import com.tencent.imsdk.TIMUserProfile;

/**
 * 好友资料
 *
 * @author ailibin
 */
public class FriendProfile implements ProfileSummary {


    private TIMUserProfile profile;
    private boolean isSelected;

    public FriendProfile(TIMUserProfile profile) {
        this.profile = profile;
    }


    /**
     * 获取头像资源
     */
    @Override
    public int getAvatarRes() {
//        return R.drawable.img_default;
        return R.drawable.my_icon_default_avatar2x;
    }

    /**
     * 获取头像地址
     */
    @Override
    public String getAvatarUrl() {
        return null;
    }

    /**
     * 获取名字
     */
    @Override
    public String getName() {
        if (!"".equals(profile.getRemark())) {
            return profile.getRemark();
        } else if (!"".equals(profile.getNickName())) {
            return profile.getNickName();
        }
        return profile.getIdentifier();
    }

    /**
     * 获取描述信息
     */
    @Override
    public String getDescription() {
        return null;
    }

    /**
     * 显示详情
     *
     * @param context 上下文
     */
    @Override
    public void onClick(Context context) {
//        if (FriendshipInfo.getInstance().isFriend(profile.getIdentifier())){
//            ProfileActivity.navToProfile(context, profile.getIdentifier());
//        }else{
//            Intent person = new Intent(context,AddFriendActivity.class);
//            person.putExtra("id",profile.getIdentifier());
//            person.putExtra("name",getName());
//            context.startActivity(person);
//        }
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    /**
     * 获取用户ID
     */
    @Override
    public String getIdentify() {
        return profile.getIdentifier();
    }


    /**
     * 获取用户备注名
     */
    public String getRemark() {
        return profile.getRemark();
    }


    /**
     * 获取好友分组
     */
    public String getGroupName() {
//
//        if (profile.getFriendGroups().size() == 0){
//            return MyApplication.getContext().getString(R.string.default_group_name);
//        }else{
//            return profile.getFriendGroups().get(0);
//        }
        return App.Companion.getContext().getString(R.string.default_group_name);
    }

}