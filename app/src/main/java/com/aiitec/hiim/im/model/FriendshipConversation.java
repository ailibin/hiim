package com.aiitec.hiim.im.model;

import android.content.Context;
import android.content.Intent;

import com.aiitec.hiim.R;
import com.aiitec.hiim.base.App;
import com.aiitec.hiim.im.chat.NewFriendActivity;
import com.tencent.imsdk.ext.sns.TIMFriendFutureItem;

/**
 * 新朋友会话
 *
 * @author ailibin
 */
public class FriendshipConversation extends Conversation {

    private TIMFriendFutureItem lastMessage;

    private long unreadCount;

    private boolean isSelected;

    public FriendshipConversation(TIMFriendFutureItem message) {
        lastMessage = message;
    }


    /**
     * 获取最后一条消息的时间
     */
    @Override
    public long getLastMessageTime() {
        if (lastMessage == null) {
            return 0;
        }
        return lastMessage.getAddTime();
    }

    /**
     * 获取未读消息数量
     */
    @Override
    public long getUnreadNum() {
        return unreadCount;
    }

    /**
     * 将所有消息标记为已读
     */
    @Override
    public void readAllMessage() {

    }


    /**
     * 获取头像
     */
    @Override
    public int getAvatar() {
        return R.drawable.my_icon_default_avatar2x;
    }

    /**
     * 跳转到聊天界面或会话详情
     *
     * @param context 跳转上下文
     */
    @Override
    public void navToDetail(Context context) {
//        Intent intent = new Intent(context, FriendshipManageMessageActivity.class);
//        context.startActivity(intent);
        //这里还是跳转到新朋友界面
        context.startActivity(new Intent(context, NewFriendActivity.class));
    }

    /**
     * 获取最后一条消息摘要
     */
    @Override
    public String getLastMessageSummary() {
        if (lastMessage == null) {
            return "";
        }
        String name = lastMessage.getProfile().getNickName();
        if ("".equals(name)) {
            name = lastMessage.getIdentifier();
        }
        switch (lastMessage.getType()) {
            //我收到的好友申请的未决消息
            case TIM_FUTURE_FRIEND_PENDENCY_IN_TYPE:
                return name + App.Companion.getContext().getString(R.string.summary_friend_add);
            //我发出的好友申请的未决消息
            case TIM_FUTURE_FRIEND_PENDENCY_OUT_TYPE:
                return App.Companion.getContext().getString(R.string.summary_me) + App.Companion.getContext().getString(R.string.summary_friend_add_me) + name;
            //已决消息
            case TIM_FUTURE_FRIEND_DECIDE_TYPE:
                return App.Companion.getContext().getString(R.string.summary_friend_added) + name;
            //好友推荐
            case TIM_FUTURE_FRIEND_RECOMMEND_TYPE:
                return App.Companion.getContext().getString(R.string.summary_friend_recommend) + name;
            default:
                return "";
        }
    }

    /**
     * 获取名称
     */
    @Override
    public String getName() {
        return App.Companion.getContext().getString(R.string.conversation_system_friend);

    }


    /**
     * 设置最后一条消息
     */
    public void setLastMessage(TIMFriendFutureItem message) {
        lastMessage = message;
    }


    /**
     * 设置未读数量
     *
     * @param count 未读数量
     */
    public void setUnreadCount(long count) {
        unreadCount = count;
    }


}
