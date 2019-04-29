package com.aiitec.hiim.im.model;

import android.content.Context;
import android.text.TextUtils;

import com.aiitec.hiim.im.utils.AiiUtil;
import com.aiitec.hiim.im.utils.LogUtil;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMFriendshipManager;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.TIMValueCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * 会话数据
 */
public abstract class Conversation implements Comparable {

    /**
     * 会话对象id
     */
    protected String identify;

    /**
     * 会话类型
     */
    public TIMConversationType type;

    /**
     * 会话对象名称
     */
    protected String name;

    /**
     * 免打扰模式值(0 否 1 是)
     */
    protected int noDisturb;


    /**
     * 获取最后一条消息的时间
     */
    abstract public long getLastMessageTime();

    /**
     * 获取未读消息数量
     */
    abstract public long getUnreadNum();


    /**
     * 将所有消息标记为已读
     */
    abstract public void readAllMessage();


    /**
     * 获取头像
     */
    abstract public int getAvatar();

    /**
     * 获取头像地址
     *
     * @param context
     * @return
     */
    public String getAvatarUrl(final Context context) {
        if (context == null) {
            return "";
        }
        //这里需要前缀identify本来就是腾讯云那边的id,已经带前缀了
        LogUtil.d("ailibin", "identify: " + identify);
        String avatar = AiiUtil.getString(context, "avatar_" + identify);
        if (!TextUtils.isEmpty(avatar)) {
            return avatar;
        }

        if (type == TIMConversationType.C2C) {
            if (!TextUtils.isEmpty(identify)) {
                ArrayList<String> identifys = new ArrayList<>();
                identifys.add(identify);
                TIMFriendshipManager.getInstance().getUsersProfile(identifys, new TIMValueCallBack<List<TIMUserProfile>>() {
                    @Override
                    public void onError(int i, String s) {
                    }

                    @Override
                    public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                        //这里不是群,只能用个人
                        if (timUserProfiles.size() > 0) {
                            String imagePath = timUserProfiles.get(0).getFaceUrl();
                            String nickname = timUserProfiles.get(0).getNickName();
                            LogUtil.d("ailibin", "TIMUserProfile: " + nickname);
                            AiiUtil.putString(context, "avatar_" + identify, imagePath);
                            AiiUtil.putString(context, "nickname_" + identify, nickname);
                        }
                    }
                });
            }
        } else {
            //群会话消息
            try {
                long id = Long.parseLong(identify.replace("IM", ""));
//                requestGroupProfile(id, context);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //无语了,这里腾讯云不返回群成员的昵称和头像
//            TIMGroupManagerExt.getInstance().getGroupMembers(identify, new TIMValueCallBack<List<TIMGroupMemberInfo>>() {
//                @Override
//                public void onError(int i, String s) {
//                }
//
//                @Override
//                public void onSuccess(List<TIMGroupMemberInfo> timGroupMemberInfos) {
//                    if (timGroupMemberInfos != null && timGroupMemberInfos.size() > 0) {
//                        for (TIMGroupMemberInfo MemberInfo : timGroupMemberInfos) {
//                            AiiUtil.putString(context, "avatar_" + MemberInfo.getUser(), MemberInfo.getUser());
//                            AiiUtil.putString(context, "nickname_" + MemberInfo.getUser(), MemberInfo.getNameCard());
//                        }
//                    }
//                }
//            });
        }
        return "";
    }

    //这里有个问题报用户在别处登录,直接就跳转到登录界面了
//    private void requestGroupProfile(long groupId, final Context context) {
//        GroupDetailsRequestQuery query = new GroupDetailsRequestQuery();
//        query.setId(groupId);
//        query.setAction(AIIAction.ONE);
//        query.setDir("sns");
//        App.Companion.getAiiRequest().send(query, new AIIResponse<GroupDetailsResponseQuery>(context, false) {
//            @Override
//            public void onSuccess(GroupDetailsResponseQuery response, int index) {
//                super.onSuccess(response, index);
//                Group group = response.getGroup();
//                if (group != null) {
//                    if (group.getUsers() != null && group.getUsers().size() > 0) {
//                        for (User user : group.getUsers()) {
//                            String identify = Constants.INSTANCE.getIM_PREFIX() + user.getId();
//                            AiiUtil.putString(context, "avatar_" + identify, user.getImagePath());
//                            AiiUtil.putString(context, "nickname_" + identify, user.getNickname());
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onServiceError(String content, int status, int index) {
////                super.onServiceError(content, status, index);
//            }
//
//            @Override
//            public void onLoginOut(int status) {
////                super.onLoginOut(status);
//            }
//        });
//    }


    /**
     * 跳转到聊天界面或会话详情
     *
     * @param context 跳转上下文
     */

    abstract public void navToDetail(Context context);

    /**
     * 获取最后一条消息摘要
     */
    abstract public String getLastMessageSummary();

    /**
     * 获取名称
     */
    abstract public String getName();


    public String getIdentify() {
        return identify;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Conversation that = (Conversation) o;
        return identify.equals(that.identify) && type == that.type;

    }

    @Override
    public int hashCode() {
        int result = identify.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }


    /**
     * Compares this object to the specified object to determine their relative
     * order.
     *
     * @param another the object to compare to this instance.
     * @return a negative integer if this instance is less than {@code another};
     * a positive integer if this instance is greater than
     * {@code another}; 0 if this instance has the same order as
     * {@code another}.
     * @throws ClassCastException if {@code another} cannot be converted into something
     *                            comparable to {@code this} instance.
     */
    @Override
    public int compareTo(Object another) {
        if (another instanceof Conversation) {
            Conversation anotherConversation = (Conversation) another;
            long timeGap = anotherConversation.getLastMessageTime() - getLastMessageTime();
            if (timeGap > 0) {
                return 1;
            } else if (timeGap < 0) {
                return -1;
            }
            return 0;
        } else {
            throw new ClassCastException();
        }
    }

    public TIMConversationType getType() {
        return type;
    }
}
