package com.aiitec.hiim.im.model;

import android.content.Context;

import com.aiitec.hiim.R;
import com.aiitec.hiim.base.App;
import com.aiitec.hiim.im.chat.ChatActivity;
import com.aiitec.openapi.utils.LogUtil;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.ext.message.TIMConversationExt;

/**
 * 好友或群聊的会话
 *
 * @author ailibin
 */
public class NormalConversation extends Conversation {

    private TIMConversation conversation;

    /**
     * 最后一条消息
     */
    private Message lastMessage;

    /**
     * 设置免打扰模式(默认为:0 打扰  1 免打扰)
     */
    private int noDisturb;

    /**
     * 设置会话是否选中(长按的时候有个状态)
     *
     */
    private boolean isSelected;

    public NormalConversation(TIMConversation conversation) {
        this.conversation = conversation;
        type = conversation.getType();
        identify = conversation.getPeer();
    }

    /**
     * 获取会话
     *
     * @return
     * @author ailibin
     */
    public TIMConversation getConversation() {
        return conversation;
    }


    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setNoDisturb(int noDisturb) {
        this.noDisturb = noDisturb;
    }

    public int getNoDisturb() {
        return noDisturb;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public int getAvatar() {
        switch (type) {
            case C2C:
                return R.drawable.my_icon_default_avatar2x;
            case Group:
                return R.drawable.my_icon_default_avatar2x;
            default:
                break;
        }
        return 0;
    }

    /**
     * 跳转到聊天界面或会话详情
     *
     * @param context 跳转上下文
     */
    @Override
    public void navToDetail(Context context) {
        //这里为了测试是否能聊天,先写死一个identify
        ChatActivity.Companion.navToChat(context, identify, name, type);
        LogUtil.d("ailibin", "NormalConversation" + " identify: " + identify + " name: " + name);
    }

    /**
     * 获取最后一条消息摘要
     */
    @Override
    public String getLastMessageSummary() {
        TIMConversationExt ext = new TIMConversationExt(conversation);
        if (ext.hasDraft()) {
            TextMessage textMessage = new TextMessage(ext.getDraft());
            if (lastMessage == null || lastMessage.getMessage().timestamp() < ext.getDraft().getTimestamp()) {
                return App.Companion.getContext().getString(R.string.conversation_draft) + textMessage.getSummary();
            } else {
                return lastMessage.getSummary();
            }
        } else {
            if (lastMessage == null) {
                return "";
            }
            return lastMessage.getSummary();
        }
    }

    /**
     * 获取名称
     */
    @Override
    public String getName() {
        if (type == TIMConversationType.Group) {
            name = GroupInfo.getInstance().getGroupName(identify);
            if ("".equals(name)) {
                name = identify;
            }
        } else {
            FriendProfile profile = FriendshipInfo.getInstance().getProfile(identify);
            name = profile == null ? identify : profile.getName();
        }
        return name;
    }


    /**
     * 获取未读消息数量
     */
    @Override
    public long getUnreadNum() {
        if (conversation == null) {
            return 0;
        }
        TIMConversationExt ext = new TIMConversationExt(conversation);
        return ext.getUnreadMessageNum();
    }

    /**
     * 将所有消息标记为已读
     */
    @Override
    public void readAllMessage() {
        if (conversation != null) {
            TIMConversationExt ext = new TIMConversationExt(conversation);
            ext.setReadMessage(null, null);
        }
    }


    /**
     * 获取最后一条消息的时间
     */
    @Override
    public long getLastMessageTime() {
        TIMConversationExt ext = new TIMConversationExt(conversation);
        if (ext.hasDraft()) {
            if (lastMessage == null || lastMessage.getMessage().timestamp() < ext.getDraft().getTimestamp()) {
                return ext.getDraft().getTimestamp();
            } else {
                return lastMessage.getMessage().timestamp();
            }
        }
        if (lastMessage == null) {
            return 0;
        }
        return lastMessage.getMessage().timestamp();
    }

    /**
     * 获取会话类型
     */
    @Override
    public TIMConversationType getType() {
        return conversation.getType();
    }
}
