package com.aiitec.hiim.im.model;

import android.content.Context;

import com.aiitec.hiim.R;
import com.tencent.imsdk.TIMGroupMemberRoleType;
import com.tencent.imsdk.TIMGroupReceiveMessageOpt;
import com.tencent.imsdk.ext.group.TIMGroupBasicSelfInfo;
import com.tencent.imsdk.ext.group.TIMGroupCacheInfo;
import com.tencent.imsdk.ext.group.TIMGroupDetailInfo;

/**
 * 群资料
 *
 * @author ailibin
 */
public class GroupProfile implements ProfileSummary {


    private TIMGroupDetailInfo profile;
    private TIMGroupBasicSelfInfo selfInfo;

    public GroupProfile(TIMGroupCacheInfo profile) {
        this.profile = profile.getGroupInfo();
        selfInfo = profile.getSelfInfo();
    }

    public GroupProfile(TIMGroupDetailInfo profile) {
        this.profile = profile;
    }

    /**
     * 获取群ID
     */
    @Override
    public String getIdentify() {
        return profile.getGroupId();
    }


    public void setProfile(TIMGroupCacheInfo profile) {
        this.profile = profile.getGroupInfo();
        selfInfo = profile.getSelfInfo();
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
        return profile.getGroupName();
    }

    /**
     * 获取描述信息
     */
    @Override
    public String getDescription() {
        return null;
    }


    /**
     * 获取自己身份
     */
    public TIMGroupMemberRoleType getRole() {
        return selfInfo.getRole();
    }


    /**
     * 获取消息接收状态
     */
    public TIMGroupReceiveMessageOpt getMessagOpt() {
        return selfInfo.getRecvMsgOption();
    }

    /**
     * 显示详情
     *
     * @param context 上下文
     */
    @Override
    public void onClick(Context context) {
//        Intent intent = new Intent(context, GroupProfileActivity.class);
//        intent.putExtra("identify", profile.getGroupId());
//        context.startActivity(intent);
    }
}
