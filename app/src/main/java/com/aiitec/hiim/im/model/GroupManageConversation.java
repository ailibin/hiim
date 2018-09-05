package com.aiitec.hiim.im.model;

import android.content.Context;
import android.util.Log;

import com.aiitec.imlibrary.presentation.presenter.GroupManagerPresenter;
import com.aiitec.hiim.R;
import com.aiitec.hiim.base.App;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.ext.group.TIMGroupPendencyItem;

import java.util.Calendar;

/**
 * 群管理会话
 */
public class GroupManageConversation extends Conversation {

    private final String TAG = "GroupManageConversation";

    private TIMGroupPendencyItem lastMessage;

    private long unreadCount;


    public GroupManageConversation(TIMGroupPendencyItem message) {
        lastMessage = message;
    }


    /**
     * 获取最后一条消息的时间
     */
    @Override
    public long getLastMessageTime() {
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
        //不能传入最后一条消息时间，由于消息时间戳的单位是秒
        GroupManagerPresenter.readGroupManageMessage(Calendar.getInstance().getTimeInMillis(), new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                Log.i(TAG, "read all message error,code " + i);
            }

            @Override
            public void onSuccess() {
                Log.i(TAG, "read all message succeed");
            }
        });
    }

    /**
     * 获取头像
     */
    @Override
    public int getAvatar() {
//        return R.drawable.img_default;
        return R.drawable.my_icon_default_avatar2x;
    }

    /**
     * 跳转到聊天界面或会话详情
     *
     * @param context 跳转上下文
     */
    @Override
    public void navToDetail(Context context) {
        readAllMessage();
//        Intent intent = new Intent(context, GroupManageMessageActivity.class);
//        context.startActivity(intent);
    }

    /**
     * 获取最后一条消息摘要
     */
    @Override
    public String getLastMessageSummary() {
        if (lastMessage == null) {
            return "";
        }
        String from = lastMessage.getFromUser();
        String to = lastMessage.getToUser();

        boolean isSelf = from.equals(UserInfo.getInstance().getId());
        switch (lastMessage.getPendencyType()) {
            case INVITED_BY_OTHER:
                if (isSelf) {
                    return App.Companion.getContext().getResources().getString(R.string.summary_me) +
                            App.Companion.getContext().getResources().getString(R.string.summary_group_invite) +
                            to +
                            App.Companion.getContext().getResources().getString(R.string.summary_group_add);
                } else {
                    if (to.equals(UserInfo.getInstance().getId())) {
                        return from +
                                App.Companion.getContext().getResources().getString(R.string.summary_group_invite) +
                                App.Companion.getContext().getResources().getString(R.string.summary_me) +
                                App.Companion.getContext().getResources().getString(R.string.summary_group_add);
                    } else {
                        return from +
                                App.Companion.getContext().getResources().getString(R.string.summary_group_invite) +
                                to +
                                App.Companion.getContext().getResources().getString(R.string.summary_group_add);
                    }

                }
            case APPLY_BY_SELF:
                if (isSelf) {
                    return App.Companion.getContext().getResources().getString(R.string.summary_me) +
                            App.Companion.getContext().getResources().getString(R.string.summary_group_apply) +
                            GroupInfo.getInstance().getGroupName(lastMessage.getGroupId());
                } else {
                    return from + App.Companion.getContext().getResources().getString(R.string.summary_group_apply) + GroupInfo.getInstance().getGroupName(lastMessage.getGroupId());
                }

            default:
                return "";
        }
    }

    /**
     * 获取名称
     */
    @Override
    public String getName() {
        return App.Companion.getContext().getString(R.string.conversation_system_group);
    }


    /**
     * 设置最后一条消息
     */
    public void setLastMessage(TIMGroupPendencyItem message) {
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
